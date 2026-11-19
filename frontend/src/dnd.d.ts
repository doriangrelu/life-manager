// Événements du glisser-déposer (svelte-dnd-action) reconnus sur les éléments HTML par le typage de Svelte.
import type { DndEvent } from 'svelte-dnd-action'

declare module 'svelte/elements' {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  interface HTMLAttributes<T> {
    onconsider?: (event: CustomEvent<DndEvent<any>>) => void
    onfinalize?: (event: CustomEvent<DndEvent<any>>) => void
  }
}

export {}
