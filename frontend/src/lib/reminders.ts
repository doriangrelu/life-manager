// Rappels : textes affichés et choix des notifications à montrer (une fois par jour et par rappel, par appareil).

import type { Reminder } from './api/types'

const KIND_LABELS: Record<Reminder['kind'], string> = {
  TECHNICAL_INSPECTION: 'Contrôle technique',
  SERVICE: 'Révision',
}

/** « Révision — Clio » */
export function reminderTitle(reminder: Reminder): string {
  return `${KIND_LABELS[reminder.kind]} — ${reminder.subject}`
}

/** « en retard de 46 jours », « aujourd'hui », « dans 15 jours ». */
export function reminderDelay(reminder: Reminder): string {
  const days = Math.abs(reminder.daysLeft)
  const unit = days > 1 ? 'jours' : 'jour'
  if (reminder.daysLeft < 0) {
    return `en retard de ${days} ${unit}`
  }
  return reminder.daysLeft === 0 ? "aujourd'hui" : `dans ${days} ${unit}`
}

/** Rappels déjà notifiés sur cet appareil, pour un jour donné. */
export interface NotifiedMemo {
  day: string
  ids: string[]
}

/**
 * Rappels à notifier aujourd'hui et mémo mis à jour. Un mémo d'un autre jour est oublié :
 * un rappel toujours d'actualité est notifié de nouveau le lendemain.
 */
export function toNotify(
  reminders: Reminder[],
  memo: NotifiedMemo | null,
  today: string,
): { fresh: Reminder[]; memo: NotifiedMemo } {
  const already = memo?.day === today ? memo.ids : []
  const fresh = reminders.filter((r) => !already.includes(r.deadlineId))
  return { fresh, memo: { day: today, ids: [...already, ...fresh.map((r) => r.deadlineId)] } }
}
