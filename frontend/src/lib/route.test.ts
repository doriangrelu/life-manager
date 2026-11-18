import { describe, expect, it } from 'vitest'
import { formatRoute, parseRoute } from './route'

describe('adresse de l\'écran', () => {
  it('lit les adresses de chaque section', () => {
    expect(parseRoute('#/budget')).toEqual({ section: 'budget' })
    expect(parseRoute('#/budget/2026-10')).toEqual({ section: 'budget', month: '2026-10' })
    expect(parseRoute('#/courses')).toEqual({ section: 'shopping' })
    expect(parseRoute('#/vehicules')).toEqual({ section: 'vehicles' })
    expect(parseRoute('#/vehicules/abc-123')).toEqual({ section: 'vehicles', vehicleId: 'abc-123' })
    expect(parseRoute('#/foyer')).toEqual({ section: 'household' })
  })

  it('ramène une adresse absente ou inconnue au budget', () => {
    expect(parseRoute('')).toEqual({ section: 'budget' })
    expect(parseRoute('#/')).toEqual({ section: 'budget' })
    expect(parseRoute('#/inconnu')).toEqual({ section: 'budget' })
    expect(parseRoute('#/vehicules/a/b')).toEqual({ section: 'budget' })
  })

  it('ignore un mois invalide ou un paramètre inattendu', () => {
    expect(parseRoute('#/budget/2026-13')).toEqual({ section: 'budget' })
    expect(parseRoute('#/budget/octobre')).toEqual({ section: 'budget' })
    expect(parseRoute('#/courses/x')).toEqual({ section: 'shopping' })
  })

  it('écrit l\'adresse d\'un écran, relue à l\'identique', () => {
    const routes = [
      { section: 'budget', month: '2026-10' },
      { section: 'shopping' },
      { section: 'vehicles', vehicleId: 'abc-123' },
      { section: 'household' },
    ] as const
    for (const route of routes) {
      expect(parseRoute(formatRoute(route))).toEqual(route)
    }
    expect(formatRoute({ section: 'budget', month: '2026-10' })).toBe('#/budget/2026-10')
    expect(formatRoute({ section: 'household', month: '2026-10' })).toBe('#/foyer')
  })
})
