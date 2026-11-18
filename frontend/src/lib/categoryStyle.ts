// Apparence d'une catégorie de dépenses : une icône et une couleur, stables d'un écran à l'autre.
// Les catégories par défaut ont leur icône ; une catégorie ajoutée par le foyer reçoit une couleur
// tirée de son nom (toujours la même) et une étiquette.

import { Bus, HeartPulse, House, Popcorn, Repeat, ShoppingCart, Tag, UtensilsCrossed } from '@lucide/svelte'

export interface CategoryStyle {
  icon: typeof Tag
  /** Couleur de fond de la pastille. */
  soft: string
  /** Couleur de l'icône sur ce fond (contraste vérifié par les tests). */
  strong: string
  /** Couleur des barres de proportion. */
  bar: string
}

export const PALETTE: Omit<CategoryStyle, 'icon'>[] = [
  { soft: '#d1fae5', strong: '#047857', bar: '#10b981' },
  { soft: '#e0e7ff', strong: '#3730a3', bar: '#6366f1' },
  { soft: '#e0f2fe', strong: '#075985', bar: '#0ea5e9' },
  { soft: '#ffe4e6', strong: '#9f1239', bar: '#f43f5e' },
  { soft: '#f3e8ff', strong: '#6b21a8', bar: '#a855f7' },
  { soft: '#ffedd5', strong: '#9a3412', bar: '#f97316' },
  { soft: '#fef9c3', strong: '#854d0e', bar: '#eab308' },
  { soft: '#e2e8f0', strong: '#334155', bar: '#64748b' },
]

const KNOWN: Record<string, { icon: typeof Tag; color: number }> = {
  courses: { icon: ShoppingCart, color: 0 },
  logement: { icon: House, color: 1 },
  transport: { icon: Bus, color: 2 },
  santé: { icon: HeartPulse, color: 3 },
  loisirs: { icon: Popcorn, color: 4 },
  restaurants: { icon: UtensilsCrossed, color: 5 },
  abonnements: { icon: Repeat, color: 6 },
  autre: { icon: Tag, color: 7 },
}

export function categoryStyle(name: string): CategoryStyle {
  const key = name.trim().toLowerCase()
  const known = KNOWN[key]
  const color = known ? known.color : hash(key) % PALETTE.length
  return { icon: known?.icon ?? Tag, ...PALETTE[color] }
}

// Petit hachage de texte : la même catégorie garde toujours la même couleur.
function hash(text: string): number {
  let value = 0
  for (const char of text) {
    value = (value * 31 + char.charCodeAt(0)) >>> 0
  }
  return value
}
