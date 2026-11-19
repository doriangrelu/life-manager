import { apiRequest } from './http'
import type { BudgetReport, Category, CategoryPlanInput, Expense, ExpenseInput, FlowDirection, Income, IncomeInput, MonthlyBudget } from './types'

const base = (householdId: string) => `/households/${householdId}`

// Catégories de dépenses ou de rentrées : mêmes actions, adresses différentes.
const categoriesOf = (householdId: string, kind: FlowDirection) =>
  `${base(householdId)}/${kind === 'INCOME' ? 'income' : 'expense'}-categories`

/** Catégories de dépenses (par défaut) ou de rentrées (suggestions créées au premier affichage). */
export function listCategories(householdId: string, kind: FlowDirection = 'EXPENSE'): Promise<Category[]> {
  return apiRequest('GET', categoriesOf(householdId, kind))
}

export function addCategory(householdId: string, name: string, kind: FlowDirection = 'EXPENSE'): Promise<Category> {
  return apiRequest('POST', categoriesOf(householdId, kind), { name })
}

export function renameCategory(
  householdId: string,
  categoryId: string,
  name: string,
  kind: FlowDirection = 'EXPENSE',
): Promise<Category> {
  return apiRequest('PUT', `${categoriesOf(householdId, kind)}/${categoryId}`, { name })
}

export function archiveCategory(householdId: string, categoryId: string, kind: FlowDirection = 'EXPENSE'): Promise<Category> {
  return apiRequest('POST', `${categoriesOf(householdId, kind)}/${categoryId}/archive`)
}

export function reactivateCategory(
  householdId: string,
  categoryId: string,
  kind: FlowDirection = 'EXPENSE',
): Promise<Category> {
  return apiRequest('POST', `${categoriesOf(householdId, kind)}/${categoryId}/reactivate`)
}

/** Fixe ou retire le budget prévu d'une catégorie de dépenses. */
export function setCategoryPlan(householdId: string, categoryId: string, plan: CategoryPlanInput): Promise<void> {
  return apiRequest('PUT', `${categoriesOf(householdId, 'EXPENSE')}/${categoryId}/budget`, plan)
}

export function recordExpense(householdId: string, expense: ExpenseInput): Promise<Expense> {
  return apiRequest('POST', `${base(householdId)}/expenses`, expense)
}

export function correctExpense(householdId: string, expenseId: string, expense: ExpenseInput): Promise<Expense> {
  return apiRequest('PUT', `${base(householdId)}/expenses/${expenseId}`, expense)
}

export function deleteExpense(householdId: string, expenseId: string): Promise<void> {
  return apiRequest('DELETE', `${base(householdId)}/expenses/${expenseId}`)
}

export function recordIncome(householdId: string, income: IncomeInput): Promise<Income> {
  return apiRequest('POST', `${base(householdId)}/incomes`, income)
}

export function correctIncome(householdId: string, incomeId: string, income: IncomeInput): Promise<Income> {
  return apiRequest('PUT', `${base(householdId)}/incomes/${incomeId}`, income)
}

export function deleteIncome(householdId: string, incomeId: string): Promise<void> {
  return apiRequest('DELETE', `${base(householdId)}/incomes/${incomeId}`)
}

/** Vue d'un mois, ex. month = « 2026-11 ». */
export function monthlyBudget(householdId: string, month: string): Promise<MonthlyBudget> {
  return apiRequest('GET', `${base(householdId)}/budget/${month}`)
}

/** Bilan réalisé de from à to inclus (mois « 2026-10 »), 24 mois au plus. */
export function budgetReport(householdId: string, from: string, to: string): Promise<BudgetReport> {
  return apiRequest('GET', `${base(householdId)}/report?from=${from}&to=${to}`)
}
