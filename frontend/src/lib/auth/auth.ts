import { UserManager, WebStorageStateStore } from 'oidc-client-ts'
import { config } from '../config'
import { navigate, parseRoute } from '../route'

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

/** Écran mémorisé le temps d'un aller-retour par la page de connexion. */
interface LoginState {
  hash?: string
}

/**
 * Si l'on revient de la page de connexion Keycloak, termine la connexion, nettoie l'URL
 * et rouvre l'écran affiché avant le départ (Keycloak ne garde pas la partie « # » de l'adresse).
 */
export async function completeLoginIfReturning(): Promise<void> {
  const params = new URLSearchParams(window.location.search)
  if (!params.has('code') || !params.has('state')) {
    return
  }
  const user = await userManager.signinRedirectCallback()
  window.history.replaceState({}, '', '/')
  navigate(parseRoute((user.state as LoginState | undefined)?.hash ?? ''), { replace: true })
}

/** Jeton d'accès valide, ou null si l'utilisateur doit se (re)connecter. */
export async function accessToken(): Promise<string | null> {
  const user = await userManager.getUser()
  return user && !user.expired ? user.access_token : null
}

export function login(): Promise<void> {
  const state: LoginState = { hash: window.location.hash }
  return userManager.signinRedirect({ state })
}

export async function logout(): Promise<void> {
  await userManager.signoutRedirect()
}
