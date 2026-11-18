import { describe, expect, it } from 'vitest'
import { categoryStyle, PALETTE } from './categoryStyle'
import { contrast } from './color'

describe('apparence des catégories', () => {
  it('donne aux catégories par défaut leur couleur propre, quelle que soit la casse', () => {
    expect(categoryStyle('Courses')).toEqual(categoryStyle(' courses '))
    expect(categoryStyle('Courses').bar).not.toBe(categoryStyle('Transport').bar)
  })

  it('donne toujours la même couleur à une catégorie ajoutée', () => {
    expect(categoryStyle('Animaux')).toEqual(categoryStyle('Animaux'))
  })

  it.each(PALETTE)('icône lisible sur sa pastille (%o)', ({ soft, strong }) => {
    expect(contrast(strong, soft)).toBeGreaterThanOrEqual(4.5)
  })
})
