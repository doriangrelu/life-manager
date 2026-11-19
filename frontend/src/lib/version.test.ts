import { describe, expect, it } from 'vitest'
import { versionText } from './version'

describe('versionText', () => {
  it('montre une seule version quand la PWA et l API sont alignées', () => {
    expect(versionText('v0.12.0', 'v0.12.0')).toBe('v0.12.0')
  })

  it("montre la version de la PWA si l'API ne répond pas", () => {
    expect(versionText('v0.12.0', null)).toBe('v0.12.0')
  })

  it('montre les deux versions juste après une livraison', () => {
    expect(versionText('v0.11.0', 'v0.12.0')).toBe('PWA v0.11.0 · API v0.12.0')
  })
})
