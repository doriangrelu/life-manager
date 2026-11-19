// Charges récurrentes : libellés, tri et estimation mensuelle (affichage seulement, montants en centimes).

import type { Charge, Frequency, Occurrence, OccurrenceStatus } from './api/types'
import { formatCents } from './money'

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

/** Vrai pour un crédit (mensualité de prêt, assurance comprise). */
export function isLoan(charge: Pick<Charge, 'loan'>): boolean {
  return Boolean(charge.loan)
}

/** « prêt 588,40 € + assurance 24,00 € », ou rien si le crédit n'a pas d'assurance. */
export function loanBreakdown(charge: Pick<Charge, 'amountCents' | 'loan'>): string | null {
  const insuranceCents = charge.loan?.insuranceCents ?? 0
  return insuranceCents > 0
    ? `prêt ${formatCents(charge.amountCents - insuranceCents)} + assurance ${formatCents(insuranceCents)}`
    : null
}

/** Mensualités encore à payer, de la prochaine échéance à la dernière ; null sans date de fin ou sans échéance. */
export function remainingInstalments(charge: Pick<Charge, 'next' | 'lastDueOn' | 'frequency'>): number | null {
  if (!charge.lastDueOn || !charge.next) {
    return null
  }
  const [fromYear, fromMonth, fromDay] = charge.next.dueOn.split('-').map(Number)
  const [toYear, toMonth, toDay] = charge.lastDueOn.split('-').map(Number)
  const months = (toYear - fromYear) * 12 + (toMonth - fromMonth) - (toDay < fromDay ? 1 : 0)
  const step = FREQUENCIES.find((f) => f.value === charge.frequency)?.months ?? 1
  return months < 0 ? 0 : Math.floor(months / step) + 1
}

/** « mars 2049 », pour « fin en mars 2049 ». */
export function endMonthLabel(lastDueOn: string): string {
  const [year, month] = lastDueOn.split('-').map(Number)
  return new Date(year, month - 1, 1).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
}

/** Une charge arrêtée d'elle-même après sa dernière échéance (et non résiliée à la main). */
export function isFinished(charge: Pick<Charge, 'active' | 'lastDueOn'>, today: string): boolean {
  return !charge.active && Boolean(charge.lastDueOn) && (charge.lastDueOn as string) <= today
}

/** Montant à afficher pour une échéance : le payé s'il l'est, sinon le prévu. */
export function occurrenceCents(occurrence: Occurrence): number {
  return occurrence.paidCents ?? occurrence.expectedCents
}
