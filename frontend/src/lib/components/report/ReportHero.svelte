<script lang="ts">
  import { PiggyBank, TrendingDown } from '@lucide/svelte'
  import type { BudgetReport } from '../../api/types'
  import { formatCents } from '../../money'
  import type { Totals } from '../../report'

  // Résultat de la période (rentré − dépensé) : positif = mis de côté, négatif = dans le rouge.
  // À côté du réalisé, le prévu en fin de période quand elle n'est pas finie.
  let { report, projected }: { report: BudgetReport; projected: Totals | null } = $props()

  const positive = $derived(report.resultCents >= 0)
  const signed = (cents: number) => `${cents >= 0 ? '+' : '−'}${formatCents(Math.abs(cents))}`
</script>

<section class="hero" aria-label="Résultat de la période">
  <span class="caption">Résultat de la période</span>
  <div class="result">
    <span class="total amount">{signed(report.resultCents)}</span>
    <span class="trend" class:negative={!positive}>
      {#if positive}
        <PiggyBank size={16} strokeWidth={2.6} /> Mis de côté
      {:else}
        <TrendingDown size={16} strokeWidth={2.6} /> Dans le rouge
      {/if}
    </span>
  </div>

  <table>
    <thead>
      <tr>
        <th scope="col"><span class="sr-only">Poste</span></th>
        <th scope="col">Réalisé</th>
        {#if projected}<th scope="col">Prévu fin de période</th>{/if}
      </tr>
    </thead>
    <tbody>
      <tr>
        <th scope="row">Rentrées</th>
        <td class="amount">{formatCents(report.incomeCents)}</td>
        {#if projected}<td class="amount">{formatCents(projected.incomeCents)}</td>{/if}
      </tr>
      <tr>
        <th scope="row">Dépenses</th>
        <td class="amount">{formatCents(report.expenseCents)}</td>
        {#if projected}<td class="amount">{formatCents(projected.expenseCents)}</td>{/if}
      </tr>
      <tr class="sum">
        <th scope="row">Résultat</th>
        <td class="amount">{signed(report.resultCents)}</td>
        {#if projected}<td class="amount">{signed(projected.resultCents)}</td>{/if}
      </tr>
    </tbody>
  </table>
</section>

<style>
  .hero {
    display: grid;
    gap: 14px;
    padding: 20px;
    border-radius: var(--radius-xl);
    background: var(--color-primary);
    color: var(--color-on-primary);
  }
  .caption {
    font-size: 0.82rem;
    font-weight: 600;
  }
  .result {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px 12px;
    margin-top: -10px;
  }
  .total {
    font-size: 2.4rem;
    letter-spacing: -0.02em;
  }
  .trend {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 12px;
    border-radius: 999px;
    background: var(--color-success-soft);
    color: var(--color-on-success-soft);
    font-size: 0.82rem;
    font-weight: 800;
  }
  .trend.negative {
    background: var(--color-danger-soft);
    color: var(--color-danger);
  }
  /* Tableau réalisé / prévu, sur un fond légèrement éclairci. */
  table {
    width: 100%;
    border-collapse: collapse;
    border-radius: var(--radius-md);
    background: rgb(255 255 255 / 0.12);
    overflow: hidden;
    font-size: 0.92rem;
  }
  th,
  td {
    padding: 8px 12px;
    text-align: right;
  }
  th:first-child {
    text-align: left;
  }
  thead th {
    font-size: 0.75rem;
    font-weight: 700;
    opacity: 0.9;
  }
  tbody th {
    font-weight: 600;
  }
  .sum {
    border-top: 1px solid rgb(255 255 255 / 0.3);
    font-weight: 800;
  }
  .sum th {
    font-weight: 800;
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
