<script lang="ts">
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import { listMove, ms, rise } from '../../motion'
  import { displayName } from '../../text'
  import type { Category, Expense } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'

  // Dépenses du mois, la plus récente en premier ; toucher une dépense l'ouvre pour la modifier.
  let {
    expenses,
    categories,
    memberName,
    onopen,
  }: {
    expenses: Expense[]
    categories: Category[]
    memberName: (id: string | null) => string
    onopen: (expense: Expense) => void
  } = $props()

  const categoryName = (categoryId: string) => displayName(categories.find((c) => c.id === categoryId)?.name ?? 'Catégorie')
  const dayLabel = (day: string) =>
    new Date(`${day}T12:00:00`).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' })
</script>

{#if expenses.length > 0}
  <section class="stack">
    <h2>Dépenses du mois</h2>
    <ul class="expenses">
      {#each expenses as expense (expense.id)}
        {@const name = categoryName(expense.categoryId)}
        {@const style = categoryStyle(name)}
        <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
          <button class="expense" onclick={() => onopen(expense)}>
            <span class="bubble" style:background={style.soft} aria-hidden="true">
              <style.icon size={20} color={style.strong} />
            </span>
            <span class="text">
              <span class="title">{expense.label ? displayName(expense.label) : name}</span>
              <span class="meta">
                {name} · {dayLabel(expense.spentOn)} · {memberName(expense.paidBy)} → {memberName(expense.beneficiary)}
              </span>
            </span>
            <span class="amount">{formatCents(expense.amountCents)}</span>
          </button>
        </li>
      {/each}
    </ul>
  </section>
{/if}

<style>
  .expenses {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .expense {
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
</style>
