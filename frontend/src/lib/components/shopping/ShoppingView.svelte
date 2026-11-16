<script lang="ts">
  import { onMount } from 'svelte'
  import {
    addShoppingItem,
    clearCheckedItems,
    listShoppingItems,
    removeShoppingItem,
    setChecked,
  } from '../../api/shoppingApi'
  import type { ShoppingItem } from '../../api/types'
  import { REFRESH_MS, toggled } from '../../shopping'

  // Onglet Courses : liste partagée du foyer, pensée pour une main en magasin.
  // Synchro : rechargement toutes les 5 s quand l'écran est visible, et dès qu'il le redevient.
  let { householdId }: { householdId: string } = $props()

  let items = $state<ShoppingItem[]>([])
  let label = $state('')
  let error = $state<string | null>(null)
  let pending = 0 // actions en cours : on ne remplace pas la liste sous les doigts de l'utilisateur
  let field: HTMLInputElement | undefined = $state()
  const checkedCount = $derived(items.filter((i) => i.checked).length)

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

  async function refresh() {
    if (pending > 0) {
      return
    }
    try {
      const latest = await listShoppingItems(householdId)
      if (pending === 0) {
        items = latest
      }
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Exécute un geste, puis recharge la liste pour revenir à l'état du serveur.
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
    if (text) {
      label = ''
      field?.focus()
      await act(() => addShoppingItem(householdId, text))
    }
  }

  function toggle(item: ShoppingItem) {
    const checked = !item.checked
    items = toggled(items, item.id, checked)
    act(() => setChecked(householdId, item.id, checked))
  }

  function remove(item: ShoppingItem) {
    items = items.filter((i) => i.id !== item.id)
    act(() => removeShoppingItem(householdId, item.id))
  }

  function clearChecked() {
    if (confirm(`Retirer les ${checkedCount} article(s) coché(s) ?`)) {
      items = items.filter((i) => !i.checked)
      act(() => clearCheckedItems(householdId))
    }
  }
</script>

<section class="stack">
  <h1>Courses</h1>

  <form class="add" onsubmit={add}>
    <input bind:this={field} bind:value={label} maxlength="60" placeholder="Ajouter un article" aria-label="Article" />
    <button disabled={!label.trim()}>Ajouter</button>
  </form>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if items.length === 0}
    <p class="muted">La liste est vide.</p>
  {:else}
    <ul class="card list">
      {#each items as item (item.id)}
        <li class:checked={item.checked}>
          <label>
            <input type="checkbox" checked={item.checked} onchange={() => toggle(item)} />
            <span>{item.label}</span>
          </label>
          <button class="link" aria-label="Supprimer {item.label}" onclick={() => remove(item)}>✕</button>
        </li>
      {/each}
    </ul>
  {/if}

  {#if checkedCount > 0}
    <button class="secondary" onclick={clearChecked}>Retirer les articles cochés ({checkedCount})</button>
  {/if}
</section>

<style>
  .add {
    display: flex;
    gap: 8px;
  }
  .add input {
    flex: 1;
    min-width: 0;
  }
  .list {
    list-style: none;
    margin: 0;
    padding: 4px 16px;
  }
  .list li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    border-bottom: 1px solid var(--border);
  }
  .list li:last-child {
    border-bottom: none;
  }
  .list label {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
    padding: 12px 0;
    font-size: 1.05rem;
    cursor: pointer;
  }
  .list input[type='checkbox'] {
    width: 24px;
    height: 24px;
    accent-color: var(--brand);
  }
  .checked span {
    text-decoration: line-through;
    color: var(--muted);
  }
  .list .link {
    text-decoration: none;
    color: var(--muted);
    padding: 8px;
  }
</style>
