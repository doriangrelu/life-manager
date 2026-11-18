import { describe, expect, it } from 'vitest'
import { displayName } from './text'

describe('displayName', () => {
  it('met une majuscule initiale sans toucher au reste', () => {
    expect(displayName('lait demi-écrémé')).toBe('Lait demi-écrémé')
    expect(displayName('œufs')).toBe('Œufs')
    expect(displayName('épicerie')).toBe('Épicerie')
  })

  it('laisse intacte une valeur déjà en forme ou vide', () => {
    expect(displayName('Clio RS')).toBe('Clio RS')
    expect(displayName('')).toBe('')
  })
})
