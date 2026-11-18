import { describe, expect, it } from 'vitest'
import type { ShoppingItem, ShoppingList } from './api/types'
import { pickList, purchaseSummary, toggled } from './shopping'

const list = (id: string, name: string): ShoppingList => ({ id, name, pendingCount: 0 })

describe('choix de la sous-liste', () => {
  const lists = [list('b', 'Bricolage'), list('c', 'Courses'), list('v', 'Vêtements')]

  it('rouvre la dernière sous-liste consultée', () => {
    expect(pickList(lists, 'v')?.name).toBe('Vêtements')
  })

  it('ouvre « Courses » si la sous-liste mémorisée a disparu', () => {
    expect(pickList(lists, 'supprimée')?.name).toBe('Courses')
    expect(pickList(lists, null)?.name).toBe('Courses')
  })

  it('ouvre la première à défaut de « Courses »', () => {
    expect(pickList([list('b', 'Bricolage')], null)?.name).toBe('Bricolage')
    expect(pickList([], null)).toBeNull()
  })
})

describe('historique des achats', () => {
  it('résume le nombre d\'achats et la date du dernier', () => {
    const entry = { id: '1', label: 'Lait', lastPurchasedAt: '2026-11-12T10:00:00Z', purchaseCount: 3 }
    expect(purchaseSummary(entry)).toBe('Acheté 3 fois · 12/11')
    expect(purchaseSummary({ ...entry, purchaseCount: 1 })).toBe('Acheté 1 fois · 12/11')
  })
})

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
