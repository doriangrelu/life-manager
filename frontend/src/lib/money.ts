// Montants : l'API ne parle qu'en centimes d'euro (entiers). Ce fichier fait la conversion avec ce que
// l'utilisateur tape ou lit (« 12,50 € »). La saisie est découpée comme du texte : aucun calcul à virgule.

const AMOUNT = /^(\d{1,7})(?:[.,](\d{1,2}))?$/

/** « 12,5 » → 1250. Renvoie null si le texte n'est pas un montant positif valide. */
export function parseAmountToCents(input: string): number | null {
  const compact = input.replace(/[\s  ]/g, '').replace(/€$/, '')
  const match = AMOUNT.exec(compact)
  if (!match) {
    return null
  }
  const euros = Number(match[1])
  const cents = Number((match[2] ?? '').padEnd(2, '0'))
  const total = euros * 100 + cents
  return total > 0 ? total : null
}

/** 1250 → « 12,50 € » (pour l'affichage). */
export function formatCents(cents: number): string {
  return euroFormat.format(cents / 100)
}

/** 1250 → « 12,50 » (pour pré-remplir un champ de saisie). */
export function centsToInput(cents: number): string {
  const euros = Math.trunc(cents / 100)
  const rest = String(cents % 100).padStart(2, '0')
  return `${euros},${rest}`
}

const euroFormat = new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' })
