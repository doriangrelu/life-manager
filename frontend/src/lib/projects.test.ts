import { describe, expect, it } from 'vitest'
import type { Project } from './api/types'
import { periodText, splitProjects, statusLabel, tasksText } from './projects'

const project = (status: Project['status'], name = status): Project => ({
  id: name,
  name,
  description: null,
  color: 'TEAL',
  plannedCents: null,
  startOn: null,
  targetOn: null,
  status,
  countsInBudget: true,
  tasksDone: 0,
  tasksTotal: 0,
})

describe('projets', () => {
  it('sépare les projets en cours des autres', () => {
    const { active, closed } = splitProjects([project('DONE'), project('ACTIVE'), project('ABANDONED')])
    expect(active.map((p) => p.status)).toEqual(['ACTIVE'])
    expect(closed.map((p) => p.status)).toEqual(['DONE', 'ABANDONED'])
  })

  it("décrit l'avancement des tâches", () => {
    expect(tasksText({ tasksDone: 0, tasksTotal: 0 })).toBeNull()
    expect(tasksText({ tasksDone: 1, tasksTotal: 1 })).toBe('1 / 1 tâche')
    expect(tasksText({ tasksDone: 3, tasksTotal: 7 })).toBe('3 / 7 tâches')
  })

  it('décrit la période', () => {
    const day = (d: string) => d
    expect(periodText(null, null, day)).toBeNull()
    expect(periodText('a', 'b', day)).toBe('du a au b')
    expect(periodText('a', null, day)).toBe('à partir du a')
    expect(periodText(null, 'b', day)).toBe("jusqu'au b")
  })

  it("nomme l'état", () => {
    expect(statusLabel('ABANDONED')).toBe('Abandonné')
  })
})
