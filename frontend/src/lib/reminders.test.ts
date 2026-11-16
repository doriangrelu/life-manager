import { describe, expect, it } from 'vitest'
import type { Reminder } from './api/types'
import { reminderDelay, reminderTitle, toNotify } from './reminders'

const reminder = (deadlineId: string, daysLeft: number): Reminder => ({
  deadlineId,
  kind: 'SERVICE',
  subject: 'Clio',
  dueOn: '2026-12-01',
  status: daysLeft < 0 ? 'OVERDUE' : 'DUE_SOON',
  daysLeft,
})

describe('rappels', () => {
  it('décrit un rappel en français', () => {
    expect(reminderTitle(reminder('a', 3))).toBe('Révision — Clio')
    expect(reminderDelay(reminder('a', -46))).toBe('en retard de 46 jours')
    expect(reminderDelay(reminder('a', -1))).toBe('en retard de 1 jour')
    expect(reminderDelay(reminder('a', 0))).toBe("aujourd'hui")
    expect(reminderDelay(reminder('a', 15))).toBe('dans 15 jours')
  })

  it('ne notifie un rappel qu\'une fois par jour', () => {
    const first = toNotify([reminder('a', 3)], null, '2026-11-16')
    const again = toNotify([reminder('a', 3), reminder('b', -2)], first.memo, '2026-11-16')

    expect(first.fresh.map((r) => r.deadlineId)).toEqual(['a'])
    expect(again.fresh.map((r) => r.deadlineId)).toEqual(['b'])
    expect(again.memo).toEqual({ day: '2026-11-16', ids: ['a', 'b'] })
  })

  it('notifie de nouveau le lendemain', () => {
    const memo = { day: '2026-11-16', ids: ['a'] }
    expect(toNotify([reminder('a', 2)], memo, '2026-11-17').fresh).toHaveLength(1)
  })
})
