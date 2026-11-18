<script lang="ts">
  import { History, Plus, Settings2, X } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import {
    addShoppingItem,
    clearCheckedItems,
    listShoppingItems,
    listShoppingLists,
    removeShoppingItem,
    setChecked,
  } from '../../api/shoppingApi'
  import type { ShoppingItem, ShoppingList } from '../../api/types'
  import { rememberedShoppingListId, rememberShoppingListId } from '../../preferences'
  import { pickList, REFRESH_MS, toggled } from '../../shopping'
  import Chip from '../../ui/Chip.svelte'
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'
  import PurchaseHistoryDialog from './PurchaseHistoryDialog.svelte'
  import ShoppingListsManager from './ShoppingListsManager.svelte'

  // Onglet Courses : sous-listes partagées du foyer (Courses, Bricolage…), pensées pour une main en magasin.
  // Synchro : rechargement toutes les 5 s quand l'écran est visible, et dès qu'il le redevient.
  let { householdId }: { householdId: string } = $props()

  let lists = $state<ShoppingList[]>([])
  let currentId = $state<string | null>(null)
  let items = $state<ShoppingItem[]>([])
  let label = $state('')
  let error = $state<string | null>(null)
  let confirmingClear = $state(false)
  let managing = $state(false)
  let showingHistory = $state(false)
  let pending = 0 // actions en cours : on ne remplace pas la liste sous les doigts de l'utilisateur
  let field: HTMLInputElement | undefined = $state()
  const current = $derived(lists.find((l) => l.id === currentId) ?? null)
  const toBuy = $derived(items.filter((i) => !i.checked))
  const inCart = $derived(items.filter((i) => i.checked))

  onMount(() => {
    refresh()
    const timer = setInterval(() => document.visibilityState === 'visible' && refresh(), REFRESH_MS)
    const onVisible = () => document.visibilityState === 'visible' && refresh()
    document.addEventListener('visibilitychange', onVisible)
    return () => {
      clearInterval(timer)
      document.removeEventListener('visibilitychange', onVisible)
    }
  })

  // Recharge les sous-listes (compteurs), puis les articles de celle affichée.
  async function refresh(selectId?: string) {
    if (pending > 0) {
      return
    }
    try {
      const latestLists = await listShoppingLists(householdId)
      const chosen = pickList(latestLists, selectId ?? currentId ?? rememberedShoppingListId(householdId))
      const latestItems = chosen ? await listShoppingItems(householdId, chosen.id) : []
      if (pending === 0) {
        lists = latestLists
        select(chosen)
        items = latestItems
      }
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  function select(list: ShoppingList | null) {
    if (list && list.id !== currentId) {
      rememberShoppingListId(householdId, list.id)
    }
    currentId = list?.id ?? null
  }

  async function open(list: ShoppingList) {
    if (list.id !== currentId) {
      error = null
      select(list)
      items = []
      await refresh(list.id)
    }
  }

  // Exécute un geste, puis recharge pour revenir à l'état du serveur.
  async function act(action: () => Promise<unknown>) {
    error = null
    pending++
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      pending--
    }
    await refresh()
  }

  async function add(event: SubmitEvent) {
    event.preventDefault()
    const text = label.trim()
    const listId = currentId
    if (text && listId) {
      label = ''
      field?.focus()
      await act(() => addShoppingItem(householdId, listId, text))
    }
  }

  function toggle(item: ShoppingItem, listId: string) {
    const checked = !item.checked
    items = toggled(items, item.id, checked)
    act(() => setChecked(householdId, listId, item.id, checked))
  }

  function remove(item: ShoppingItem, listId: string) {
    items = items.filter((i) => i.id !== item.id)
    act(() => removeShoppingItem(householdId, listId, item.id))
  }

  function clearChecked(listId: string) {
    items = items.filter((i) => !i.checked)
    act(() => clearCheckedItems(householdId, listId))
  }
</script>

{#snippet row(item: ShoppingItem, listId: string)}
  <li class:checked={item.checked}>
    <label>
      <input type="checkbox" checked={item.checked} onchange={() => toggle(item, listId)} />
      <span>{item.label}</span>
    </label>
    <button class="link icon" aria-label="Supprimer {item.label}" onclick={() => remove(item, listId)}>
      <X size={18} />
    </button>
  </li>
{/snippet}

<div class="title">
  <h1>Courses</h1>
  <div class="tools">
    <button class="soft icon" aria-label="Historique des achats" disabled={!current} onclick={() => (showingHistory = true)}>
      <History size={20} />
    </button>
    <button class="soft icon" aria-label="Gérer les sous-listes" onclick={() => (managing = true)}>
      <Settings2 size={20} />
    </button>
  </div>
</div>

<nav class="lists" aria-label="Sous-listes">
  {#each lists as list (list.id)}
    <Chip selected={list.id === currentId} onclick={() => open(list)}>
      {list.name}
      {#if list.pendingCount > 0}<span class="count">{list.pendingCount}</span>{/if}
    </Chip>
  {/each}
</nav>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if current}
  {@const listId = current.id}
  <form class="add" onsubmit={add}>
    <label class="visually-hidden" for="new-item">Nouvel article dans {current.name}</label>
    <input id="new-item" bind:this={field} bind:value={label} maxlength="60" placeholder="Ajouter à {current.name}…" />
    <button class="icon add-button" aria-label="Ajouter" disabled={!label.trim()}><Plus size={22} strokeWidth={2.6} /></button>
  </form>

  {#if items.length === 0}
    <div class="empty card">
      <p class="muted">« {current.name} » est vide.</p>
      <button class="soft" onclick={() => (showingHistory = true)}><History size={18} /> Reprendre des achats passés</button>
    </div>
  {/if}

  {#if toBuy.length > 0}
    <ul class="list" aria-label="À acheter">
      {#each toBuy as item (item.id)}
        {@render row(item, listId)}
      {/each}
    </ul>
  {/if}

  {#if inCart.length > 0}
    <section class="stack">
      <div class="section-title">
        <h2 class="muted">Dans le panier · {inCart.length}</h2>
        <button class="soft" onclick={() => (confirmingClear = true)}>Vider le panier</button>
      </div>
      <ul class="list" aria-label="Dans le panier">
        {#each inCart as item (item.id)}
          {@render row(item, listId)}
        {/each}
      </ul>
    </section>
  {/if}

  <ConfirmDialog
    bind:open={confirmingClear}
    title="Vider le panier ?"
    message="Les {inCart.length} article(s) coché(s) quittent la liste et rejoignent l'historique de « {current.name} »."
    confirmLabel="Vider"
    onconfirm={() => clearChecked(listId)}
  />

  <PurchaseHistoryDialog bind:open={showingHistory} {householdId} list={current} onrestocked={() => refresh()} />
{:else if lists.length === 0 && !error}
  <p class="muted">Aucune sous-liste : créez-en une avec le bouton de réglage.</p>
{/if}

<ShoppingListsManager bind:open={managing} {householdId} {lists} onchanged={(id) => refresh(id)} />

<style>
  .title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }
  .tools {
    display: flex;
    gap: 8px;
  }
  .lists {
    display: flex;
    gap: 8px;
    overflow-x: auto;
    padding-bottom: 4px;
    scrollbar-width: none;
  }
  .lists :global(.chip) {
    flex: none;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
  .count {
    min-width: 22px;
    height: 22px;
    padding: 0 6px;
    border-radius: 11px;
    display: inline-grid;
    place-items: center;
    font-size: 0.8rem;
    font-weight: 800;
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
  }
  .add {
    display: flex;
    gap: 8px;
  }
  .add input {
    min-height: 52px;
    border-radius: 16px;
  }
  .add-button {
    width: 52px;
    min-height: 52px;
    border-radius: 16px;
  }
  .empty {
    display: grid;
    justify-items: start;
    gap: 12px;
  }
  .empty button {
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
  .list {
    list-style: none;
    margin: 0;
    padding: 4px 0;
    background: var(--color-surface);
    border-radius: var(--radius-lg);
  }
  .list li {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 8px 0 16px;
    border-bottom: 1px solid var(--color-border);
  }
  .list li:last-child {
    border-bottom: none;
  }
  .list label {
    display: flex;
    align-items: center;
    gap: 14px;
    flex: 1;
    min-height: 56px;
    font-size: 1rem;
    font-weight: 600;
    color: var(--color-text);
    cursor: pointer;
  }
  .checked span {
    text-decoration: line-through;
    color: var(--color-text-muted);
  }
  .list .link {
    color: var(--color-text-muted);
  }
</style>
