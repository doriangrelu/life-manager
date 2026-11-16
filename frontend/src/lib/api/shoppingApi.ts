import { apiRequest } from './http'
import type { ShoppingItem } from './types'

const base = (householdId: string) => `/households/${householdId}/shopping-items`

export function listShoppingItems(householdId: string): Promise<ShoppingItem[]> {
  return apiRequest('GET', base(householdId))
}

export function addShoppingItem(householdId: string, label: string): Promise<ShoppingItem> {
  return apiRequest('POST', base(householdId), { label })
}

/** Coche (checked = true) ou décoche un article ; sans effet s'il est déjà dans cet état. */
export function setChecked(householdId: string, itemId: string, checked: boolean): Promise<ShoppingItem> {
  return apiRequest('POST', `${base(householdId)}/${itemId}/${checked ? 'check' : 'uncheck'}`)
}

export function removeShoppingItem(householdId: string, itemId: string): Promise<void> {
  return apiRequest('DELETE', `${base(householdId)}/${itemId}`)
}

export function clearCheckedItems(householdId: string): Promise<void> {
  return apiRequest('POST', `${base(householdId)}/clear-checked`)
}
