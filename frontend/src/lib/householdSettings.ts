// Réglages du foyer affiché, partagés par les écrans (menus, budget, récurrents).
import { writable } from 'svelte/store'

/** Option « Rentrées et bilan » ; null tant que le foyer n'est pas chargé (rien n'est proposé ni caché à tort). */
export const incomeTracking = writable<boolean | null>(null)

/** Nom et explication de l'option, identiques partout (Foyer, mises en avant). */
export const INCOME_OPTION_TITLE = 'Rentrées et bilan'
export const INCOME_OPTION_TEXT =
  "Notez aussi l'argent qui rentre (salaires, aides…) pour voir chaque mois si vous êtes dans le vert et ce que vous mettez de côté."
