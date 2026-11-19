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

/** Nouveau code d'invitation ; le code précédent ne fonctionne plus. */
export function issueInvitation(householdId: string): Promise<Invitation> {
  return apiRequest('POST', `/households/${householdId}/invitations`)
}

export function joinHousehold(code: string): Promise<HouseholdSummary> {
  return apiRequest('POST', '/households/join', { code })
}
