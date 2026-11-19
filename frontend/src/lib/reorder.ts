// Glisser-déposer (svelte-dnd-action) : réglages communs et lecture du résultat.
import { SHADOW_ITEM_MARKER_PROPERTY_NAME, TRIGGERS, type DndEvent } from 'svelte-dnd-action'
import { ms } from './motion'

/** Réglages communs des zones de glisser-déposer : animation courte, pas de cadre autour de la zone. */
export function dndOptions<T extends { id: string }>(items: T[], type: string) {
  return { items, type, flipDurationMs: ms(180), dropTargetStyle: {} }
}

/** Élément fantôme (emplacement de dépôt) pendant un glisser. */
export function isShadow(item: object): boolean {
  return Boolean((item as Record<string, unknown>)[SHADOW_ITEM_MARKER_PROPERTY_NAME])
}

/**
 * Signal tardif envoyé après le dépôt (au clavier notamment) : ce n'est pas un nouveau geste.
 * Le prendre pour un début de glisser bloquerait la synchronisation de la liste.
 */
export function isAfterDrop(event: CustomEvent<DndEvent<unknown>>): boolean {
  return event.detail.info.trigger === TRIGGERS.DRAG_STOPPED
}

/** Éléments de la zone après un geste, sans le fantôme. */
export function dndItems<T>(event: CustomEvent<DndEvent<T>>): T[] {
  return event.detail.items.filter((item) => !isShadow(item as object))
}

/** Identifiants dans l'ordre affiché, à envoyer à l'API. */
export function idsOf(items: { id: string }[]): string[] {
  return items.map((item) => item.id)
}
