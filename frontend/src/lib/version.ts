// Version déployée, affichée en pied de page : celle de la PWA (figée à la construction) et celle de l'API.

/** Version de la PWA (tag Git passé à la construction de l'image), « dev » en développement. */
export const PWA_VERSION: string = import.meta.env.VITE_APP_VERSION || 'dev'

let apiVersion: Promise<string | null> | null = null

/** Version de l'API (route publique, sans jeton), lue une seule fois ; null si elle ne répond pas. */
export function loadApiVersion(): Promise<string | null> {
  apiVersion ??= fetch('/api/version')
    .then((response) => (response.ok ? response.json() : null))
    .then((body: { version?: string } | null) => body?.version ?? null)
    .catch(() => null)
  return apiVersion
}

/** Texte du pied de page : une seule version si tout est aligné, les deux juste après une livraison. */
export function versionText(pwa: string, api: string | null): string {
  if (api === null || api === pwa) {
    return pwa
  }
  return `PWA ${pwa} · API ${api}`
}
