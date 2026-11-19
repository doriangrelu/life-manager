// Bilan : périodes (mois, trimestre, année, 12 derniers mois) et « prévu en fin de période ».
// Le réalisé vient de l'API du budget ; le prévu y ajoute les échéances récurrentes encore attendues.

import type { BudgetReport, ForecastItem } from './api/types'
import { monthLabel, shiftMonth } from './month'

export type PeriodKind = 'month' | 'quarter' | 'year' | 'rolling'

export const PERIOD_KINDS: { value: PeriodKind; label: string }[] = [
  { value: 'month', label: 'Mois' },
  { value: 'quarter', label: 'Trimestre' },
  { value: 'year', label: 'Année' },
  { value: 'rolling', label: '12 mois' },
]

/** Premier et dernier mois inclus (« 2026-10 »). */
export interface Period {
  from: string
  to: string
}

/** Période d'un type qui contient le mois `anchor` (pour « 12 mois » : les 12 mois qui finissent par lui). */
export function periodOf(kind: PeriodKind, anchor: string): Period {
  const [year, month] = anchor.split('-').map(Number)
  switch (kind) {
    case 'month':
      return { from: anchor, to: anchor }
    case 'quarter': {
      const first = month - ((month - 1) % 3)
      const from = `${year}-${String(first).padStart(2, '0')}`
      return { from, to: shiftMonth(from, 2) }
    }
    case 'year':
      return { from: `${year}-01`, to: `${year}-12` }
    case 'rolling':
      return { from: shiftMonth(anchor, -11), to: anchor }
  }
}

/** Mois de référence de la période précédente (delta = -1) ou suivante (delta = 1). */
export function shiftAnchor(kind: PeriodKind, anchor: string, delta: number): string {
  const step = { month: 1, quarter: 3, year: 12, rolling: 12 }[kind]
  return shiftMonth(anchor, step * delta)
}

/** « novembre 2026 », « 4e trimestre 2026 », « 2026 », « déc. 2025 – nov. 2026 ». */
export function periodLabel(kind: PeriodKind, period: Period): string {
  const year = period.from.slice(0, 4)
  switch (kind) {
    case 'month':
      return monthLabel(period.from)
    case 'quarter': {
      const quarter = Math.floor((Number(period.from.slice(5)) - 1) / 3) + 1
      return `${quarter}${quarter === 1 ? 'er' : 'e'} trimestre ${year}`
    }
    case 'year':
      return year
    case 'rolling':
      return `${shortMonth(period.from)} – ${shortMonth(period.to)}`
  }
}

/** Premier et dernier jour d'une période (« 2026-10-01 », « 2026-12-31 »), pour les prévisions des charges. */
export function periodDays(period: Period): { from: string; to: string } {
  const [year, month] = period.to.split('-').map(Number)
  const lastDay = new Date(year, month, 0).getDate()
  return { from: `${period.from}-01`, to: `${period.to}-${String(lastDay).padStart(2, '0')}` }
}

/** Vrai si la période n'est pas entièrement passée : le prévu a alors un sens. */
export function hasFuture(period: Period, currentMonth: string): boolean {
  return period.to >= currentMonth
}

export interface Totals {
  incomeCents: number
  expenseCents: number
  resultCents: number
}

/** Prévu en fin de période : réalisé + échéances encore attendues (rentrées et dépenses, au montant prévu). */
export function projected(report: BudgetReport, forecast: ForecastItem[]): Totals {
  const sum = (direction: ForecastItem['direction']) =>
    forecast.filter((item) => item.direction === direction).reduce((total, item) => total + item.expectedCents, 0)
  const incomeCents = report.incomeCents + sum('INCOME')
  const expenseCents = report.expenseCents + sum('EXPENSE')
  return { incomeCents, expenseCents, resultCents: incomeCents - expenseCents }
}

/** Mois de la période, dans l'ordre, chacun avec ses chiffres (0 pour un mois sans mouvement). */
export function monthsOf(report: BudgetReport): BudgetReport['months'] {
  const months: BudgetReport['months'] = []
  for (let month = report.from; month <= report.to; month = shiftMonth(month, 1)) {
    months.push(report.months.find((m) => m.month === month) ?? { month, incomeCents: 0, expenseCents: 0, resultCents: 0 })
  }
  return months
}

function shortMonth(month: string): string {
  const [year, monthNumber] = month.split('-').map(Number)
  return new Date(year, monthNumber - 1, 1).toLocaleDateString('fr-FR', { month: 'short', year: 'numeric' })
}
