// Véhicules : libellés des types d'entretien et état des échéances (dates au format « 2027-03-01 »).

import type { MaintenanceKind } from './api/types'

export const MAINTENANCE_KINDS: { kind: MaintenanceKind; label: string }[] = [
  { kind: 'SERVICE', label: 'Révision' },
  { kind: 'OIL_CHANGE', label: 'Vidange' },
  { kind: 'TECHNICAL_INSPECTION', label: 'Contrôle technique' },
  { kind: 'TIRES', label: 'Pneus' },
  { kind: 'REPAIR', label: 'Réparation' },
  { kind: 'OTHER', label: 'Autre' },
]

export function kindLabel(kind: MaintenanceKind): string {
  return MAINTENANCE_KINDS.find((k) => k.kind === kind)?.label ?? kind
}

/** Durée en années avant la prochaine opération (même règle que le serveur, qui fait foi). */
const VALIDITY_YEARS: Partial<Record<MaintenanceKind, number>> = { TECHNICAL_INSPECTION: 2, SERVICE: 1 }

/** Le type d'opération fixe-t-il une prochaine échéance du véhicule ? */
export function setsDeadline(kind: MaintenanceKind): boolean {
  return VALIDITY_YEARS[kind] !== undefined
}

/** Échéance calculée automatiquement : « 2026-11-18 » + 2 ans → « 2028-11-18 » ; un 29 février donne le 28. */
export function automaticNextDue(kind: MaintenanceKind, performedOn: string): string | null {
  const years = VALIDITY_YEARS[kind]
  if (years === undefined || !performedOn) {
    return null
  }
  const [year, month, day] = performedOn.split('-').map(Number)
  const target = year + years
  const isLeap = (target % 4 === 0 && target % 100 !== 0) || target % 400 === 0
  const safeDay = month === 2 && day === 29 && !isLeap ? 28 : day
  return `${target}-${String(month).padStart(2, '0')}-${String(safeDay).padStart(2, '0')}`
}

/** Lendemain d'une date ISO : première date précise acceptée pour la prochaine échéance. */
export function dayAfter(day: string): string {
  return new Date(Date.parse(`${day}T00:00:00Z`) + 86_400_000).toISOString().slice(0, 10)
}

/** « Prochain contrôle technique », « Prochaine révision ». */
export function nextDueLabel(kind: MaintenanceKind): string {
  return kind === 'SERVICE' ? 'Prochaine révision' : 'Prochain contrôle technique'
}

/** Une échéance est « proche » dans les 30 jours qui viennent. */
export const SOON_DAYS = 30

export type DeadlineStatus = 'none' | 'overdue' | 'soon' | 'later'

/** Nombre de jours entre deux dates ISO (négatif si `due` est passée). */
export function daysBetween(today: string, due: string): number {
  return (Date.parse(`${due}T00:00:00Z`) - Date.parse(`${today}T00:00:00Z`)) / 86_400_000
}

/** État d'une échéance par rapport à aujourd'hui. */
export function deadlineStatus(due: string | null, today: string): DeadlineStatus {
  if (!due) {
    return 'none'
  }
  const days = daysBetween(today, due)
  if (days < 0) {
    return 'overdue'
  }
  return days <= SOON_DAYS ? 'soon' : 'later'
}

/** Texte court de l'urgence : « dans 18 jours », « en retard de 3 jours », « à jour »… */
export function deadlineText(due: string | null, today: string): string {
  const status = deadlineStatus(due, today)
  if (status === 'none' || !due) {
    return 'non renseigné'
  }
  if (status === 'later') {
    return 'à jour'
  }
  const days = Math.abs(daysBetween(today, due))
  const plural = days > 1 ? 's' : ''
  if (status === 'overdue') {
    return `en retard de ${days} jour${plural}`
  }
  return days === 0 ? "aujourd'hui" : `dans ${days} jour${plural}`
}

/** « 2027-03-01 » → « 01/03/2027 ». */
export function formatDay(day: string): string {
  const [year, month, date] = day.split('-')
  return `${date}/${month}/${year}`
}
