import { describe, expect, it } from 'vitest'
import type { BudgetReport, ForecastItem } from './api/types'
import { hasFuture, monthsOf, periodDays, periodLabel, periodOf, projected, shiftAnchor } from './report'

const report = (incomeCents: number, expenseCents: number): BudgetReport => ({
  from: '2026-10',
  to: '2026-12',
  incomeCents,
  expenseCents,
  resultCents: incomeCents - expenseCents,
  incomeByCategory: [],
  incomeByReceiver: [],
  expenseByCategory: [],
  months: [{ month: '2026-11', incomeCents, expenseCents, resultCents: incomeCents - expenseCents }],
  recurringExpenseCents: 0,
  monthPlans: [],
  monthExpenses: [],
})

const item = (direction: ForecastItem['direction'], expectedCents: number): ForecastItem => ({
  occurrenceId: null,
  chargeId: 'c',
  name: 'x',
  dueOn: '2026-11-28',
  expectedCents,
  status: 'PLANNED',
  direction,
  budgetCategoryId: 'k',
})

describe('bilan', () => {
  it('trouve la période qui contient un mois', () => {
    expect(periodOf('month', '2026-11')).toEqual({ from: '2026-11', to: '2026-11' })
    expect(periodOf('quarter', '2026-11')).toEqual({ from: '2026-10', to: '2026-12' })
    expect(periodOf('quarter', '2026-01')).toEqual({ from: '2026-01', to: '2026-03' })
    expect(periodOf('year', '2026-11')).toEqual({ from: '2026-01', to: '2026-12' })
    expect(periodOf('rolling', '2026-11')).toEqual({ from: '2025-12', to: '2026-11' })
  })

  it('passe à la période précédente ou suivante', () => {
    expect(shiftAnchor('month', '2026-01', -1)).toBe('2025-12')
    expect(shiftAnchor('quarter', '2026-11', 1)).toBe('2027-02')
    expect(shiftAnchor('year', '2026-11', -1)).toBe('2025-11')
  })

  it('nomme la période en français', () => {
    expect(periodLabel('quarter', periodOf('quarter', '2026-02'))).toBe('1er trimestre 2026')
    expect(periodLabel('quarter', periodOf('quarter', '2026-11'))).toBe('4e trimestre 2026')
    expect(periodLabel('year', periodOf('year', '2026-11'))).toBe('2026')
    expect(periodLabel('month', periodOf('month', '2026-11'))).toBe('novembre 2026')
  })

  it('donne les jours extrêmes de la période, février compris', () => {
    expect(periodDays({ from: '2026-01', to: '2026-02' })).toEqual({ from: '2026-01-01', to: '2026-02-28' })
    expect(periodDays({ from: '2028-02', to: '2028-02' }).to).toBe('2028-02-29')
  })

  it('ajoute au réalisé les échéances encore attendues', () => {
    const totals = projected(report(100_000, 80_000), [item('INCOME', 245_000), item('EXPENSE', 3_999)])

    expect(totals).toEqual({ incomeCents: 345_000, expenseCents: 83_999, resultCents: 261_001 })
  })

  it('ne parle de prévu que pour une période pas encore finie', () => {
    expect(hasFuture({ from: '2026-10', to: '2026-12' }, '2026-11')).toBe(true)
    expect(hasFuture({ from: '2026-01', to: '2026-03' }, '2026-11')).toBe(false)
  })

  it('complète les mois sans mouvement', () => {
    expect(monthsOf(report(10, 5)).map((m) => `${m.month} ${m.resultCents}`)).toEqual([
      '2026-10 0',
      '2026-11 5',
      '2026-12 0',
    ])
  })
})
