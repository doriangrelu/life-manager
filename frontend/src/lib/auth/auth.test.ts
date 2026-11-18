// @vitest-environment happy-dom
// Navigateur simulé : adresse, historique et sessionStorage.
import { beforeEach, describe, expect, it, vi } from 'vitest'

// UserManager simulé : on pilote l'échange du code et la session existante.
const manager = vi.hoisted(() => ({
  signinRedirectCallback: vi.fn(),
  clearStaleState: vi.fn(async () => undefined),
  getUser: vi.fn(async () => null as unknown),
  signinRedirect: vi.fn(async () => undefined),
}))

vi.mock('oidc-client-ts', () => ({
  UserManager: class {
    constructor() {
      return manager
    }
  },
  WebStorageStateStore: class {},
}))

const { completeLoginIfReturning } = await import('./auth')

function returnFromKeycloak() {
  window.history.replaceState({}, '', '/?code=abc&state=xyz')
}

describe('retour de la page de connexion', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorage.clear()
    manager.getUser.mockResolvedValue(null)
  })

  it('nettoie l\'adresse avant l\'échange du code, puis rouvre l\'écran mémorisé', async () => {
    returnFromKeycloak()
    manager.signinRedirectCallback.mockImplementation(async () => {
      // Un rechargement à cet instant ne verrait plus le code dans l'adresse.
      expect(window.location.search).toBe('')
      return { state: { hash: '#/courses' } }
    })

    await completeLoginIfReturning()

    expect(manager.signinRedirectCallback).toHaveBeenCalledWith(expect.stringContaining('code=abc'))
    expect(window.location.hash).toBe('#/courses')
  })

  it('ignore un retour déjà consommé quand la session est valide', async () => {
    returnFromKeycloak()
    manager.signinRedirectCallback.mockRejectedValue(new Error('No matching state found in storage'))
    manager.getUser.mockResolvedValue({ expired: false, access_token: 'jeton' })

    await expect(completeLoginIfReturning()).resolves.toBeUndefined()
    expect(window.location.search).toBe('')
  })

  it('laisse relancer une connexion une seule fois, puis affiche un message clair', async () => {
    manager.signinRedirectCallback.mockRejectedValue(new Error('No matching state found in storage'))

    returnFromKeycloak()
    await expect(completeLoginIfReturning()).resolves.toBeUndefined()

    returnFromKeycloak()
    await expect(completeLoginIfReturning()).rejects.toThrow("La connexion n'a pas abouti")
  })

  it('ne fait rien hors d\'un retour de connexion', async () => {
    window.history.replaceState({}, '', '/#/budget')
    await completeLoginIfReturning()
    expect(manager.signinRedirectCallback).not.toHaveBeenCalled()
  })
})
