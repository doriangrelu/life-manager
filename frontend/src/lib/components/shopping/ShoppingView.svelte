<script lang="ts">
  import { Plus, X } from '@lucide/svelte'
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
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'

  // Onglet Courses : liste partagée du foyer, pensée pour une main en magasin.
  // Synchro : rechargement toutes les 5 s quand l'écran est visible, et dès qu'il le redevient.
  let { householdId }: { householdId: string } = $props()

  let items = $state<ShoppingItem[]>([])
  let label = $state('')
  let error = $state<string | null>(null)
  let confirmingClear = $state(false)
  let pending = 0 // actions en cours : on ne remplace pas la liste sous les doigts de l'utilisateur
  let field: HTMLInputElement | undefined = $state()
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
    items = items.filter((i) => !i.checked)
    act(() => clearCheckedItems(householdId))
  }
</script>

{#snippet row(item: ShoppingItem)}
  <li class:checked={item.checked}>
    <label>
      <input type="checkbox" checked={item.checked} onchange={() => toggle(item)} />
      <span>{item.label}</span>
    </label>
    <button class="link icon" aria-label="Supprimer {item.label}" onclick={() => remove(item)}>
      <X size={18} />
    </button>
  </li>
{/snippet}

<h1>Courses</h1>

<form class="add" onsubmit={add}>
  <label class="visually-hidden" for="new-item">Nouvel article</label>
  <input id="new-item" bind:this={field} bind:value={label} maxlength="60" placeholder="Ajouter un article…" />
  <button class="icon add-button" aria-label="Ajouter" disabled={!label.trim()}><Plus size={22} strokeWidth={2.6} /></button>
</form>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if items.length === 0}
  <p class="muted">La liste est vide : ajoutez le premier article.</p>
{/if}

{#if toBuy.length > 0}
  <ul class="list" aria-label="À acheter">
    {#each toBuy as item (item.id)}
      {@render row(item)}
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
        {@render row(item)}
      {/each}
    </ul>
  </section>
{/if}

<ConfirmDialog
  bind:open={confirmingClear}
  title="Vider le panier ?"
  message="Les {inCart.length} article(s) coché(s) seront retirés de la liste."
  confirmLabel="Vider"
  onconfirm={clearChecked}
/>

<style>
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
