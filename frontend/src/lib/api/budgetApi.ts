import { apiRequest } from './http'
import type { Category, Expense, ExpenseInput, MonthlyBudget } from './types'

const base = (householdId: string) => `/households/${householdId}`

export function listCategories(householdId: string): Promise<Category[]> {
  return apiRequest('GET', `${base(householdId)}/expense-categories`)
}

export function addCategory(householdId: string, name: string): Promise<Category> {
  return apiRequest('POST', `${base(householdId)}/expense-categories`, { name })
}

export function renameCategory(householdId: string, categoryId: string, name: string): Promise<Category> {
  return apiRequest('PUT', `${base(householdId)}/expense-categories/${categoryId}`, { name })
}

export function archiveCategory(householdId: string, categoryId: string): Promise<Category> {
  return apiRequest('POST', `${base(householdId)}/expense-categories/${categoryId}/archive`)
}

export function reactivateCategory(householdId: string, categoryId: string): Promise<Category> {
  return apiRequest('POST', `${base(householdId)}/expense-categories/${categoryId}/reactivate`)
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

/** Vue d'un mois, ex. month = « 2026-11 ». */
export function monthlyBudget(householdId: string, month: string): Promise<MonthlyBudget> {
  return apiRequest('GET', `${base(householdId)}/budget/${month}`)
}
