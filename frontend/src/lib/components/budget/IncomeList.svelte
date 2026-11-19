<script lang="ts">
  import { ArrowDownLeft } from '@lucide/svelte'
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import type { Category, Income } from '../../api/types'
  import { formatCents } from '../../money'
  import { listMove, ms, rise } from '../../motion'
  import { displayName } from '../../text'

  // Rentrées du mois, la plus récente en premier ; toucher une rentrée l'ouvre pour la modifier.
  let {
    incomes,
    categories,
    receiverName,
    onopen,
  }: {
    incomes: Income[]
    categories: Category[]
    receiverName: (id: string | null) => string
    onopen: (income: Income) => void
  } = $props()

  const categoryName = (categoryId: string) => displayName(categories.find((c) => c.id === categoryId)?.name ?? 'Rentrée')
  const dayLabel = (day: string) =>
    new Date(`${day}T12:00:00`).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' })
</script>

{#if incomes.length > 0}
  <section class="stack">
    <h2>Rentrées du mois</h2>
    <ul class="incomes">
      {#each incomes as income (income.id)}
        {@const name = categoryName(income.categoryId)}
        <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
          <button class="income" onclick={() => onopen(income)}>
            <span class="bubble" aria-hidden="true"><ArrowDownLeft size={20} strokeWidth={2.4} /></span>
            <span class="text">
              <span class="title">{income.label ? displayName(income.label) : name}</span>
              <span class="meta">{name} · {dayLabel(income.receivedOn)} · reçu par {receiverName(income.receivedBy)}</span>
            </span>
            <span class="amount positive">+{formatCents(income.amountCents)}</span>
          </button>
        </li>
      {/each}
    </ul>
  </section>
{/if}

<style>
  .incomes {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .income {
    width: 100%;
    justify-content: flex-start;
    gap: 12px;
    padding: 12px;
    min-height: 66px;
    border-radius: 18px;
    background: var(--color-surface);
    color: var(--color-text);
    text-align: left;
  }
  .bubble {
    width: 42px;
    height: 42px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    background: var(--color-success-soft);
    color: var(--color-on-success-soft);
  }
  .text {
    display: grid;
    flex: 1;
    min-width: 0;
  }
  .title {
    font-weight: 800;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .meta {
    font-size: 0.82rem;
    font-weight: 500;
    color: var(--color-text-muted);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .positive {
    color: var(--color-on-success-soft);
  }
</style>
