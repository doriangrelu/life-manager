import { describe, expect, it } from 'vitest'
import type { Expense, Income } from './api/types'
import { operations } from './operations'

const expense = (id: string, spentOn: string): Expense => ({
  id,
  amountCents: 100,
  categoryId: 'c',
  spentOn,
  paidBy: 'm',
  beneficiary: null,
  label: null,
  projectId: null,
  offBudget: false,
})
const income = (id: string, receivedOn: string): Income => ({
  id,
  amountCents: 100,
  categoryId: 'k',
  receivedOn,
  receivedBy: null,
  label: null,
})

describe('opérations du mois', () => {
  const expenses = [expense('courses', '2026-11-27'), expense('essence', '2026-11-10')]
  const incomes = [income('salaire', '2026-11-28'), income('prime', '2026-11-10')]

  it('mêle dépenses et rentrées, la plus récente en premier', () => {
    expect(operations(expenses, incomes).map((o) => o.id)).toEqual(['salaire', 'courses', 'prime', 'essence'])
  })

  it('filtre les dépenses ou les rentrées', () => {
    expect(operations(expenses, incomes, 'incomes').map((o) => o.id)).toEqual(['salaire', 'prime'])
    expect(operations(expenses, incomes, 'expenses').map((o) => o.id)).toEqual(['courses', 'essence'])
  })
})
