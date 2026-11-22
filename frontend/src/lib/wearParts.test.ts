import { describe, expect, it } from 'vitest'
import type { WearPart } from './api/types'
import { freeKinds, km, splitWearParts, wearPartLabel, wearRatio, wearStatus } from './wearParts'

const part = (over: Partial<WearPart>): WearPart => ({
  id: 'p',
  kind: 'TIRES_FRONT',
  label: null,
  fittedOn: '2025-01-10',
  fittedKm: 40_000,
  replaceAfterKm: null,
  removedOn: null,
  removedKm: null,
  kmDone: null,
  ...over,
})

describe('état d\'une pièce d\'usure', () => {
  it('rien sans intervalle ou sans kilométrage connu', () => {
    expect(wearStatus(part({ kmDone: 5000 }))).toBeNull()
    expect(wearStatus(part({ replaceAfterKm: 30_000 }))).toBeNull()
  })
  it('à prévoir à 90 %, à changer à 100 %', () => {
    expect(wearStatus(part({ kmDone: 26_999, replaceAfterKm: 30_000 }))).toBe('ok')
    expect(wearStatus(part({ kmDone: 27_000, replaceAfterKm: 30_000 }))).toBe('soon')
    expect(wearStatus(part({ kmDone: 30_000, replaceAfterKm: 30_000 }))).toBe('due')
    expect(wearRatio(part({ kmDone: 45_000, replaceAfterKm: 30_000 }))).toBe(1)
  })
})

describe('liste des pièces', () => {
  it('montées dans l\'ordre des types, historique le plus récent d\'abord', () => {
    const parts = [
      part({ id: 'b', kind: 'BATTERY' }),
      part({ id: 'old1', removedOn: '2024-01-01', removedKm: 30_000 }),
      part({ id: 't', kind: 'TIRES_FRONT' }),
      part({ id: 'old2', removedOn: '2025-01-10', removedKm: 40_000 }),
    ]
    const { mounted, history } = splitWearParts(parts)
    expect(mounted.map((p) => p.id)).toEqual(['t', 'b'])
    expect(history.map((p) => p.id)).toEqual(['old2', 'old1'])
    expect(freeKinds(parts)).not.toContain('TIRES_FRONT')
    expect(freeKinds(parts)).toContain('TIRES_REAR')
  })
  it('libellés et kilomètres lisibles', () => {
    expect(wearPartLabel('TIMING_BELT')).toBe('Courroie de distribution')
    expect(km(54_300).replace(/\s/g, ' ')).toBe('54 300 km')
  })
})
