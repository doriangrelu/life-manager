import { describe, expect, it } from 'vitest'
import { centsToInput, formatCents, parseAmountToCents } from './money'

describe('parseAmountToCents', () => {
  it.each([
    ['12', 1200],
    ['12,5', 1250],
    ['12,50', 1250],
    ['12.50', 1250],
    ['0,1', 10],
    ['0,01', 1],
    [' 1 234,56 ', 123456],
    ['42,50 €', 4250],
  ])('« %s » vaut %i centimes', (input, cents) => {
    expect(parseAmountToCents(input)).toBe(cents)
  })

  it.each(['', '0', '0,00', '-5', 'abc', '12,345', '12,', ',5', '1e3', '12,5,0'])(
    '« %s » est refusé',
    (input) => {
      expect(parseAmountToCents(input)).toBeNull()
    },
  )
})

describe('affichage', () => {
  it('formate en euros à la française', () => {
    expect(formatCents(123456).replace(/\s/g, ' ')).toBe('1 234,56 €')
  })

  it('pré-remplit un champ de saisie', () => {
    expect(centsToInput(1250)).toBe('12,50')
    expect(centsToInput(5)).toBe('0,05')
  })
})
