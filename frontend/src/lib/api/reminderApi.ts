import { apiRequest } from './http'
import type { PersonalReminder, PersonalReminderInput, Reminder, ReminderDone } from './types'

/** Taille d'une page d'historique : une page pleine laisse supposer qu'il y en a d'autres. */
export const HISTORY_PAGE_SIZE = 20

const personal = (householdId: string) => `/households/${householdId}/personal-reminders`

/** Rappels du foyer : échéances passées ou proches et rappels saisis, du plus ancien au plus lointain. */
export function listReminders(householdId: string): Promise<Reminder[]> {
  return apiRequest('GET', `/households/${householdId}/reminders`)
}

/** Rappels saisis par les membres, du plus proche au plus lointain. */
export function listPersonalReminders(householdId: string): Promise<PersonalReminder[]> {
  return apiRequest('GET', personal(householdId))
}

export function addPersonalReminder(householdId: string, input: PersonalReminderInput): Promise<PersonalReminder> {
  return apiRequest('POST', personal(householdId), input)
}

export function updatePersonalReminder(
  householdId: string,
  reminderId: string,
  input: PersonalReminderInput,
): Promise<PersonalReminder> {
  return apiRequest('PUT', `${personal(householdId)}/${reminderId}`, input)
}

export function deletePersonalReminder(householdId: string, reminderId: string): Promise<void> {
  return apiRequest('DELETE', `${personal(householdId)}/${reminderId}`)
}

/** « C'est fait » : un rappel ponctuel disparaît, un rappel récurrent passe à sa date suivante. */
export function completePersonalReminder(householdId: string, reminderId: string): Promise<void> {
  return apiRequest('POST', `${personal(householdId)}/${reminderId}/done`)
}

/** Une page de l'historique des rappels faits, du plus récent au plus ancien, après `offset` lignes. */
export function listReminderHistory(householdId: string, offset = 0): Promise<ReminderDone[]> {
  return apiRequest('GET', `/households/${householdId}/reminder-history?offset=${offset}`)
}

export function removeReminderDone(householdId: string, entryId: string): Promise<void> {
  return apiRequest('DELETE', `/households/${householdId}/reminder-history/${entryId}`)
}
