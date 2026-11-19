import { describe, expect, it } from 'vitest'
import { legalPageOf } from './legal'

describe('legalPageOf', () => {
  it('reconnaît les deux pages légales', () => {
    expect(legalPageOf('#/cgu')).toBe('cgu')
    expect(legalPageOf('#/confidentialite')).toBe('confidentialite')
  })

  it("ignore les écrans de l'application", () => {
    expect(legalPageOf('#/budget')).toBeNull()
    expect(legalPageOf('')).toBeNull()
  })
})
