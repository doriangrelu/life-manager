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

// Une seule nouvelle tentative de connexion après un retour en échec : jamais de boucle de redirections.
const RETRY_KEY = 'life-manager.login-retry'

/**
 * Si l'on revient de la page de connexion Keycloak, termine la connexion et rouvre l'écran affiché
 * avant le départ (Keycloak ne garde pas la partie « # » de l'adresse).
 *
 * L'adresse de retour (?code=…&state=…) est nettoyée AVANT l'échange du code : un rechargement
 * (mise à jour de l'application, F5, Précédent) ne rejoue jamais un retour déjà consommé.
 * Un retour périmé n'est pas une erreur : session valide → on continue, sinon nouvelle connexion.
 */
export async function completeLoginIfReturning(): Promise<void> {
  const returnUrl = window.location.href
  const params = new URLSearchParams(window.location.search)
  if (!params.has('code') || !params.has('state')) {
    return
  }
  window.history.replaceState({}, '', '/')
  try {
    const user = await userManager.signinRedirectCallback(returnUrl)
    sessionStorage.removeItem(RETRY_KEY)
    navigate(parseRoute((user.state as LoginState | undefined)?.hash ?? ''), { replace: true })
  } catch {
    await userManager.clearStaleState()
    if (await accessToken()) {
      return
    }
    if (sessionStorage.getItem(RETRY_KEY)) {
      sessionStorage.removeItem(RETRY_KEY)
      throw new Error("La connexion n'a pas abouti. Fermez l'application puis rouvrez-la.")
    }
    // Aucune session : l'application relance une connexion (Keycloak reconnaît souvent l'utilisateur sans mot de passe).
    sessionStorage.setItem(RETRY_KEY, '1')
  }
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

/**
 * Console de compte Keycloak (mot de passe, nom, sessions ouvertes…). Le lien « retour » de la console
 * ramène à l'application (referrer = client de la PWA, adresse de retour autorisée par le realm).
 */
export function accountConsoleUrl(): string {
  const back = new URLSearchParams({ referrer: config.oidcClientId, referrer_uri: `${window.location.origin}/` })
  return `${config.oidcAuthority}/account?${back}`
}

export async function logout(): Promise<void> {
  await userManager.signoutRedirect()
}
