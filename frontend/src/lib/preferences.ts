// Préférences propres à l'appareil. Le stockage peut être indisponible (navigation privée…) : jamais bloquant.

const SELECTED_HOUSEHOLD_KEY = 'life-manager.selected-household'

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
