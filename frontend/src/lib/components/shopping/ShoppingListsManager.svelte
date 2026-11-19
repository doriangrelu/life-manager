<script lang="ts">
  import { displayName } from '../../text'
  import { Pencil, Plus, Trash2 } from '@lucide/svelte'
  import { flip } from 'svelte/animate'
  import { dragHandleZone, type DndEvent } from 'svelte-dnd-action'
  import { addShoppingList, deleteShoppingList, renameShoppingList, reorderShoppingLists } from '../../api/shoppingApi'
  import type { ShoppingList } from '../../api/types'
  import { ms } from '../../motion'
  import { dndItems, dndOptions, idsOf, isAfterDrop, isShadow } from '../../reorder'
  import DragHandle from '../../ui/atoms/DragHandle.svelte'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import TextDialog from '../../ui/molecules/TextDialog.svelte'

  // Gestion des sous-listes du foyer : ordre (glisser-déposer), ajout, renommage, suppression (avec confirmation).
  let {
    open = $bindable(false),
    householdId,
    lists,
    onchanged,
  }: {
    open: boolean
    householdId: string
    lists: ShoppingList[]
    onchanged: (selectId?: string) => void
  } = $props()

  let name = $state('')
  let error = $state<string | null>(null)
  let renaming = $state<ShoppingList | null>(null)
  let deleting = $state<ShoppingList | null>(null)
  let renameOpen = $state(false)
  let deleteOpen = $state(false)

  async function run(action: () => Promise<string | undefined>) {
    error = null
    try {
      onchanged(await action())
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  function add(event: SubmitEvent) {
    event.preventDefault()
    const text = name.trim()
    if (text) {
      name = ''
      run(async () => (await addShoppingList(householdId, text)).id)
    }
  }

  // Ordre affiché : celui reçu, sauf pendant un glisser où il suit le doigt.
  let ordered = $state<ShoppingList[]>([])
  let dragging = false
  $effect(() => {
    const latest = lists
    if (!dragging) {
      ordered = latest
    }
  })

  function consider(event: CustomEvent<DndEvent<ShoppingList>>) {
    if (isAfterDrop(event)) {
      return
    }
    dragging = true
    ordered = event.detail.items
  }

  // Chaque dépôt est enregistré (au clavier, chaque flèche dépose déjà) ; renvoyer le même ordre est sans effet.
  function finalize(event: CustomEvent<DndEvent<ShoppingList>>) {
    ordered = dndItems(event)
    dragging = false
    const ids = idsOf(ordered)
    run(async () => {
      await reorderShoppingLists(householdId, ids)
      return undefined
    })
  }

  function askRename(list: ShoppingList) {
    renaming = list
    renameOpen = true
  }

  function askDelete(list: ShoppingList) {
    deleting = list
    deleteOpen = true
  }
</script>

<Modal bind:open title="Sous-listes">
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <ul class="lists" use:dragHandleZone={dndOptions(ordered, 'shopping-lists')} onconsider={consider} onfinalize={finalize}>
    {#each ordered as list (list.id)}
      <li class:shadow={isShadow(list)} animate:flip={{ duration: ms(180) }}>
        <DragHandle label="Déplacer {displayName(list.name)}" />
        <span class="name">{displayName(list.name)}</span>
        <span class="muted count">{list.pendingCount} à acheter</span>
        <button class="icon ghost" aria-label="Renommer {displayName(list.name)}" onclick={() => askRename(list)}>
          <Pencil size={18} />
        </button>
        <button class="icon ghost" aria-label="Supprimer {displayName(list.name)}" onclick={() => askDelete(list)}>
          <Trash2 size={18} />
        </button>
      </li>
    {/each}
  </ul>

  <form class="add" onsubmit={add}>
    <label class="visually-hidden" for="new-list">Nouvelle sous-liste</label>
    <input id="new-list" bind:value={name} maxlength="30" placeholder="Nouvelle sous-liste (Pharmacie…)" />
    <button class="icon" aria-label="Ajouter la sous-liste" disabled={!name.trim()}><Plus size={22} /></button>
  </form>
</Modal>

<TextDialog
  bind:open={renameOpen}
  title="Renommer la sous-liste"
  label="Nom"
  initial={renaming ? displayName(renaming.name) : ''}
  maxlength={30}
  onsubmit={(text) => renaming && run(async () => (await renameShoppingList(householdId, renaming!.id, text)).id)}
/>

<ConfirmDialog
  bind:open={deleteOpen}
  title="Supprimer « {deleting ? displayName(deleting.name) : ''} » ?"
  message="Ses articles et son historique d'achats seront supprimés aussi."
  onconfirm={() => deleting && run(async () => {
    await deleteShoppingList(householdId, deleting!.id)
    return undefined
  })}
/>

<style>
  .lists {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 4px;
  }
  .lists li {
    display: flex;
    align-items: center;
    gap: 8px;
    min-height: 52px;
    padding: 0 4px 0 14px;
    border-radius: var(--radius-md);
    background: var(--color-surface-muted);
  }
  /* Emplacement où l'élément tombera. */
  .lists li.shadow {
    opacity: 0.45;
    outline: 2px dashed var(--color-primary);
    outline-offset: -2px;
  }
  .name {
    flex: 1;
    font-weight: 700;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .count {
    font-size: 0.85rem;
    white-space: nowrap;
  }
  .lists button {
    box-shadow: none;
    background: transparent;
  }
  .add {
    display: flex;
    gap: 8px;
  }
</style>
