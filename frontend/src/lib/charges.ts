// Charges récurrentes : libellés, tri et estimation mensuelle (affichage seulement, montants en centimes).

import type { Charge, Frequency, Occurrence, OccurrenceStatus } from './api/types'

export const FREQUENCIES: { value: Frequency; label: string; months: number }[] = [
  { value: 'MONTHLY', label: 'Mensuelle', months: 1 },
  { value: 'BIMONTHLY', label: 'Tous les 2 mois', months: 2 },
  { value: 'QUARTERLY', label: 'Trimestrielle', months: 3 },
  { value: 'YEARLY', label: 'Annuelle', months: 12 },
]

const STATUS_LABELS: Record<OccurrenceStatus, string> = {
  UPCOMING: 'À venir',
  TO_CONFIRM: 'À confirmer',
  PAID: 'Payée',
  SKIPPED: 'Sautée',
}

export function frequencyLabel(frequency: Frequency): string {
  return FREQUENCIES.find((f) => f.value === frequency)?.label ?? frequency
}

export function statusLabel(status: OccurrenceStatus): string {
  return STATUS_LABELS[status]
}

/** Coût mensuel moyen des charges actives (une charge annuelle de 120 € compte pour 10 €). */
export function monthlyEstimateCents(charges: Charge[]): number {
  return Math.round(
    charges
      .filter((c) => c.active)
      .reduce((total, c) => total + c.amountCents / (FREQUENCIES.find((f) => f.value === c.frequency)?.months ?? 1), 0),
  )
}

/** Échéances à confirmer d'abord, puis par prochaine échéance ; les charges résiliées en dernier. */
export function sortCharges(charges: Charge[]): Charge[] {
  const rank = (c: Charge) => (!c.active ? 2 : c.next?.status === 'TO_CONFIRM' ? 0 : 1)
  const due = (c: Charge) => c.next?.dueOn ?? '9999-12-31'
  return [...charges].sort((a, b) => rank(a) - rank(b) || due(a).localeCompare(due(b)))
}

/** Montant à afficher pour une échéance : le payé s'il l'est, sinon le prévu. */
export function occurrenceCents(occurrence: Occurrence): number {
  return occurrence.paidCents ?? occurrence.expectedCents
}
