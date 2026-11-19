import { describe, expect, it } from 'vitest'
import type { Category, ForecastItem, MonthlyBudget } from './api/types'
import { categoryProgress, planBadge, planStatus, restToLive } from './plan'

const category = (id: string, name: string, archived = false): Category => ({ id, name, archived }) as Category

const upcoming = (budgetCategoryId: string, expectedCents: number, direction: 'EXPENSE' | 'INCOME' = 'EXPENSE'): ForecastItem => ({
  occurrenceId: null,
  chargeId: 'charge',
  name: 'Échéance',
  dueOn: '2026-11-28',
  expectedCents,
  status: 'PLANNED',
  direction,
  budgetCategoryId,
})

const month = (over: Partial<MonthlyBudget>): MonthlyBudget => ({
  month: '2026-11',
  totalCents: 0,
  byCategory: [],
  byPayer: [],
  byBeneficiary: [],
  expenses: [],
  incomeCents: 0,
  resultCents: 0,
  incomeByCategory: [],
  incomes: [],
  plans: [],
  recurringExpenseCents: 0,
  ...over,
})

describe('avancement d\'une catégorie', () => {
  it('passe en alerte à 80 % et en dépassement au-delà du prévu', () => {
    expect(planStatus(11900, 15000)).toBe('ok')
    expect(planStatus(12000, 15000)).toBe('warning')
    expect(planStatus(15000, 15000)).toBe('warning')
    expect(planStatus(15001, 15000)).toBe('over')
    expect(planStatus(5000, null)).toBe('none')
  })

  it('résume l\'alerte en pourcentage et le dépassement en euros', () => {
    expect(planBadge(12900, 15000)).toBe('86 %')
    expect(planBadge(13400, 10000)?.replace(/\s/g, ' ')).toBe('+ 34,00 €')
    expect(planBadge(1000, 15000)).toBeNull()
    expect(planBadge(1000, null)).toBeNull()
  })

  it('liste les catégories dépensées, puis les catégories prévues sans dépense', () => {
    const budget = month({
      byCategory: [
        { categoryId: 'home', name: 'logement', totalCents: 9200 },
        { categoryId: 'health', name: 'santé', totalCents: 2500 },
      ],
      plans: [
        { categoryId: 'home', plannedCents: 90000, scope: 'FROM_MONTH' },
        { categoryId: 'fun', plannedCents: 15000, scope: 'MONTH_ONLY' },
        { categoryId: 'old', plannedCents: 5000, scope: 'FROM_MONTH' },
      ],
    })
    const categories = [category('home', 'logement'), category('health', 'santé'), category('fun', 'loisirs'), category('old', 'ancien', true)]

    const lines = categoryProgress(budget, categories, [upcoming('home', 76000), upcoming('home', 50000, 'INCOME')])

    expect(lines.map((line) => line.name)).toEqual(['logement', 'santé', 'loisirs'])
    expect(lines[0]).toMatchObject({ spentCents: 9200, plannedCents: 90000, upcomingCents: 76000, status: 'ok' })
    expect(lines[1]).toMatchObject({ plannedCents: null, status: 'none', badge: null })
    expect(lines[2]).toMatchObject({ spentCents: 0, plannedCents: 15000, scope: 'MONTH_ONLY' })
  })
})

describe('reste à vivre', () => {
  it('retire les dépenses récurrentes des rentrées prévues, puis les dépenses courantes', () => {
    const rest = restToLive(
      { incomeCents: 245000, expenseCents: 183800, recurringExpenseCents: 100000 },
      [upcoming('salary', 75000, 'INCOME'), upcoming('home', 75000)],
    )

    expect(rest).toEqual({
      expectedIncomeCents: 320000,
      recurringCents: 175000,
      totalCents: 145000,
      everydayCents: 83800,
      leftCents: 61200,
    })
  })

  it('n\'existe pas sans rentrée prévue, et peut être dépassé', () => {
    expect(restToLive({ incomeCents: 0, expenseCents: 5000, recurringExpenseCents: 0 }, [])).toBeNull()
    expect(restToLive({ incomeCents: 10000, expenseCents: 15000, recurringExpenseCents: 0 }, [])?.leftCents).toBe(-5000)
  })
})
