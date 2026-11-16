import { describe, expect, it } from 'vitest'
import type { ShoppingItem } from './api/types'
import { toggled } from './shopping'

const item = (id: string, addedAt: string, checked = false): ShoppingItem => ({ id, label: id, checked, addedAt })

describe('liste de courses', () => {
  const list = [
    item('pain', '2026-11-16T08:00:00Z'),
    item('lait', '2026-11-16T09:00:00Z'),
    item('oeufs', '2026-11-16T07:00:00Z', true),
  ]

  it('place un article coché en tête des articles cochés', () => {
    expect(toggled(list, 'lait', true).map((i) => i.id)).toEqual(['pain', 'lait', 'oeufs'])
    expect(toggled(list, 'pain', true).map((i) => [i.id, i.checked])).toEqual([
      ['lait', false],
      ['pain', true],
      ['oeufs', true],
    ])
  })

  it('remet un article décoché à sa place parmi les non cochés', () => {
    expect(toggled(list, 'oeufs', false).map((i) => i.id)).toEqual(['oeufs', 'pain', 'lait'])
  })

  it('ne change rien si l\'article est inconnu ou déjà dans cet état', () => {
    expect(toggled(list, 'inconnu', true)).toBe(list)
    expect(toggled(list, 'oeufs', true)).toBe(list)
  })
})
