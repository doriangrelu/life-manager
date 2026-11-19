import { describe, expect, it } from 'vitest'
import type { Charge, Occurrence } from './api/types'
import { endMonthLabel, isFinished, isLoan, loanBreakdown, remainingInstalments } from './charges'

const next = (dueOn: string) => ({ dueOn }) as Occurrence

const loan = (changes: Partial<Charge> = {}): Charge =>
  ({
    amountCents: 61240,
    frequency: 'MONTHLY',
    active: true,
    next: next('2026-12-05'),
    lastDueOn: '2027-03-05',
    loan: { insuranceCents: 2400 },
    ...changes,
  }) as Charge

describe('crédits', () => {
  it('distingue le prêt de son assurance', () => {
    expect(isLoan(loan())).toBe(true)
    expect(isLoan(loan({ loan: null }))).toBe(false)
    expect(loanBreakdown(loan())?.replace(/\s/g, ' ')).toBe('prêt 588,40 € + assurance 24,00 €')
    expect(loanBreakdown(loan({ loan: { insuranceCents: 0 } }))).toBeNull()
  })

  it('compte les mensualités de la prochaine échéance à la dernière', () => {
    expect(remainingInstalments(loan())).toBe(4)
    expect(remainingInstalments(loan({ next: next('2027-03-05') }))).toBe(1)
    // Dernière mensualité un peu avant le jour habituel : le mois entamé ne compte pas.
    expect(remainingInstalments(loan({ lastDueOn: '2027-03-04' }))).toBe(3)
    expect(remainingInstalments(loan({ lastDueOn: null }))).toBeNull()
    expect(remainingInstalments(loan({ next: null }))).toBeNull()
  })

  it('nomme le mois de fin et reconnaît un crédit terminé', () => {
    expect(endMonthLabel('2049-03-05')).toBe('mars 2049')
    expect(isFinished(loan({ active: false, lastDueOn: '2026-11-05' }), '2026-11-19')).toBe(true)
    // Résilié à la main avant sa fin : ce n'est pas « terminé ».
    expect(isFinished(loan({ active: false }), '2026-11-19')).toBe(false)
    expect(isFinished(loan(), '2026-11-19')).toBe(false)
  })
})
