import { describe, expect, it } from 'vitest'
import type { BudgetReport } from './api/types'
import { averageSpent, chartCategories, monthFlows, overMonths, spendSeries } from './evolution'

const report = {
  from: '2026-09',
  to: '2026-12',
  months: [
    { month: '2026-09', incomeCents: 300000, expenseCents: 250000, resultCents: 50000 },
    { month: '2026-10', incomeCents: 300000, expenseCents: 320000, resultCents: -20000 },
    { month: '2026-11', incomeCents: 300000, expenseCents: 200000, resultCents: 100000 },
  ],
  expenseByCategory: [
    { categoryId: 'fun', name: 'loisirs', totalCents: 30000 },
    { categoryId: 'food', name: 'courses', totalCents: 120000 },
  ],
  monthPlans: ['2026-09', '2026-10', '2026-11', '2026-12'].map((month) => ({ month, categoryId: 'food', cents: 40000 })),
  monthExpenses: [
    { month: '2026-09', categoryId: 'food', cents: 38000 },
    { month: '2026-10', categoryId: 'food', cents: 45000 },
    { month: '2026-11', categoryId: 'food', cents: 37000 },
    { month: '2026-10', categoryId: 'fun', cents: 30000 },
  ],
} as BudgetReport

describe('évolution mois par mois', () => {
  it('cumule l\'épargne depuis le début de la période, sans les mois à venir', () => {
    const flows = monthFlows(report, '2026-11')

    expect(flows.map((m) => m.savingsCents)).toEqual([50000, 30000, 130000, null])
    expect(flows[3]).toMatchObject({ month: '2026-12', expenseCents: 0, future: true })
  })

  it('compare les dépenses des catégories prévues au prévu, ou une seule catégorie', () => {
    const all = spendSeries(report, null, '2026-11')
    expect(all.map((m) => m.spentCents)).toEqual([38000, 45000, 37000, 0])
    expect(all.map((m) => m.plannedCents)).toEqual([40000, 40000, 40000, 40000])
    expect(overMonths(all)).toEqual(['2026-10'])
    expect(averageSpent(all)).toBe(40000)

    const fun = spendSeries(report, 'fun', '2026-11')
    expect(fun.map((m) => m.spentCents)).toEqual([0, 30000, 0, 0])
    expect(fun.every((m) => m.plannedCents === null)).toBe(true)
    expect(overMonths(fun)).toEqual([])
    // Moyenne à partir du premier mois dépensé (octobre) : (300 + 0) / 2.
    expect(averageSpent(fun)).toBe(15000)
  })

  it('compte toutes les dépenses quand aucun budget n\'est prévu', () => {
    const noPlans = { ...report, monthPlans: [] }
    expect(spendSeries(noPlans, null, '2026-11').map((m) => m.spentCents)).toEqual([38000, 75000, 37000, 0])
  })

  it('propose les catégories dépensées ou prévues, les plus dépensières d\'abord', () => {
    expect(chartCategories(report, new Map([['food', 'courses']]))).toEqual([
      { id: 'food', name: 'courses' },
      { id: 'fun', name: 'loisirs' },
    ])
  })
})
