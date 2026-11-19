// Budget prévu : avancement de chaque catégorie (dépensé, à venir, prévu) et reste à vivre.
// Le réalisé et le prévu viennent du budget ; les échéances à venir, des dépenses et rentrées récurrentes.

import type { Category, ForecastItem, MonthlyBudget, PlanScope } from './api/types'
import { formatCents } from './money'

/** À partir de cette part du prévu, la catégorie passe en alerte (orange). */
export const WARNING_RATIO = 0.8

/** none : pas de prévu ; ok ; warning : 80 % atteints ; over : prévu dépassé. */
export type PlanStatus = 'none' | 'ok' | 'warning' | 'over'

export interface CategoryProgress {
  categoryId: string
  name: string
  spentCents: number
  plannedCents: number | null
  scope: PlanScope | null
  /** Dépenses récurrentes encore attendues ce mois dans la catégorie. */
  upcomingCents: number
  status: PlanStatus
  /** « 86 % » en alerte, « + 34,00 € » en dépassement, sinon rien. */
  badge: string | null
}

export function planStatus(spentCents: number, plannedCents: number | null): PlanStatus {
  if (plannedCents === null) {
    return 'none'
  }
  if (spentCents > plannedCents) {
    return 'over'
  }
  return spentCents >= plannedCents * WARNING_RATIO ? 'warning' : 'ok'
}

export function planBadge(spentCents: number, plannedCents: number | null): string | null {
  switch (planStatus(spentCents, plannedCents)) {
    case 'over':
      return `+ ${formatCents(spentCents - (plannedCents ?? 0))}`
    case 'warning':
      return `${Math.round((spentCents / (plannedCents ?? 1)) * 100)} %`
    default:
      return null
  }
}

/**
 * Une ligne par catégorie qui a des dépenses ou un prévu : d'abord celles du mois (de la plus grosse
 * à la plus petite, comme le serveur), puis les catégories prévues encore sans dépense.
 */
export function categoryProgress(budget: MonthlyBudget, categories: Category[], forecast: ForecastItem[]): CategoryProgress[] {
  const planOf = new Map(budget.plans.map((plan) => [plan.categoryId, plan]))
  const upcomingOf = new Map<string, number>()
  for (const item of forecast) {
    if (item.direction === 'EXPENSE') {
      upcomingOf.set(item.budgetCategoryId, (upcomingOf.get(item.budgetCategoryId) ?? 0) + item.expectedCents)
    }
  }
  const row = (categoryId: string, name: string, spentCents: number): CategoryProgress => {
    const plan = planOf.get(categoryId)
    const plannedCents = plan?.plannedCents ?? null
    return {
      categoryId,
      name,
      spentCents,
      plannedCents,
      scope: plan?.scope ?? null,
      upcomingCents: upcomingOf.get(categoryId) ?? 0,
      status: planStatus(spentCents, plannedCents),
      badge: planBadge(spentCents, plannedCents),
    }
  }
  const spent = budget.byCategory.map((total) => row(total.categoryId, total.name, total.totalCents))
  const seen = new Set(spent.map((line) => line.categoryId))
  const plannedOnly = categories
    .filter((category) => !category.archived && planOf.has(category.id) && !seen.has(category.id))
    .map((category) => row(category.id, category.name, 0))
  return [...spent, ...plannedOnly]
}

/** Ce qu'il faut du réalisé pour le reste à vivre : un mois du budget ou une période du bilan. */
export interface Actuals {
  incomeCents: number
  expenseCents: number
  recurringExpenseCents: number
}

export interface RestToLive {
  /** Rentrées réalisées et encore attendues. */
  expectedIncomeCents: number
  /** Dépenses récurrentes payées et encore à venir. */
  recurringCents: number
  /** Reste à vivre : rentrées prévues moins dépenses récurrentes. */
  totalCents: number
  /** Dépenses courantes déjà faites (hors récurrents). */
  everydayCents: number
  /** Ce qu'il reste du reste à vivre ; négatif = dépassé. */
  leftCents: number
}

/** Reste à vivre de la période ; null tant qu'aucune rentrée n'est prévue (rien à répartir). */
export function restToLive(actuals: Actuals, forecast: ForecastItem[]): RestToLive | null {
  const upcoming = (direction: ForecastItem['direction']) =>
    forecast.filter((item) => item.direction === direction).reduce((total, item) => total + item.expectedCents, 0)
  const expectedIncomeCents = actuals.incomeCents + upcoming('INCOME')
  if (expectedIncomeCents === 0) {
    return null
  }
  const recurringCents = actuals.recurringExpenseCents + upcoming('EXPENSE')
  const totalCents = expectedIncomeCents - recurringCents
  const everydayCents = actuals.expenseCents - actuals.recurringExpenseCents
  return { expectedIncomeCents, recurringCents, totalCents, everydayCents, leftCents: totalCents - everydayCents }
}
