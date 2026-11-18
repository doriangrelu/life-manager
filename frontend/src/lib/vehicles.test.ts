import { describe, expect, it } from 'vitest'
import { daysBetween, deadlineStatus, deadlineText, formatDay, kindLabel } from './vehicles'

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
    expect(formatDay('2027-03-01')).toBe('01/03/2027')
    expect(kindLabel('TECHNICAL_INSPECTION')).toBe('Contrôle technique')
  })
})
