// Écran affiché, écrit dans l'adresse après « # » : F5 et Précédent / Suivant le retrouvent.
// Exemples : #/budget, #/budget/2026-10, #/courses, #/vehicules/<id>, #/foyer.

import { writable, type Readable } from 'svelte/store'

export type Section = 'budget' | 'shopping' | 'vehicles' | 'household'

/** Écran : la section, et pour Budget le mois consulté, pour Véhicules le véhicule ouvert. */
export interface Route {
  section: Section
  month?: string
  vehicleId?: string
}

// Nom de chaque section dans l'adresse.
const SLUGS: Record<Section, string> = {
  budget: 'budget',
  shopping: 'courses',
  vehicles: 'vehicules',
  household: 'foyer',
}

const MONTH = /^\d{4}-(0[1-9]|1[0-2])$/
const DEFAULT_ROUTE: Route = { section: 'budget' }

/** Lit une adresse ; tout ce qui n'est pas reconnu ramène au budget du mois courant. */
export function parseRoute(hash: string): Route {
  const [slug, param, ...rest] = hash.replace(/^#?\/?/, '').split('/')
  const section = (Object.keys(SLUGS) as Section[]).find((s) => SLUGS[s] === slug)
  if (!section || rest.length > 0) {
    return DEFAULT_ROUTE
  }
  if (!param) {
    return { section }
  }
  if (section === 'budget' && MONTH.test(param)) {
    return { section, month: param }
  }
  if (section === 'vehicles') {
    return { section, vehicleId: decodeURIComponent(param) }
  }
  // Paramètre inattendu (mois invalide…) : la section seule.
  return { section }
}

/** Écrit un écran sous forme d'adresse, ex. « #/budget/2026-10 ». */
export function formatRoute(route: Route): string {
  const param = route.section === 'budget' ? route.month : route.section === 'vehicles' ? route.vehicleId : undefined
  const suffix = param ? `/${encodeURIComponent(param)}` : ''
  return `#/${SLUGS[route.section]}${suffix}`
}

function currentHash(): string {
  return typeof location === 'undefined' ? '' : location.hash
}

const store = writable<Route>(parseRoute(currentHash()), (set) => {
  // Adresse absente ou invalide : corrigée sans créer d'entrée d'historique.
  const sync = () => {
    const route = parseRoute(location.hash)
    if (formatRoute(route) !== location.hash) {
      history.replaceState(history.state, '', formatRoute(route))
    }
    set(route)
  }
  sync()
  window.addEventListener('hashchange', sync)
  return () => window.removeEventListener('hashchange', sync)
})

/** Écran courant, suivi au fil des changements d'adresse. */
export const route: Readable<Route> = { subscribe: store.subscribe }

/** Affiche un écran ; `replace` corrige l'adresse sans ajouter d'entrée d'historique. */
export function navigate(next: Route, options: { replace?: boolean } = {}): void {
  const hash = formatRoute(next)
  if (hash === location.hash) {
    return
  }
  if (options.replace) {
    // replaceState ne déclenche pas « hashchange » : l'état est mis à jour à la main.
    history.replaceState(history.state, '', hash)
    store.set(parseRoute(hash))
  } else {
    location.hash = hash
  }
}
