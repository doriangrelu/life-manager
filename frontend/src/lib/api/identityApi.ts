import { personName } from '../text'
import { apiBlob, apiRequest } from './http'
import type { UserProfile } from './types'

/** Profil de l'utilisateur connecté (endpoint maison, remplace le « userinfo » de Keycloak), nom mis en forme. */
export async function whoami(): Promise<UserProfile> {
  const profile: UserProfile = await apiRequest('GET', '/whoami')
  return { ...profile, displayName: personName(profile.displayName) }
}

/** Accepte les conditions d'utilisation lues (leur version). */
export function acceptTerms(version: string): Promise<void> {
  return apiRequest('PUT', '/me/terms', { version })
}

/** Données saisies par l'utilisateur, en JSON (un export par minute au plus). */
export function exportMyData(): Promise<Blob> {
  return apiBlob('/me/export')
}

/** Supprime le compte ; l'e-mail retapé confirme. */
export function deleteMyAccount(confirmEmail: string): Promise<void> {
  return apiRequest('DELETE', '/me', { confirmEmail })
}
