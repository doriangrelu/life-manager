import { describe, expect, it } from 'vitest'
import { isOther, sortCategories } from './categories'

const category = (name: string) => ({ id: name, name, archived: false })

describe('catégories', () => {
  it('reconnaît « Autre » quelle que soit la casse', () => {
    expect(isOther(' autre ')).toBe(true)
    expect(isOther('Autres frais')).toBe(false)
  })

  it('range par nom, « Autre » en dernier', () => {
    const sorted = sortCategories(['Transport', 'Autre', 'Animaux', 'Santé'].map(category))

    expect(sorted.map((c) => c.name)).toEqual(['Animaux', 'Santé', 'Transport', 'Autre'])
  })
})
