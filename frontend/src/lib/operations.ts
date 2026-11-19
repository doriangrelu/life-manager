// Opérations du mois : dépenses et rentrées dans une seule liste, la plus récente en premier.
import type { Expense, Income } from './api/types'

export type OperationFilter = 'all' | 'expenses' | 'incomes'

export const OPERATION_FILTERS: { value: OperationFilter; label: string }[] = [
  { value: 'all', label: 'Tout' },
  { value: 'expenses', label: 'Dépenses' },
  { value: 'incomes', label: 'Rentrées' },
]

export type Operation =
  | { kind: 'expense'; id: string; day: string; expense: Expense }
  | { kind: 'income'; id: string; day: string; income: Income }

/**
 * Mêle dépenses et rentrées, triées par jour décroissant. À jour égal, l'ordre reçu de l'API est gardé
 * (le plus récemment saisi d'abord) et les rentrées passent avant les dépenses.
 */
export function operations(expenses: Expense[], incomes: Income[], filter: OperationFilter = 'all'): Operation[] {
  const fromIncomes: Operation[] =
    filter === 'expenses' ? [] : incomes.map((income) => ({ kind: 'income', id: income.id, day: income.receivedOn, income }))
  const fromExpenses: Operation[] =
    filter === 'incomes' ? [] : expenses.map((expense) => ({ kind: 'expense', id: expense.id, day: expense.spentOn, expense }))
  // Tri stable : l'ordre d'origine départage les égalités.
  return [...fromIncomes, ...fromExpenses].sort((a, b) => b.day.localeCompare(a.day))
}
