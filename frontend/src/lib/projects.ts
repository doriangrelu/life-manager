// Projets : couleurs, libellés d'état, tri de la liste et avancement.
import type { Project, ProjectColor, ProjectStatus } from './api/types'

/** Couleur de chaque projet : teintes moyennes lisibles en clair comme en sombre. */
export const PROJECT_COLORS: Record<ProjectColor, string> = {
  INDIGO: '#6366f1',
  TEAL: '#14b8a6',
  GREEN: '#22c55e',
  AMBER: '#f59e0b',
  ORANGE: '#f97316',
  RED: '#ef4444',
  PINK: '#ec4899',
  SLATE: '#64748b',
}

export const COLOR_NAMES: Record<ProjectColor, string> = {
  INDIGO: 'Indigo',
  TEAL: 'Sarcelle',
  GREEN: 'Vert',
  AMBER: 'Ambre',
  ORANGE: 'Orange',
  RED: 'Rouge',
  PINK: 'Rose',
  SLATE: 'Ardoise',
}

export const STATUS_OPTIONS: { value: ProjectStatus; label: string }[] = [
  { value: 'ACTIVE', label: 'En cours' },
  { value: 'DONE', label: 'Terminé' },
  { value: 'ABANDONED', label: 'Abandonné' },
]

export function statusLabel(status: ProjectStatus): string {
  return STATUS_OPTIONS.find((option) => option.value === status)?.label ?? status
}

/** Projets en cours d'un côté, terminés et abandonnés de l'autre (ordre reçu gardé). */
export function splitProjects(projects: Project[]): { active: Project[]; closed: Project[] } {
  return {
    active: projects.filter((p) => p.status === 'ACTIVE'),
    closed: projects.filter((p) => p.status !== 'ACTIVE'),
  }
}

/** « 3 / 7 tâches », ou rien s'il n'y en a pas. */
export function tasksText(project: Pick<Project, 'tasksDone' | 'tasksTotal'>): string | null {
  if (project.tasksTotal === 0) {
    return null
  }
  return `${project.tasksDone} / ${project.tasksTotal} tâche${project.tasksTotal > 1 ? 's' : ''}`
}

/** Période d'un projet : « du 1 mars au 30 juin 2027 », « à partir du… », « jusqu'au… », ou rien. */
export function periodText(startOn: string | null, targetOn: string | null, format: (day: string) => string): string | null {
  if (startOn && targetOn) {
    return `du ${format(startOn)} au ${format(targetOn)}`
  }
  if (startOn) {
    return `à partir du ${format(startOn)}`
  }
  if (targetOn) {
    return `jusqu'au ${format(targetOn)}`
  }
  return null
}
