<script lang="ts">
  import type { BudgetReport } from '../../api/types'
  import { formatCents } from '../../money'
  import { monthsOf } from '../../report'

  // Résultat de chaque mois de la période : barre verte vers le haut si positif, rouge vers le bas sinon.
  let { report }: { report: BudgetReport } = $props()

  const months = $derived(monthsOf(report))
  const largest = $derived(Math.max(1, ...months.map((m) => Math.abs(m.resultCents))))
  const short = (month: string) => {
    const [year, monthNumber] = month.split('-').map(Number)
    return new Date(year, monthNumber - 1, 1).toLocaleDateString('fr-FR', { month: 'short' }).replace('.', '')
  }
  const signed = (cents: number) => `${cents >= 0 ? '+' : '−'}${formatCents(Math.abs(cents))}`
</script>

<section class="card stack">
  <h2>Mois par mois</h2>
  <div class="chart" style:--count={months.length}>
    {#each months as month (month.month)}
      {@const ratio = Math.abs(month.resultCents) / largest}
      <div class="column" title="{short(month.month)} : {signed(month.resultCents)}">
        <div class="half up">
          {#if month.resultCents > 0}<span class="bar positive" style:height="{ratio * 100}%"></span>{/if}
        </div>
        <div class="half down">
          {#if month.resultCents < 0}<span class="bar negative" style:height="{ratio * 100}%"></span>{/if}
        </div>
        <span class="label">{short(month.month)}</span>
      </div>
    {/each}
  </div>
  <!-- Les mêmes chiffres en texte, pour les lecteurs d'écran. -->
  <ul class="sr-only">
    {#each months as month (month.month)}
      <li>{short(month.month)} : {signed(month.resultCents)}</li>
    {/each}
  </ul>
</section>

<style>
  .chart {
    display: grid;
    grid-template-columns: repeat(var(--count), minmax(0, 1fr));
    gap: 4px;
    height: 160px;
  }
  .column {
    display: grid;
    grid-template-rows: 1fr 1fr auto;
    min-width: 0;
  }
  .half {
    display: flex;
    justify-content: center;
  }
  .up {
    align-items: flex-end;
    border-bottom: 1px solid var(--color-border);
  }
  .down {
    align-items: flex-start;
  }
  .bar {
    width: min(70%, 28px);
    min-height: 2px;
  }
  .bar.positive {
    background: var(--color-success);
    border-radius: 4px 4px 0 0;
  }
  .bar.negative {
    background: var(--color-danger);
    border-radius: 0 0 4px 4px;
  }
  .label {
    padding-top: 4px;
    font-size: 0.7rem;
    font-weight: 600;
    color: var(--color-text-muted);
    text-align: center;
    overflow: hidden;
    white-space: nowrap;
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
