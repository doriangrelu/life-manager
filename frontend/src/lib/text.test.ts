import { describe, expect, it } from 'vitest'
import { displayName, personName } from './text'

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

describe('personName', () => {
  it('met une majuscule à chaque mot et après un trait d’union', () => {
    expect(personName('dorian grelu')).toBe('Dorian Grelu')
    expect(personName('jean-pierre  martin ')).toBe('Jean-Pierre Martin')
    expect(personName('élodie')).toBe('Élodie')
  })

  it('garde les majuscules déjà présentes', () => {
    expect(personName('Alice McDonald')).toBe('Alice McDonald')
    expect(personName('')).toBe('')
  })
})
