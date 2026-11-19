import { describe, expect, it } from 'vitest'
import { SPACES, sectionToOpen, spaceOf } from './navigation'
import type { Section } from './route'

const space = (id: string) => SPACES.find((s) => s.id === id)!

describe('navigation par espaces', () => {
  it('range chaque écran dans un seul espace', () => {
    const sections: Section[] = ['budget', 'charges', 'shopping', 'vehicles', 'household']
    expect(sections.map((s) => spaceOf(s).id)).toEqual(['money', 'money', 'shopping', 'home', 'household'])
    const all = SPACES.flatMap((s) => s.entries.map((e) => e.section))
    expect(new Set(all).size).toBe(all.length)
  })

  it('rouvre le dernier écran consulté de l’espace', () => {
    expect(sectionToOpen(space('money'), 'shopping', 'charges')).toBe('charges')
  })

  it('ouvre le premier écran sans souvenir valide', () => {
    expect(sectionToOpen(space('money'), 'shopping', null)).toBe('budget')
    expect(sectionToOpen(space('money'), 'shopping', 'vehicles')).toBe('budget')
  })

  it('revient au premier écran quand on touche l’espace déjà ouvert', () => {
    expect(sectionToOpen(space('money'), 'charges', 'charges')).toBe('budget')
  })
})
