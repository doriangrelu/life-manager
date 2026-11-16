import { apiRequest } from './http'
import type { HouseholdDetails, HouseholdSummary, Invitation } from './types'

export function listHouseholds(): Promise<HouseholdSummary[]> {
  return apiRequest('GET', '/households')
}

export function createHousehold(name: string): Promise<HouseholdSummary> {
  return apiRequest('POST', '/households', { name })
}

export function getHousehold(householdId: string): Promise<HouseholdDetails> {
  return apiRequest('GET', `/households/${householdId}`)
}

/** Nouveau code d'invitation ; le code précédent ne fonctionne plus. */
export function issueInvitation(householdId: string): Promise<Invitation> {
  return apiRequest('POST', `/households/${householdId}/invitations`)
}

export function joinHousehold(code: string): Promise<HouseholdSummary> {
  return apiRequest('POST', '/households/join', { code })
}
