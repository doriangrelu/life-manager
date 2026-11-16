import { apiRequest } from './http'
import type { UserProfile } from './types'

/** Profil de l'utilisateur connecté (endpoint maison, remplace le « userinfo » de Keycloak). */
export function whoami(): Promise<UserProfile> {
  return apiRequest('GET', '/whoami')
}
