// Animations de l'interface : courtes, et coupées si le système demande de réduire les animations.
import { cubicOut } from 'svelte/easing'
import { fly, type FlyParams } from 'svelte/transition'

const reduced = typeof matchMedia !== 'undefined' && matchMedia('(prefers-reduced-motion: reduce)').matches

/** Durée d'animation, ou 0 si l'utilisateur préfère moins d'animations. */
export function ms(duration: number): number {
  return reduced ? 0 : duration
}

/** Apparition d'un écran ou d'un bloc : fondu avec une légère montée. */
export function rise(node: Element, params: FlyParams = {}) {
  return fly(node, { y: 8, duration: ms(220), easing: cubicOut, ...params })
}

/** Réglages communs du déplacement des éléments d'une liste (animate:flip). */
export const listMove = { duration: ms(220), easing: cubicOut }
