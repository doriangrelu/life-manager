// Mise en forme des noms à l'affichage.

/** Noms enregistrés en minuscules par l'API (articles, catégories…) : « lait demi-écrémé » → « Lait demi-écrémé ». */
export function displayName(text: string): string {
  const trimmed = text.trim()
  return trimmed.charAt(0).toLocaleUpperCase('fr') + trimmed.slice(1)
}

/** Nom d'une personne : majuscule à chaque mot et après un trait d'union, « jean-pierre martin » → « Jean-Pierre Martin ». */
export function personName(text: string): string {
  return text
    .trim()
    .replace(/\s+/g, ' ')
    .split(/([ -])/)
    .map((part) => (part === ' ' || part === '-' ? part : displayName(part)))
    .join('')
}
