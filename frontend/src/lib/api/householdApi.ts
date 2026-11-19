import { personName } from '../text'
import { apiRequest } from './http'
import type { HouseholdDetails, HouseholdSummary, Invitation } from './types'

export function listHouseholds(): Promise<HouseholdSummary[]> {
  return apiRequest('GET', '/households')
}

export function createHousehold(name: string): Promise<HouseholdSummary> {
  return apiRequest('POST', '/households', { name })
}

/** Le foyer et ses membres, noms des membres mis en forme (« dorian grelu » → « Dorian Grelu »). */
export async function getHousehold(householdId: string): Promise<HouseholdDetails> {
  const household: HouseholdDetails = await apiRequest('GET', `/households/${householdId}`)
  return { ...household, members: household.members.map((m) => ({ ...m, displayName: personName(m.displayName) })) }
}

/** Renomme le foyer (propriétaire). */
export function renameHousehold(householdId: string, name: string): Promise<HouseholdDetails> {
  return apiRequest('PUT', `/households/${householdId}`, { name })
}

/** Supprime définitivement le foyer et toutes ses données (propriétaire, nom retapé). */
export function deleteHousehold(householdId: string, confirmName: string): Promise<void> {
  return apiRequest('DELETE', `/households/${householdId}`, { confirmName })
}

/** Le propriétaire passe la main à un autre membre. */
export function transferOwnership(householdId: string, memberId: string): Promise<void> {
  return apiRequest('PUT', `/households/${householdId}/owner`, { memberId })
}

/** Le propriétaire retire un membre, qui perd aussitôt l'accès au foyer. */
export function removeMember(householdId: string, memberId: string): Promise<void> {
  return apiRequest('DELETE', `/households/${householdId}/members/${memberId}`)
}

/** Le membre connecté quitte le foyer (pas le propriétaire). */
export function leaveHousehold(householdId: string): Promise<void> {
  return apiRequest('POST', `/households/${householdId}/leave`)
}

/** Active ou désactive le suivi des rentrées et le bilan (propriétaire). */
export function setIncomeTracking(householdId: string, incomeTracking: boolean): Promise<void> {
  return apiRequest('PUT', `/households/${householdId}/settings`, { incomeTracking })
}

/** Annule le code d'invitation en cours (propriétaire). */
export function cancelInvitation(householdId: string): Promise<void> {
  return apiRequest('DELETE', `/households/${householdId}/invitations`)
}

/** Nouveau code d'invitation (propriétaire) ; le code précédent ne fonctionne plus. */
export function issueInvitation(householdId: string): Promise<Invitation> {
  return apiRequest('POST', `/households/${householdId}/invitations`)
}

export function joinHousehold(code: string): Promise<HouseholdSummary> {
  return apiRequest('POST', '/households/join', { code })
}
