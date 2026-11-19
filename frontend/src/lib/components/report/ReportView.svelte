<script lang="ts">
  import { CalendarCheck, ChevronLeft, ChevronRight } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { scale } from 'svelte/transition'
  import { forecast as loadForecast } from '../../api/billsApi'
  import { budgetReport, listCategories } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import type { BudgetReport, Category, ForecastItem, Member } from '../../api/types'
  import { receiverName } from '../../members'
  import { currentMonth } from '../../month'
  import { ms, rise } from '../../motion'
  import { budgetAdherence, restToLive } from '../../plan'
  import {
    PERIOD_KINDS,
    hasFuture,
    periodDays,
    periodLabel,
    periodOf,
    projected,
    shiftAnchor,
    type PeriodKind,
  } from '../../report'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import BudgetAdherence from './BudgetAdherence.svelte'
  import ExpectedList from './ExpectedList.svelte'
  import MonthBars from './MonthBars.svelte'
  import ReportBreakdown from './ReportBreakdown.svelte'
  import ReportHero from './ReportHero.svelte'

  // Écran Bilan : ce qui est rentré, dépensé et mis de côté sur une période ; réalisé et prévu en fin de période.
  let { householdId }: { householdId: string } = $props()

  let kind = $state<PeriodKind>('month')
  let anchor = $state(currentMonth())
  let report = $state<BudgetReport | null>(null)
  let expected = $state<ForecastItem[]>([])
  let members = $state<Member[]>([])
  let categories = $state<Category[]>([])
  // Deux vues : le résultat de la période, ou le respect des budgets prévus.
  let view = $state<'result' | 'budgets'>('result')
  let loading = $state(false)
  let error = $state<string | null>(null)

  const period = $derived(periodOf(kind, anchor))
  const withForecast = $derived(hasFuture(period, currentMonth()))
  const forecastTotals = $derived(report && withForecast ? projected(report, expected) : null)
  // Reste à vivre de la période : rentrées prévues moins dépenses récurrentes (payées et à venir).
  const rest = $derived(report ? restToLive(report, expected) : null)
  // Respect des budgets : une ligne par catégorie prévue sur la période (vide = onglet masqué).
  const adherence = $derived(report ? budgetAdherence(report, categories, currentMonth()) : [])
  const shownView = $derived(adherence.length > 0 ? view : 'result')

  onMount(async () => {
    try {
      const [household, loadedCategories] = await Promise.all([getHousehold(householdId), listCategories(householdId)])
      members = household.members
      categories = loadedCategories
    } catch {
      // Sans les noms, le bilan reste lisible (« Ancien membre »).
    }
  })

  // Chaque changement de période recharge le réalisé, et le prévu si la période n'est pas finie.
  $effect(() => {
    const shown = period
    const future = withForecast
    load(shown, future)
  })

  async function load(shown: typeof period, future: boolean) {
    loading = true
    error = null
    try {
      const days = periodDays(shown)
      const [loaded, items] = await Promise.all([
        budgetReport(householdId, shown.from, shown.to),
        future ? loadForecast(householdId, days.from, days.to) : Promise.resolve([]),
      ])
      // Réponse d'une période quittée entre-temps : ignorée.
      if (shown.from === period.from && shown.to === period.to) {
        report = loaded
        expected = items
      }
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      loading = false
    }
  }

</script>

<div class="stack" in:rise>
  <h1>Bilan</h1>

  <Segmented label="Période" hideLabel options={PERIOD_KINDS} bind:value={kind} />

  <div class="period-nav">
    <button class="ghost icon" aria-label="Période précédente" onclick={() => (anchor = shiftAnchor(kind, anchor, -1))}>
      <ChevronLeft size={20} strokeWidth={2.4} />
    </button>
    <div class="period-title">
      <h2>{periodLabel(kind, period)}</h2>
      {#if anchor !== currentMonth()}
        <button class="today soft" transition:scale={{ duration: ms(150), start: 0.8 }} onclick={() => (anchor = currentMonth())}>
          <CalendarCheck size={15} strokeWidth={2.4} /> Aujourd'hui
        </button>
      {/if}
    </div>
    <button class="ghost icon" aria-label="Période suivante" onclick={() => (anchor = shiftAnchor(kind, anchor, 1))}>
      <ChevronRight size={20} strokeWidth={2.4} />
    </button>
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if report}
    <div class="stack" class:reloading={loading} aria-busy={loading}>
      {#if adherence.length > 0}
        <Segmented
          label="Vue du bilan"
          hideLabel
          options={[
            { value: 'result', label: 'Résultat' },
            { value: 'budgets', label: 'Budgets' },
          ]}
          bind:value={view}
        />
      {/if}
      {#if shownView === 'budgets'}
        <BudgetAdherence lines={adherence} />
      {:else}
      <ReportHero {report} projected={forecastTotals} {rest} />
      {#if forecastTotals}
        <ExpectedList items={expected} />
      {/if}
      {#if report.from !== report.to}
        <MonthBars {report} />
      {/if}
      <ReportBreakdown {report} receiverName={(id) => receiverName(members, id)} />
      {/if}
    </div>
  {:else if !error}
    <Skeleton variant="hero" />
    <Skeleton variant="rows" count={4} />
  {/if}
</div>

<style>
  .period-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  }
  .period-title {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
  }
  .period-title h2 {
    font-size: 1.1rem;
    text-align: center;
  }
  /* Majuscule au premier mot seulement (« 4e trimestre », « déc. 2025 – nov. 2026 »). */
  .period-title h2::first-letter {
    text-transform: uppercase;
  }
  .today {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 32px;
    padding: 0 12px;
    border-radius: 999px;
    font-size: 0.8rem;
    font-weight: 800;
  }
</style>
