import { describe, expect, it } from 'vitest'
import {
  automaticNextDue,
  dayAfter,
  daysBetween,
  deadlineStatus,
  deadlineText,
  formatDay,
  kindLabel,
  setsDeadline,
} from './vehicles'

describe('prochaine échéance fixée par le carnet', () => {
  it('ajoute 2 ans pour un contrôle technique et 1 an pour une révision', () => {
    expect(automaticNextDue('TECHNICAL_INSPECTION', '2026-11-18')).toBe('2028-11-18')
    expect(automaticNextDue('SERVICE', '2026-11-18')).toBe('2027-11-18')
  })

  it('ramène un 29 février au 28 les années non bissextiles', () => {
    expect(automaticNextDue('TECHNICAL_INSPECTION', '2024-02-29')).toBe('2026-02-28')
    expect(automaticNextDue('SERVICE', '2027-02-28')).toBe('2028-02-28')
  })

  it('ne fixe rien pour les autres types', () => {
    expect(setsDeadline('OIL_CHANGE')).toBe(false)
    expect(automaticNextDue('OIL_CHANGE', '2026-11-18')).toBeNull()
  })

  it('donne le lendemain, y compris en fin de mois', () => {
    expect(dayAfter('2026-12-31')).toBe('2027-01-01')
  })
})

describe('échéances', () => {
  it('compte les jours entre deux dates, y compris en changeant de mois', () => {
    expect(daysBetween('2026-11-16', '2026-12-16')).toBe(30)
    expect(daysBetween('2026-11-16', '2026-11-15')).toBe(-1)
  })

  it('classe une échéance : aucune, passée, proche ou lointaine', () => {
    expect(deadlineStatus(null, '2026-11-16')).toBe('none')
    expect(deadlineStatus('2026-11-15', '2026-11-16')).toBe('overdue')
    expect(deadlineStatus('2026-11-16', '2026-11-16')).toBe('soon')
    expect(deadlineStatus('2026-12-16', '2026-11-16')).toBe('soon')
    expect(deadlineStatus('2026-12-17', '2026-11-16')).toBe('later')
  })

  it("décrit l'urgence en quelques mots", () => {
    expect(deadlineText(null, '2026-11-16')).toBe('non renseigné')
    expect(deadlineText('2026-11-13', '2026-11-16')).toBe('en retard de 3 jours')
    expect(deadlineText('2026-11-16', '2026-11-16')).toBe("aujourd'hui")
    expect(deadlineText('2026-11-17', '2026-11-16')).toBe('dans 1 jour')
    expect(deadlineText('2026-12-04', '2026-11-16')).toBe('dans 18 jours')
    expect(deadlineText('2027-03-15', '2026-11-16')).toBe('à jour')
  })

  it('affiche les dates et les types en français', () => {
    expect(formatDay('2027-03-01')).toBe('1 mars 2027')
    expect(formatDay('2026-11-18')).toBe('18 nov. 2026')
    expect(kindLabel('TECHNICAL_INSPECTION')).toBe('Contrôle technique')
  })
})
