// Pages légales (CGU, confidentialité) : lisibles sans être connecté, à l'adresse #/cgu ou #/confidentialite.

import { readable } from 'svelte/store'

export type LegalPage = 'cgu' | 'confidentialite'

/** Contact du responsable du traitement, repris dans les textes. */
export const CONTACT_EMAIL = 'doriangrelu@gmail.com'

/** Page légale demandée par l'adresse, ou null. */
export function legalPageOf(hash: string): LegalPage | null {
  const slug = hash.replace(/^#?\/?/, '').split('/')[0]
  return slug === 'cgu' || slug === 'confidentialite' ? slug : null
}

/** Page légale affichée, suivie au fil des changements d'adresse. */
export const legalPage = readable<LegalPage | null>(
  typeof location === 'undefined' ? null : legalPageOf(location.hash),
  (set) => {
    const sync = () => set(legalPageOf(location.hash))
    window.addEventListener('hashchange', sync)
    return () => window.removeEventListener('hashchange', sync)
  },
)
