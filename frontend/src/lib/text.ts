// Mise en forme des noms enregistrés en minuscules par l'API (articles, catégories, sous-listes, foyers…).

/** « lait demi-écrémé » devient « Lait demi-écrémé ». */
export function displayName(text: string): string {
  const trimmed = text.trim()
  return trimmed.charAt(0).toLocaleUpperCase('fr') + trimmed.slice(1)
}
