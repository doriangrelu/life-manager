// Liste de courses : mise à jour locale immédiate (avant la réponse de l'API) en gardant l'ordre du serveur :
// non cochés du plus ancien au plus récent, puis cochés du dernier coché au premier.

import type { ShoppingItem } from './api/types'

/** Délai entre deux rafraîchissements de la liste quand l'écran est visible. */
export const REFRESH_MS = 5_000

/** Coche ou décoche un article : un article coché passe en tête des cochés, un décoché revient parmi les non cochés. */
export function toggled(items: ShoppingItem[], id: string, checked: boolean): ShoppingItem[] {
  const item = items.find((i) => i.id === id)
  if (!item || item.checked === checked) {
    return items
  }
  const others = items.filter((i) => i.id !== id)
  const unchecked = others.filter((i) => !i.checked)
  const done = others.filter((i) => i.checked)
  const changed = { ...item, checked }
  if (checked) {
    return [...unchecked, changed, ...done]
  }
  const byAge = [...unchecked, changed].sort((a, b) => a.addedAt.localeCompare(b.addedAt))
  return [...byAge, ...done]
}
