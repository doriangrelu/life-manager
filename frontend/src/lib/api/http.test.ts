import { describe, expect, it, vi } from 'vitest'

vi.mock('../auth/auth', () => ({ accessToken: async () => 'jeton', login: async () => undefined }))

const { apiRequest, readableMessage } = await import('./http')

describe('messages d\'erreur', () => {
  it('reprend le message de l\'API pour une demande refusée', () => {
    expect(readableMessage(409, { _embedded: { errors: [{ message: 'Ce nom existe déjà' }] } })).toBe('Ce nom existe déjà')
  })

  it('cache le texte technique d\'une panne du serveur', () => {
    expect(readableMessage(500, { message: 'Internal Server Error' })).toBe(
      'Le serveur a rencontré un problème. Réessayez dans un instant.',
    )
  })

  it('garde le message clair d\'un service indisponible', () => {
    expect(readableMessage(503, { message: "Le fournisseur d'identité est indisponible" })).toBe(
      "Le fournisseur d'identité est indisponible",
    )
  })

  it('remplace « Failed to fetch » quand le serveur est injoignable', async () => {
    vi.stubGlobal('fetch', async () => {
      throw new TypeError('Failed to fetch')
    })
    await expect(apiRequest('GET', '/households')).rejects.toThrow(
      'Serveur injoignable : vérifiez votre connexion, puis réessayez.',
    )
    vi.unstubAllGlobals()
  })
})
