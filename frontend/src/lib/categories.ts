// Catégories de dépenses : « Autre » est le dernier recours, toujours rangé à la fin (même règle que l'API).

import type { Category } from './api/types'

export function isOther(name: string): boolean {
  return name.trim().toLowerCase() === 'autre'
}

/** Tri par nom, « Autre » en dernier. */
export function sortCategories(categories: Category[]): Category[] {
  return [...categories].sort(
    (a, b) => Number(isOther(a.name)) - Number(isOther(b.name)) || a.name.localeCompare(b.name, 'fr'),
  )
}
