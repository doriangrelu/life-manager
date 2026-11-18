import { describe, expect, it } from 'vitest'
import type { Charge } from './api/types'
import { monthlyEstimateCents, sortCharges } from './charges'

function charge(name: string, changes: Partial<Charge> = {}): Charge {
  return {
    id: name,
    name,
    amountCents: 1200,
    amountKind: 'FIXED',
    frequency: 'MONTHLY',
    firstDueOn: '2026-12-01',
    budgetCategoryId: 'cat',
    payerId: 'alice',
    beneficiaryId: null,
    active: true,
    next: null,
    ...changes,
  }
}

const next = (dueOn: string, status: 'UPCOMING' | 'TO_CONFIRM') => ({
  id: dueOn,
  chargeId: 'c',
  dueOn,
  expectedCents: 0,
  status,
  paidCents: null,
  paidOn: null,
  invoiceCount: 0,
})

describe('charges', () => {
  it('ramène chaque charge active à son coût mensuel', () => {
    const charges = [
      charge('internet', { amountCents: 3999 }),
      charge('assurance', { amountCents: 12000, frequency: 'YEARLY' }),
      charge('eau', { amountCents: 9000, frequency: 'QUARTERLY' }),
      charge('ancienne', { amountCents: 50000, active: false }),
    ]
    expect(monthlyEstimateCents(charges)).toBe(3999 + 1000 + 3000)
  })

  it('met les échéances à confirmer en tête, puis trie par date, résiliées à la fin', () => {
    const sorted = sortCharges([
      charge('fin', { active: false }),
      charge('tard', { next: next('2026-12-20', 'UPCOMING') }),
      charge('tot', { next: next('2026-12-02', 'UPCOMING') }),
      charge('elec', { next: next('2026-11-10', 'TO_CONFIRM') }),
    ])
    expect(sorted.map((c) => c.name)).toEqual(['elec', 'tot', 'tard', 'fin'])
  })
})
