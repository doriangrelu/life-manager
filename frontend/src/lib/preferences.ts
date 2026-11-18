// Préférences propres à l'appareil. Le stockage peut être indisponible (navigation privée…) : jamais bloquant.

import type { NotifiedMemo } from './reminders'

const SELECTED_HOUSEHOLD_KEY = 'life-manager.selected-household'
const NOTIFIED_KEY = 'life-manager.notified-reminders'
const SHOPPING_LIST_KEY = 'life-manager.shopping-list.'

/** Rappels déjà notifiés aujourd'hui sur cet appareil. */
export function notifiedMemo(): NotifiedMemo | null {
  try {
    return JSON.parse(localStorage.getItem(NOTIFIED_KEY) ?? 'null')
  } catch {
    return null
  }
}

export function rememberNotified(memo: NotifiedMemo): void {
  try {
    localStorage.setItem(NOTIFIED_KEY, JSON.stringify(memo))
  } catch {
    // Sans stockage, un rappel pourra être notifié plusieurs fois dans la journée : sans gravité.
  }
}

export function rememberedHouseholdId(): string | null {
  try {
    return localStorage.getItem(SELECTED_HOUSEHOLD_KEY)
  } catch {
    return null
  }
}

export function rememberHouseholdId(householdId: string): void {
  try {
    localStorage.setItem(SELECTED_HOUSEHOLD_KEY, householdId)
  } catch {
    // Sans stockage, on reviendra simplement au premier foyer au prochain lancement.
  }
}

/** Dernière sous-liste de courses consultée dans ce foyer, sur cet appareil. */
export function rememberedShoppingListId(householdId: string): string | null {
  try {
    return localStorage.getItem(SHOPPING_LIST_KEY + householdId)
  } catch {
    return null
  }
}

export function rememberShoppingListId(householdId: string, listId: string): void {
  try {
    localStorage.setItem(SHOPPING_LIST_KEY + householdId, listId)
  } catch {
    // Sans stockage, on rouvrira simplement la sous-liste « Courses ».
  }
}
