import { describe, expect, it } from 'vitest'
import type { Member } from './api/types'
import { beneficiaryOptions, memberName, payerOptions } from './members'

const members = [
  { memberId: 'a', displayName: 'Alice' },
  { memberId: 'b', displayName: 'Bob' },
] as Member[]

describe('membres', () => {
  it('nomme un membre, le foyer entier ou un membre parti', () => {
    expect(memberName(members, 'b')).toBe('Bob')
    expect(memberName(members, null)).toBe('Commun')
    expect(memberName(members, 'z')).toBe('Membre')
  })

  it('propose les payeurs, puis le foyer entier en premier bénéficiaire', () => {
    expect(payerOptions(members).map((o) => o.label)).toEqual(['Alice', 'Bob'])
    expect(beneficiaryOptions(members)).toEqual([
      { value: '', label: 'Commun' },
      { value: 'a', label: 'Alice' },
      { value: 'b', label: 'Bob' },
    ])
  })
})
