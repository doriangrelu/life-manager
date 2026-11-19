import { apiRequest } from './http'
import type { PersonalReminder, PersonalReminderInput, Reminder } from './types'

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
