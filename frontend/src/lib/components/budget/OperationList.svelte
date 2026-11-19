<script lang="ts">
  import { ArrowDownLeft } from '@lucide/svelte'
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import type { Category, Expense, Income } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'
  import { listMove, ms, rise } from '../../motion'
  import { OPERATION_FILTERS, operations, type OperationFilter } from '../../operations'
  import { displayName } from '../../text'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Opérations du mois (option « Rentrées et bilan ») : dépenses et rentrées mêlées, par date.
  // Une rentrée se reconnaît à sa pastille verte et à son montant vert « + ».
  let {
    expenses,
    incomes,
    categories,
    incomeCategories,
    memberName,
    receiverName,
    onopenexpense,
    onopenincome,
  }: {
    expenses: Expense[]
    incomes: Income[]
    categories: Category[]
    incomeCategories: Category[]
    memberName: (id: string | null) => string
    receiverName: (id: string | null) => string
    onopenexpense: (expense: Expense) => void
    onopenincome: (income: Income) => void
  } = $props()

  let filter = $state<OperationFilter>('all')
  const shown = $derived(operations(expenses, incomes, filter))

  const nameIn = (list: Category[], id: string, fallback: string) =>
    displayName(list.find((c) => c.id === id)?.name ?? fallback)
  const dayLabel = (day: string) =>
    new Date(`${day}T12:00:00`).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' })
</script>

{#if expenses.length + incomes.length > 0}
  <section class="stack">
    <h2>Opérations du mois</h2>
    <Segmented label="Afficher" hideLabel options={OPERATION_FILTERS} bind:value={filter} />
    {#if shown.length === 0}
      <p class="muted empty">{filter === 'incomes' ? 'Aucune rentrée ce mois-ci.' : 'Aucune dépense ce mois-ci.'}</p>
    {/if}
    <ul class="operations">
      {#each shown as operation (operation.kind + operation.id)}
        <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
          {#if operation.kind === 'income'}
            {@const income = operation.income}
            {@const name = nameIn(incomeCategories, income.categoryId, 'Rentrée')}
            <button class="operation" onclick={() => onopenincome(income)}>
              <span class="bubble income" aria-hidden="true"><ArrowDownLeft size={20} strokeWidth={2.4} /></span>
              <span class="text">
                <span class="title">{income.label ? displayName(income.label) : name}</span>
                <span class="meta">{name} · {dayLabel(income.receivedOn)} · reçu par {receiverName(income.receivedBy)}</span>
              </span>
              <span class="amount positive"><span class="sr-only">Rentrée de </span>+{formatCents(income.amountCents)}</span>
            </button>
          {:else}
            {@const expense = operation.expense}
            {@const name = nameIn(categories, expense.categoryId, 'Catégorie')}
            {@const style = categoryStyle(name)}
            <button class="operation" onclick={() => onopenexpense(expense)}>
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
          {/if}
        </li>
      {/each}
    </ul>
  </section>
{/if}

<style>
  .operations {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .operation {
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
  .bubble.income {
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
  .empty {
    margin: 0;
    text-align: center;
  }
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0 0 0 0);
    white-space: nowrap;
  }
</style>
