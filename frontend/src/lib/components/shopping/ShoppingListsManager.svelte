<script lang="ts">
  import { Pencil, Plus, Trash2 } from '@lucide/svelte'
  import { addShoppingList, deleteShoppingList, renameShoppingList } from '../../api/shoppingApi'
  import type { ShoppingList } from '../../api/types'
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'
  import Modal from '../../ui/Modal.svelte'
  import TextDialog from '../../ui/TextDialog.svelte'

  // Gestion des sous-listes du foyer : ajout, renommage, suppression (avec confirmation).
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

  <ul class="lists">
    {#each lists as list (list.id)}
      <li>
        <span class="name">{list.name}</span>
        <span class="muted count">{list.pendingCount} à acheter</span>
        <button class="icon ghost" aria-label="Renommer {list.name}" onclick={() => askRename(list)}>
          <Pencil size={18} />
        </button>
        <button class="icon ghost" aria-label="Supprimer {list.name}" onclick={() => askDelete(list)}>
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
  initial={renaming?.name ?? ''}
  maxlength={30}
  onsubmit={(text) => renaming && run(async () => (await renameShoppingList(householdId, renaming!.id, text)).id)}
/>

<ConfirmDialog
  bind:open={deleteOpen}
  title="Supprimer « {deleting?.name} » ?"
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
