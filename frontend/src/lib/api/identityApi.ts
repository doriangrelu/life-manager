import { personName } from '../text'
import { apiRequest } from './http'
import type { UserProfile } from './types'

/** Profil de l'utilisateur connecté (endpoint maison, remplace le « userinfo » de Keycloak), nom mis en forme. */
export async function whoami(): Promise<UserProfile> {
  const profile: UserProfile = await apiRequest('GET', '/whoami')
  return { ...profile, displayName: personName(profile.displayName) }
}
