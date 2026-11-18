<script lang="ts">
  import { flip } from 'svelte/animate'
  import { slide } from 'svelte/transition'
  import { listMove, ms } from '../../motion'
  import { X } from '@lucide/svelte'
  import { forgetHistoryEntry, listPurchaseHistory, restockFromHistory } from '../../api/shoppingApi'
  import type { PurchaseHistoryEntry, ShoppingList } from '../../api/types'
  import { purchaseSummary } from '../../shopping'
  import { displayName } from '../../text'
  import Modal from '../../ui/Modal.svelte'
  import Skeleton from '../../ui/Skeleton.svelte'

  // Historique des achats d'une sous-liste : on coche ce qu'il faut racheter et on le remet en liste d'un coup.
  let {
    open = $bindable(false),
    householdId,
    list,
    onrestocked,
  }: {
    open: boolean
    householdId: string
    list: ShoppingList
    onrestocked: () => void
  } = $props()

  let entries = $state<PurchaseHistoryEntry[]>([])
  let selected = $state<string[]>([])
  let loading = $state(false)
  let error = $state<string | null>(null)
  const allSelected = $derived(entries.length > 0 && selected.length === entries.length)

  // À chaque ouverture : historique rechargé, sélection vide.
  $effect(() => {
    if (open) {
      selected = []
      load()
    }
  })

  async function load() {
    loading = true
    error = null
    try {
      entries = await listPurchaseHistory(householdId, list.id)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      loading = false
    }
  }

  function toggle(id: string) {
    selected = selected.includes(id) ? selected.filter((s) => s !== id) : [...selected, id]
  }

  function toggleAll() {
    selected = allSelected ? [] : entries.map((e) => e.id)
  }

  async function forget(entry: PurchaseHistoryEntry) {
    entries = entries.filter((e) => e.id !== entry.id)
    selected = selected.filter((s) => s !== entry.id)
    try {
      await forgetHistoryEntry(householdId, list.id, entry.id)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      await load()
    }
  }

  async function restock() {
    error = null
    try {
      await restockFromHistory(householdId, list.id, selected)
      open = false
      onrestocked()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      await load()
    }
  }
</script>

<Modal bind:open title="Historique · {displayName(list.name)}">
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if loading && entries.length === 0}
    <Skeleton variant="rows" count={4} />
  {:else if entries.length === 0}
    <p class="muted">
      Rien encore : les articles cochés rejoignent l'historique quand vous videz le panier.
    </p>
  {:else}
    <div class="section-title">
      <span class="muted">{entries.length} article(s) déjà acheté(s)</span>
      <button class="link" onclick={toggleAll}>{allSelected ? 'Tout désélectionner' : 'Tout sélectionner'}</button>
    </div>
    <ul class="entries" aria-label="Articles déjà achetés">
      {#each entries as entry (entry.id)}
        <li class:selected={selected.includes(entry.id)} animate:flip={listMove} out:slide={{ duration: ms(180) }}>
          <label>
            <input type="checkbox" checked={selected.includes(entry.id)} onchange={() => toggle(entry.id)} />
            <span class="text">
              <span class="label">{displayName(entry.label)}</span>
              <span class="muted summary">{purchaseSummary(entry)}</span>
            </span>
          </label>
          <button class="icon ghost" aria-label="Oublier {entry.label}" onclick={() => forget(entry)}>
            <X size={18} />
          </button>
        </li>
      {/each}
    </ul>
  {/if}

  {#snippet footer()}
    <button class="large" disabled={selected.length === 0} onclick={restock}>
      Remettre en liste{selected.length > 0 ? ` (${selected.length})` : ''}
    </button>
  {/snippet}
</Modal>

<style>
  .entries {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 4px;
  }
  .entries li {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 0 4px 0 14px;
    border-radius: var(--radius-md);
    background: var(--color-surface-muted);
  }
  .entries li.selected {
    background: var(--color-primary-soft);
  }
  .entries label {
    display: flex;
    align-items: center;
    gap: 14px;
    flex: 1;
    min-height: 56px;
    cursor: pointer;
    color: var(--color-text);
  }
  .text {
    display: grid;
  }
  .label {
    font-weight: 700;
  }
  .summary {
    font-size: 0.85rem;
  }
  .entries button {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
  }
</style>
