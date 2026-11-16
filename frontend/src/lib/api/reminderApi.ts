import { apiRequest } from './http'
import type { Reminder } from './types'

/** Rappels du foyer : échéances passées ou dans les 30 jours, de la plus urgente à la moins urgente. */
export function listReminders(householdId: string): Promise<Reminder[]> {
  return apiRequest('GET', `/households/${householdId}/reminders`)
}
