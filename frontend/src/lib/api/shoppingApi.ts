import { apiRequest } from './http'
import type { PurchaseHistoryEntry, ShoppingItem, ShoppingList } from './types'

const lists = (householdId: string) => `/households/${householdId}/shopping-lists`
const items = (householdId: string, listId: string) => `${lists(householdId)}/${listId}/items`
const history = (householdId: string, listId: string) => `${lists(householdId)}/${listId}/history`

// --- Sous-listes ---

/** Sous-listes du foyer, triées par nom, avec leur nombre d'articles à acheter. */
export function listShoppingLists(householdId: string): Promise<ShoppingList[]> {
  return apiRequest('GET', lists(householdId))
}

export function addShoppingList(householdId: string, name: string): Promise<ShoppingList> {
  return apiRequest('POST', lists(householdId), { name })
}

export function renameShoppingList(householdId: string, listId: string, name: string): Promise<ShoppingList> {
  return apiRequest('PUT', `${lists(householdId)}/${listId}`, { name })
}

/** Supprime la sous-liste avec ses articles et son historique. */
export function deleteShoppingList(householdId: string, listId: string): Promise<void> {
  return apiRequest('DELETE', `${lists(householdId)}/${listId}`)
}

/** Retire les articles cochés et les inscrit à l'historique de la sous-liste. */
export function clearCheckedItems(householdId: string, listId: string): Promise<void> {
  return apiRequest('POST', `${lists(householdId)}/${listId}/clear-checked`)
}

// --- Articles d'une sous-liste ---

export function listShoppingItems(householdId: string, listId: string): Promise<ShoppingItem[]> {
  return apiRequest('GET', items(householdId, listId))
}

export function addShoppingItem(householdId: string, listId: string, label: string): Promise<ShoppingItem> {
  return apiRequest('POST', items(householdId, listId), { label })
}

/** Coche (checked = true) ou décoche un article ; sans effet s'il est déjà dans cet état. */
export function setChecked(householdId: string, listId: string, itemId: string, checked: boolean): Promise<ShoppingItem> {
  return apiRequest('POST', `${items(householdId, listId)}/${itemId}/${checked ? 'check' : 'uncheck'}`)
}

export function removeShoppingItem(householdId: string, listId: string, itemId: string): Promise<void> {
  return apiRequest('DELETE', `${items(householdId, listId)}/${itemId}`)
}

// --- Historique des achats ---

/** Du plus récemment acheté au plus ancien. */
export function listPurchaseHistory(householdId: string, listId: string): Promise<PurchaseHistoryEntry[]> {
  return apiRequest('GET', history(householdId, listId))
}

/** Remet en liste les articles choisis (tout ou rien) ; renvoie ceux ajoutés. */
export function restockFromHistory(householdId: string, listId: string, entryIds: string[]): Promise<ShoppingItem[]> {
  return apiRequest('POST', `${history(householdId, listId)}/restock`, { entryIds })
}

export function forgetHistoryEntry(householdId: string, listId: string, entryId: string): Promise<void> {
  return apiRequest('DELETE', `${history(householdId, listId)}/${entryId}`)
}
