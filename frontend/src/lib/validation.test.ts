import { describe, expect, it } from 'vitest'
import { allValid, amount, check, maxLength, notAfter, notBefore, optionalAmount, optionalInteger, required } from './validation'

describe('contrôles de saisie', () => {
  it('renvoie la première erreur, ou null si tout est valide', () => {
    expect(check('  ', required('Donnez un nom'), maxLength(5))).toBe('Donnez un nom')
    expect(check('Internet', required('Donnez un nom'), maxLength(5))).toBe('5 caractères au plus')
    expect(check('Gaz', required('Donnez un nom'), maxLength(5))).toBeNull()
  })

  it('contrôle les montants', () => {
    expect(amount('12,50')('')).toBe('Saisissez un montant')
    expect(amount('12,50')('douze')).toBe('Montant invalide (exemple : 12,50)')
    expect(amount('12,50')('0')).toBe('Montant invalide (exemple : 12,50)')
    expect(amount('12,50')('12,5')).toBeNull()
    expect(optionalAmount('24')('')).toBeNull()
    expect(optionalAmount('24')('x')).toBe('Montant invalide (exemple : 24)')
  })

  it('contrôle les entiers facultatifs', () => {
    const mileage = optionalInteger('45000', 2_000_000)
    expect(mileage('')).toBeNull()
    expect(mileage('45 000')).toBeNull()
    expect(mileage('45,5')).toBe('Nombre invalide (exemple : 45000)')
    // Le séparateur de milliers est une espace insécable : on compare sans tenir compte du type d'espace.
    expect(mileage('3000000')?.replace(/\s/g, ' ')).toBe('2 000 000 au plus')
  })

  it('contrôle l\'ordre des dates', () => {
    expect(notAfter('2026-11-19', 'Pas dans le futur')('2026-11-20')).toBe('Pas dans le futur')
    expect(notAfter('2026-11-19', 'Pas dans le futur')('2026-11-19')).toBeNull()
    expect(notBefore('2026-12-05', 'Trop tôt')('2026-12-04')).toBe('Trop tôt')
    expect(notBefore('2026-12-05', 'Trop tôt')('')).toBeNull()
  })

  it('dit si un formulaire est valide', () => {
    expect(allValid({ name: null, amount: null })).toBe(true)
    expect(allValid({ name: null, amount: 'Saisissez un montant' })).toBe(false)
  })
})
