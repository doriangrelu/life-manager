// Réglages du foyer affiché, partagés par les écrans (menus, budget, charges).
import { writable } from 'svelte/store'

/** Suivi des rentrées et bilan : faux tant que le foyer n'est pas chargé, et par défaut. */
export const incomeTracking = writable(false)
