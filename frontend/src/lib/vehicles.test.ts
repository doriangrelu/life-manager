import { describe, expect, it } from 'vitest'
import { daysBetween, deadlineStatus, formatDay, kindLabel } from './vehicles'

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

  it('affiche les dates et les types en français', () => {
    expect(formatDay('2027-03-01')).toBe('01/03/2027')
    expect(kindLabel('TECHNICAL_INSPECTION')).toBe('Contrôle technique')
  })
})
