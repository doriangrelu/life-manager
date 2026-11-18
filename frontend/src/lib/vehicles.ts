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
