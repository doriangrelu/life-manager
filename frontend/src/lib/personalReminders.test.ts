import { describe, expect, it } from 'vitest'
import { daysUntil, recurrenceText, whenText } from './personalReminders'

describe('rappels saisis', () => {
  it('compte les jours jusqu\'à la date, même par-dessus un changement d\'heure', () => {
    expect(daysUntil({ dueOn: '2026-11-24' }, '2026-11-19')).toBe(5)
    expect(daysUntil({ dueOn: '2026-11-17' }, '2026-11-19')).toBe(-2)
    expect(daysUntil({ dueOn: '2027-04-01' }, '2027-03-20')).toBe(12)
  })

  it('dit quand tombe le rappel', () => {
    expect(whenText(0)).toBe("aujourd'hui")
    expect(whenText(1)).toBe('demain')
    expect(whenText(5)).toBe('dans 5 jours')
    expect(whenText(-1)).toBe('en retard de 1 jour')
    expect(whenText(-3)).toBe('en retard de 3 jours')
  })

  it('nomme la récurrence', () => {
    expect(recurrenceText('YEARLY')).toBe('chaque année')
    expect(recurrenceText('NONE')).toBe('')
  })
})
