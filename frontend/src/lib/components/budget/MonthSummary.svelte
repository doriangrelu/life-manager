<script lang="ts">
  import type { Category, Expense, MonthlyBudget } from '../../api/types'
  import { formatCents } from '../../money'

  // Synthèse d'un mois : total, répartitions et liste des dépenses (avec modification / suppression).
  let {
    budget,
    categories,
    memberName,
    onedit,
    ondelete,
  }: {
    budget: MonthlyBudget
    categories: Category[]
    memberName: (memberId: string | null) => string
    onedit: (expense: Expense) => void
    ondelete: (expense: Expense) => void
  } = $props()

  const categoryName = (categoryId: string) => categories.find((c) => c.id === categoryId)?.name ?? 'Catégorie'
  const dayLabel = (day: string) =>
    new Date(`${day}T12:00:00`).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric' })

  function confirmDelete(expense: Expense) {
    if (confirm(`Supprimer définitivement la dépense de ${formatCents(expense.amountCents)} ?`)) {
      ondelete(expense)
    }
  }
</script>

<div class="card total">
  <span class="muted">Total du mois</span>
  <strong>{formatCents(budget.totalCents)}</strong>
</div>

{#if budget.expenses.length === 0}
  <p class="muted">Aucune dépense ce mois-ci.</p>
{:else}
  <div class="card stack">
    <h2>Par catégorie</h2>
    <ul class="totals">
      {#each budget.byCategory as total (total.categoryId)}
        <li><span>{total.name}</span><span>{formatCents(total.totalCents)}</span></li>
      {/each}
    </ul>
  </div>

  <div class="card persons">
    <div class="stack">
      <h2>Payé par</h2>
      <ul class="totals">
        {#each budget.byPayer as total (total.memberId)}
          <li><span>{memberName(total.memberId)}</span><span>{formatCents(total.totalCents)}</span></li>
        {/each}
      </ul>
    </div>
    <div class="stack">
      <h2>Pour</h2>
      <ul class="totals">
        {#each budget.byBeneficiary as total (total.memberId ?? 'commun')}
          <li><span>{memberName(total.memberId)}</span><span>{formatCents(total.totalCents)}</span></li>
        {/each}
      </ul>
    </div>
  </div>

  <div class="card stack">
    <h2>Dépenses</h2>
    <ul class="expenses">
      {#each budget.expenses as expense (expense.id)}
        <li>
          <div class="expense-main">
            <strong>{formatCents(expense.amountCents)}</strong>
            <span>{categoryName(expense.categoryId)}{expense.label ? ` · ${expense.label}` : ''}</span>
          </div>
          <div class="muted expense-meta">
            {dayLabel(expense.spentOn)} · {memberName(expense.paidBy)} → {memberName(expense.beneficiary)}
          </div>
          <div class="expense-actions">
            <button class="link" onclick={() => onedit(expense)}>Modifier</button>
            <button class="link" onclick={() => confirmDelete(expense)}>Supprimer</button>
          </div>
        </li>
      {/each}
    </ul>
  </div>
{/if}

<style>
  .total {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }
  .total strong {
    font-size: 1.6rem;
  }
  .totals,
  .expenses {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .totals li {
    display: flex;
    justify-content: space-between;
    gap: 12px;
  }
  .persons {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  .expenses li {
    display: grid;
    gap: 2px;
    padding-bottom: 8px;
    border-bottom: 1px solid var(--border);
  }
  .expense-main {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  .expense-meta {
    font-size: 0.9rem;
  }
  .expense-actions {
    display: flex;
    gap: 16px;
  }
</style>
