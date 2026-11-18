// Liste de courses : mise à jour locale immédiate (avant la réponse de l'API) en gardant l'ordre du serveur :
// non cochés du plus ancien au plus récent, puis cochés du dernier coché au premier.

import type { PurchaseHistoryEntry, ShoppingItem, ShoppingList } from './api/types'

/** Délai entre deux rafraîchissements de la liste quand l'écran est visible. */
export const REFRESH_MS = 5_000

/** Sous-liste à afficher : la dernière consultée si elle existe encore, sinon « Courses », sinon la première. */
export function pickList(lists: ShoppingList[], rememberedId: string | null): ShoppingList | null {
  return (
    lists.find((l) => l.id === rememberedId) ??
    lists.find((l) => l.name.toLowerCase() === 'courses') ??
    lists[0] ??
    null
  )
}

/** « Acheté 3 fois · 12/11 » : de quoi juger d'un coup d'œil un article de l'historique. */
export function purchaseSummary(entry: PurchaseHistoryEntry): string {
  const times = entry.purchaseCount === 1 ? 'Acheté 1 fois' : `Acheté ${entry.purchaseCount} fois`
  const day = new Date(entry.lastPurchasedAt).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })
  return `${times} · ${day}`
}

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
