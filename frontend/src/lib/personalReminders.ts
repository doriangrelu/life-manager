// Rappels saisis par les membres : libellés des choix et texte de la prochaine date.

import type { NoticePeriod, PersonalReminder, Recurrence } from './api/types'

export const RECURRENCES: { value: Recurrence; label: string }[] = [
  { value: 'NONE', label: 'Une fois' },
  { value: 'MONTHLY', label: 'Chaque mois' },
  { value: 'QUARTERLY', label: 'Trimestre' },
  { value: 'YEARLY', label: 'Chaque année' },
]

export const NOTICES: { value: NoticePeriod; label: string }[] = [
  { value: 'SAME_DAY', label: 'Le jour même' },
  { value: 'ONE_WEEK', label: '1 semaine avant' },
  { value: 'ONE_MONTH', label: '1 mois avant' },
]

const RECURRENCE_TEXT: Record<Recurrence, string> = {
  NONE: '',
  MONTHLY: 'chaque mois',
  QUARTERLY: 'chaque trimestre',
  YEARLY: 'chaque année',
}

/** « chaque année », ou rien pour un rappel ponctuel. */
export function recurrenceText(recurrence: Recurrence): string {
  return RECURRENCE_TEXT[recurrence]
}

/** Jours entre aujourd'hui et la date du rappel (négatif : en retard). */
export function daysUntil(reminder: Pick<PersonalReminder, 'dueOn'>, today: string): number {
  const [y1, m1, d1] = today.split('-').map(Number)
  const [y2, m2, d2] = reminder.dueOn.split('-').map(Number)
  return Math.round((Date.UTC(y2, m2 - 1, d2) - Date.UTC(y1, m1 - 1, d1)) / 86_400_000)
}

/** « aujourd'hui », « demain », « dans 5 jours », « en retard de 2 jours ». */
export function whenText(days: number): string {
  if (days === 0) {
    return "aujourd'hui"
  }
  if (days === 1) {
    return 'demain'
  }
  if (days < 0) {
    return `en retard de ${-days} jour${days < -1 ? 's' : ''}`
  }
  return `dans ${days} jours`
}
