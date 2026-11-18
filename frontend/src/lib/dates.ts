// Dates affichées partout de la même façon : « 18 nov. 2026 ».

/** Jour au format ISO (« 2026-11-18 ») ou date complète, affiché « 18 nov. 2026 ». */
export function formatDay(day: string): string {
  // Midi : un jour ISO ne bascule jamais sur la veille à cause du fuseau horaire.
  const date = /^\d{4}-\d{2}-\d{2}$/.test(day) ? new Date(`${day}T12:00:00`) : new Date(day)
  return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' })
}
