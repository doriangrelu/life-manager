import { describe, expect, it } from 'vitest'
import { currentMonth, monthLabel, shiftMonth, today } from './month'

describe('mois', () => {
  it('donne le mois et le jour courants', () => {
    const date = new Date(2026, 10, 6)
    expect(currentMonth(date)).toBe('2026-11')
    expect(today(date)).toBe('2026-11-06')
  })

  it('passe au mois précédent ou suivant, y compris en changeant d\'année', () => {
    expect(shiftMonth('2026-11', -1)).toBe('2026-10')
    expect(shiftMonth('2026-01', -1)).toBe('2025-12')
    expect(shiftMonth('2026-12', 1)).toBe('2027-01')
  })

  it('affiche le mois en français', () => {
    expect(monthLabel('2026-11')).toBe('novembre 2026')
  })
})
