// Onglet « Évolution » du Bilan : séries mois par mois tirées du bilan d'une période.
// Les mois après le mois courant sont marqués « à venir » : rien n'y est encore dépensé ni épargné.

import type { BudgetReport } from './api/types'
import { monthsOf } from './report'

/** Une série de barres du graphique mois par mois. */
export interface BarSeries {
  name: string
  values: number[]
  /** Couleur d'une barre, selon son mois (dépassement en rouge…). */
  colorAt: (index: number) => string
}

export interface MonthFlow {
  month: string
  incomeCents: number
  expenseCents: number
  /** Épargne cumulée depuis le début de la période (somme des résultats), null pour un mois à venir. */
  savingsCents: number | null
  future: boolean
}

/** Rentrées, dépenses et épargne cumulée de chaque mois de la période. */
export function monthFlows(report: BudgetReport, currentMonth: string): MonthFlow[] {
  let savings = 0
  return monthsOf(report).map((m) => {
    const future = m.month > currentMonth
    savings += future ? 0 : m.resultCents
    return { month: m.month, incomeCents: m.incomeCents, expenseCents: m.expenseCents, savingsCents: future ? null : savings, future }
  })
}

export interface MonthSpend {
  month: string
  spentCents: number
  /** Prévu du mois (null : aucun budget prévu ce mois-là). */
  plannedCents: number | null
  future: boolean
}

/**
 * Dépenses face au prévu, mois par mois.
 * `categoryId` null : toutes les catégories qui ont un budget prévu (comme l'onglet « Budgets »),
 * ou toutes les dépenses si aucun budget n'est prévu sur la période.
 */
export function spendSeries(report: BudgetReport, categoryId: string | null, currentMonth: string): MonthSpend[] {
  const plannedIds = new Set(report.monthPlans.map((p) => p.categoryId))
  const counts = (id: string) => (categoryId === null ? plannedIds.size === 0 || plannedIds.has(id) : id === categoryId)
  return monthsOf(report).map(({ month }) => {
    const plans = report.monthPlans.filter((p) => p.month === month && counts(p.categoryId))
    const spentCents = report.monthExpenses
      .filter((e) => e.month === month && counts(e.categoryId))
      .reduce((total, e) => total + e.cents, 0)
    return {
      month,
      spentCents,
      plannedCents: plans.length > 0 ? plans.reduce((total, p) => total + p.cents, 0) : null,
      future: month > currentMonth,
    }
  })
}

/** Mois écoulés où le dépensé a dépassé le prévu. */
export function overMonths(series: MonthSpend[]): string[] {
  return series.filter((m) => !m.future && m.plannedCents !== null && m.spentCents > m.plannedCents).map((m) => m.month)
}

/**
 * Dépense moyenne des mois écoulés de la période (arrondie au centime), à partir du premier mois
 * qui a une dépense ou un prévu : les mois d'avant l'usage de l'application ne font pas baisser la moyenne.
 */
export function averageSpent(series: MonthSpend[]): number {
  const start = series.findIndex((m) => m.spentCents > 0 || m.plannedCents !== null)
  const past = start < 0 ? [] : series.slice(start).filter((m) => !m.future)
  return past.length === 0 ? 0 : Math.round(past.reduce((total, m) => total + m.spentCents, 0) / past.length)
}

/** Catégories à proposer : celles qui ont des dépenses ou un prévu sur la période, les plus dépensières d'abord. */
export function chartCategories(report: BudgetReport, names: Map<string, string>): { id: string; name: string }[] {
  const ids = new Set([...report.expenseByCategory.map((c) => c.categoryId), ...report.monthPlans.map((p) => p.categoryId)])
  const spent = new Map(report.expenseByCategory.map((c) => [c.categoryId, c.totalCents]))
  return [...ids]
    .map((id) => ({ id, name: names.get(id) ?? report.expenseByCategory.find((c) => c.categoryId === id)?.name ?? 'autre' }))
    .sort((a, b) => (spent.get(b.id) ?? 0) - (spent.get(a.id) ?? 0))
}

/** « janv. », « févr. »… pour les axes. */
export function shortMonth(month: string): string {
  const [year, number] = month.split('-').map(Number)
  return new Date(year, number - 1, 1).toLocaleDateString('fr-FR', { month: 'short' })
}

/** « mars 2026 », pour le détail d'un mois. */
export function longMonth(month: string): string {
  const [year, number] = month.split('-').map(Number)
  return new Date(year, number - 1, 1).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
}
