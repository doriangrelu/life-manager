import { UserManager, WebStorageStateStore } from 'oidc-client-ts'
import { config } from '../config'

// Connexion OIDC auprès de Keycloak (Authorization Code + PKCE, géré par oidc-client-ts).
// Le jeton d'accès est opaque pour nous : on le transmet à l'API sans jamais le lire.
const userManager = new UserManager({
  authority: config.oidcAuthority,
  client_id: config.oidcClientId,
  redirect_uri: `${window.location.origin}/`,
  post_logout_redirect_uri: `${window.location.origin}/`,
  scope: 'openid profile email',
  // Session de l'onglet uniquement : rien ne survit à la fermeture du navigateur.
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  automaticSilentRenew: true,
})

/** Si l'on revient de la page de connexion Keycloak, termine la connexion et nettoie l'URL. */
export async function completeLoginIfReturning(): Promise<void> {
  const params = new URLSearchParams(window.location.search)
  if (!params.has('code') || !params.has('state')) {
    return
  }
  await userManager.signinRedirectCallback()
  window.history.replaceState({}, '', '/')
}

/** Jeton d'accès valide, ou null si l'utilisateur doit se (re)connecter. */
export async function accessToken(): Promise<string | null> {
  const user = await userManager.getUser()
  return user && !user.expired ? user.access_token : null
}

export function login(): Promise<void> {
  return userManager.signinRedirect()
}

export async function logout(): Promise<void> {
  await userManager.signoutRedirect()
}
