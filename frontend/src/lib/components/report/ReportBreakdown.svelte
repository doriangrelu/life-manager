<script lang="ts">
  import type { BudgetReport, CategoryTotal } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'
  import { displayName } from '../../text'
  import ProgressBar from '../../ui/atoms/ProgressBar.svelte'

  // Détail du réalisé : rentrées par catégorie et par personne, dépenses par catégorie.
  let { report, receiverName }: { report: BudgetReport; receiverName: (id: string | null) => string } = $props()

  const largest = (totals: CategoryTotal[]) => Math.max(1, ...totals.map((t) => t.totalCents))
</script>

<section class="card stack">
  <h2>Rentrées par catégorie</h2>
  {#if report.incomeByCategory.length === 0}
    <p class="muted">Aucune rentrée sur cette période.</p>
  {:else}
    <ul class="rows">
      {#each report.incomeByCategory as category (category.categoryId)}
        <li>
          <div class="line">
            <span>{displayName(category.name)}</span>
            <span class="amount">{formatCents(category.totalCents)}</span>
          </div>
          <ProgressBar ratio={category.totalCents / largest(report.incomeByCategory)} color="var(--color-success)" />
        </li>
      {/each}
    </ul>
  {/if}
</section>

{#if report.incomeByReceiver.length > 0}
  <section class="card stack">
    <h2>Reçu par</h2>
    <ul class="rows">
      {#each report.incomeByReceiver as receiver (receiver.memberId ?? 'foyer')}
        <li>
          <div class="line">
            <span>{receiverName(receiver.memberId)}</span>
            <span class="amount">{formatCents(receiver.totalCents)}</span>
          </div>
        </li>
      {/each}
    </ul>
  </section>
{/if}

<section class="card stack">
  <h2>Dépenses par catégorie</h2>
  {#if report.expenseByCategory.length === 0}
    <p class="muted">Aucune dépense sur cette période.</p>
  {:else}
    <ul class="rows">
      {#each report.expenseByCategory as category (category.categoryId)}
        <li>
          <div class="line">
            <span>{displayName(category.name)}</span>
            <span class="amount">{formatCents(category.totalCents)}</span>
          </div>
          <ProgressBar
            ratio={category.totalCents / largest(report.expenseByCategory)}
            color={categoryStyle(category.name).bar}
          />
        </li>
      {/each}
    </ul>
  {/if}
</section>

<style>
  .rows {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 12px;
  }
  .rows li {
    display: grid;
    gap: 6px;
  }
  .line {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    font-size: 0.95rem;
    font-weight: 600;
  }
  p {
    margin: 0;
  }
</style>
