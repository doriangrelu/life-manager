// Foyer devenu inaccessible (membre retiré, foyer supprimé par son propriétaire) : l'API répond 404.
// Toute réponse 404 sur une adresse de foyer prévient l'application, qui vérifie la liste des foyers et bascule.

/** Événement émis sur window, avec l'identifiant du foyer concerné. */
export const HOUSEHOLD_MAYBE_LOST = 'life-manager:household-maybe-lost'

const HOUSEHOLD_PATH = /^\/households\/([0-9a-f-]{36})(?:\/|$)/i

/** Foyer visé par une adresse de l'API (« /households/<id>/… »), ou null. */
export function householdIdOf(path: string): string | null {
  return HOUSEHOLD_PATH.exec(path)?.[1] ?? null
}

/** À appeler sur une réponse 404 : si l'adresse vise un foyer, l'application revérifie qu'elle y a encore accès. */
export function reportNotFound(path: string): void {
  const householdId = householdIdOf(path)
  if (householdId && typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent(HOUSEHOLD_MAYBE_LOST, { detail: householdId }))
  }
}

/** Nom retapé pour confirmer une suppression : comparé comme le serveur (espaces réduits, sans majuscules). */
export function sameHouseholdName(typed: string, name: string): boolean {
  const normalize = (text: string) => text.trim().replace(/\s+/g, ' ').toLocaleLowerCase('fr')
  return normalize(typed) !== '' && normalize(typed) === normalize(name)
}
