// Retour en haut : du conteneur qui défile (corps d'une modale…) ou, à défaut, de la page.

/** Conteneur défilant le plus proche d'un élément, ou null pour la page. */
export function scrollParent(element: Element | null): Element | null {
  for (let node = element?.parentElement ?? null; node; node = node.parentElement) {
    const { overflowY } = getComputedStyle(node)
    if ((overflowY === 'auto' || overflowY === 'scroll') && node.scrollHeight > node.clientHeight) {
      return node
    }
  }
  return null
}

/** Remonte en haut le conteneur de l'élément ; sans élément, la page. */
export function scrollToTop(element?: Element | null): void {
  const reduced = matchMedia('(prefers-reduced-motion: reduce)').matches
  const target = element ? scrollParent(element) : null
  const options: ScrollToOptions = { top: 0, behavior: reduced ? 'auto' : 'smooth' }
  if (target) {
    target.scrollTo(options)
  } else {
    window.scrollTo({ ...options, behavior: 'auto' })
  }
}
