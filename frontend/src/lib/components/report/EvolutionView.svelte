<script lang="ts">
  import type { BudgetReport, Category } from '../../api/types'
  import {
    averageSpent,
    chartCategories,
    longMonth,
    monthFlows,
    overMonths,
    shortMonth,
    spendSeries,
  } from '../../evolution'
  import { formatCents } from '../../money'
  import { displayName } from '../../text'
  import Chip from '../../ui/atoms/Chip.svelte'
  import MonthChart from '../../ui/organisms/MonthChart.svelte'

  // Onglet « Évolution » du Bilan : rentrées, dépenses et épargne cumulée mois par mois,
  // puis les dépenses face au prévu (toutes les catégories prévues, ou une seule au choix).
  let { report, categories, currentMonth }: { report: BudgetReport; categories: Category[]; currentMonth: string } = $props()

  const signed = (cents: number) => `${cents >= 0 ? '+' : '−'}${formatCents(Math.abs(cents))}`

  // ---- Rentrées et dépenses
  const flows = $derived(monthFlows(report, currentMonth))
  const withIncome = $derived(flows.some((m) => m.incomeCents > 0))
  let flowSelected = $state<number | null>(null)
  const flowDetail = $derived(flowSelected === null ? null : flows[flowSelected])
  const lastSavings = $derived([...flows].reverse().find((m) => m.savingsCents !== null)?.savingsCents ?? 0)

  // ---- Dépenses face au prévu
  const names = $derived(new Map(categories.map((c) => [c.id, c.name])))
  const choices = $derived(chartCategories(report, names))
  let categoryId = $state<string | null>(null)
  const series = $derived(spendSeries(report, categoryId, currentMonth))
  const withPlan = $derived(series.some((m) => m.plannedCents !== null))
  const over = $derived(overMonths(series))
  let spendSelected = $state<number | null>(null)
  const spendDetail = $derived(spendSelected === null ? null : series[spendSelected])
  const isOver = (i: number) => {
    const m = series[i]
    return m.plannedCents !== null && m.spentCents > m.plannedCents
  }
</script>

<section class="card stack">
  <h2>{withIncome ? 'Rentrées et dépenses' : 'Dépenses'}</h2>
  <MonthChart
    months={flows.map((m) => m.month)}
    bars={[
      ...(withIncome ? [{ name: 'Rentrées', values: flows.map((m) => m.incomeCents), colorAt: () => 'var(--color-success)' }] : []),
      { name: 'Dépenses', values: flows.map((m) => m.expenseCents), colorAt: () => 'var(--color-danger)' },
    ]}
    line={withIncome ? { values: flows.map((m) => m.savingsCents), color: 'var(--color-primary)' } : null}
    bind:selected={flowSelected}
    label="{withIncome ? 'Rentrées et dépenses' : 'Dépenses'} de chaque mois de la période"
  />
  <div class="legend">
    {#if withIncome}<span><i class="dot income"></i> Rentrées</span>{/if}
    <span><i class="dot expense"></i> Dépenses</span>
    {#if withIncome}<span><i class="stroke"></i> Épargne cumulée : <strong>{signed(lastSavings)}</strong></span>{/if}
  </div>
  <p class="detail" aria-live="polite">
    {#if flowDetail}
      <strong>{longMonth(flowDetail.month)}</strong> :
      {#if withIncome}rentré {formatCents(flowDetail.incomeCents)}, {/if}dépensé {formatCents(flowDetail.expenseCents)}
      {#if flowDetail.savingsCents !== null && withIncome}, épargne cumulée {signed(flowDetail.savingsCents)}{/if}
      {#if flowDetail.future}(mois à venir){/if}
    {:else}
      Touchez un mois pour voir ses chiffres.
    {/if}
  </p>
</section>

<section class="card stack">
  <h2>{withPlan ? 'Dépenses face au prévu' : 'Dépenses par catégorie'}</h2>
  <div class="chips" role="group" aria-label="Catégorie affichée">
    <Chip selected={categoryId === null} onclick={() => (categoryId = null)}>Tout</Chip>
    {#each choices as choice (choice.id)}
      <Chip selected={categoryId === choice.id} onclick={() => (categoryId = choice.id)}>{displayName(choice.name)}</Chip>
    {/each}
  </div>
  <MonthChart
    months={series.map((m) => m.month)}
    bars={[{ name: 'Dépensé', values: series.map((m) => m.spentCents), colorAt: (i) => (isOver(i) ? 'var(--color-danger)' : 'var(--color-primary)') }]}
    marks={withPlan ? series.map((m) => m.plannedCents) : null}
    bind:selected={spendSelected}
    label="Dépenses de chaque mois{withPlan ? ', comparées au prévu' : ''}"
  />
  <div class="legend">
    <span><i class="dot spent"></i> Dépensé</span>
    {#if withPlan}<span><i class="dash"></i> Prévu</span>{/if}
    <span>Moyenne : <strong>{formatCents(averageSpent(series))}</strong> par mois</span>
  </div>
  {#if categoryId === null && withPlan}
    <p class="muted note">Catégories qui ont un budget prévu.</p>
  {/if}
  <p class="detail" aria-live="polite">
    {#if spendDetail}
      <strong>{longMonth(spendDetail.month)}</strong> : dépensé {formatCents(spendDetail.spentCents)}
      {#if spendDetail.plannedCents !== null}sur {formatCents(spendDetail.plannedCents)} prévus{/if}
      {#if spendDetail.future}(mois à venir){/if}
    {:else if over.length > 0}
      {over.length} mois au-dessus du prévu : {over.map(shortMonth).join(', ')}.
    {:else if withPlan}
      Aucun mois au-dessus du prévu.
    {:else}
      Touchez un mois pour voir ses chiffres.
    {/if}
  </p>
</section>

<style>
  .legend {
    display: flex;
    flex-wrap: wrap;
    gap: 6px 14px;
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--color-text-muted);
  }
  .legend span {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }
  .dot {
    width: 10px;
    height: 10px;
    border-radius: 3px;
  }
  .income {
    background: var(--color-success);
  }
  .expense {
    background: var(--color-danger);
  }
  .spent {
    background: var(--color-primary);
  }
  .stroke {
    width: 14px;
    height: 3px;
    border-radius: 2px;
    background: var(--color-primary);
  }
  .dash {
    width: 14px;
    border-top: 2px dashed var(--color-text-muted);
  }
  .chips {
    display: flex;
    gap: 8px;
    overflow-x: auto;
    padding-bottom: 2px;
  }
  .detail {
    margin: 0;
    min-height: 1.4em;
    font-size: 0.88rem;
  }
  .note {
    margin: -6px 0 0;
    font-size: 0.8rem;
  }
</style>
