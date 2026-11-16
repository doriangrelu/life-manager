// Mois et jours au format de l'API : « 2026-11 » et « 2026-11-16 », dans le fuseau de l'appareil.

/** Mois en cours, ex. « 2026-11 ». */
export function currentMonth(now: Date = new Date()): string {
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}`
}

/** Jour en cours, ex. « 2026-11-16 ». */
export function today(now: Date = new Date()): string {
  return `${currentMonth(now)}-${pad(now.getDate())}`
}

/** Décale un mois : shiftMonth('2026-01', -1) → '2025-12'. */
export function shiftMonth(month: string, delta: number): string {
  const [year, monthNumber] = month.split('-').map(Number)
  const index = year * 12 + (monthNumber - 1) + delta
  return `${Math.floor(index / 12)}-${pad((index % 12) + 1)}`
}

/** « 2026-11 » → « novembre 2026 ». */
export function monthLabel(month: string): string {
  const [year, monthNumber] = month.split('-').map(Number)
  return new Date(year, monthNumber - 1, 1).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
}

function pad(value: number): string {
  return String(value).padStart(2, '0')
}
