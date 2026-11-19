import { describe, expect, it, vi } from 'vitest'
import { HOUSEHOLD_MAYBE_LOST, householdIdOf, reportNotFound, sameHouseholdName } from './householdAccess'

const ID = '7f1fea31-642f-4f5e-8d43-600f02ca4461'

describe('foyer inaccessible', () => {
  it('reconnaît le foyer visé par une adresse', () => {
    expect(householdIdOf(`/households/${ID}`)).toBe(ID)
    expect(householdIdOf(`/households/${ID}/charges/x`)).toBe(ID)
    expect(householdIdOf('/households/join')).toBeNull()
    expect(householdIdOf('/whoami')).toBeNull()
  })

  it('prévient l’application seulement pour une adresse de foyer', () => {
    // Les tests tournent sous Node : une simple cible d'événements tient lieu de fenêtre.
    vi.stubGlobal('window', new EventTarget())
    const listener = vi.fn()
    window.addEventListener(HOUSEHOLD_MAYBE_LOST, listener)
    reportNotFound('/whoami')
    reportNotFound(`/households/${ID}/vehicles`)
    window.removeEventListener(HOUSEHOLD_MAYBE_LOST, listener)

    expect(listener).toHaveBeenCalledTimes(1)
    expect((listener.mock.calls[0][0] as CustomEvent).detail).toBe(ID)
    vi.unstubAllGlobals()
  })

  it('compare le nom retapé comme le serveur', () => {
    expect(sameHouseholdName('  MAISON   grelu ', 'maison grelu')).toBe(true)
    expect(sameHouseholdName('maison', 'maison grelu')).toBe(false)
    expect(sameHouseholdName('  ', '')).toBe(false)
  })
})
