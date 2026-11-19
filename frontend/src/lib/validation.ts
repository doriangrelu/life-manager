// Contrôles de saisie réutilisables : chaque règle renvoie le motif de l'erreur, ou null si la valeur est valide.
// Les formulaires les appliquent en direct (champ en rouge, motif dessous) ; le serveur reste la référence.

import { parseAmountToCents } from './money'

export type Rule = (value: string) => string | null

/** Première erreur rencontrée, dans l'ordre des règles. */
export function check(value: string, ...rules: Rule[]): string | null {
  for (const rule of rules) {
    const error = rule(value)
    if (error) {
      return error
    }
  }
  return null
}

/** Après un envoi refusé : place le curseur dans le premier champ en erreur du formulaire. */
export function focusFirstInvalid(form: HTMLElement | null) {
  form?.querySelector<HTMLElement>('[aria-invalid="true"]')?.focus()
}

/** Vrai si aucun champ n'est en erreur. */
export function allValid(errors: Record<string, string | null>): boolean {
  return Object.values(errors).every((error) => error === null)
}

export const required =
  (message: string): Rule =>
  (value) =>
    value.trim() ? null : message

export const maxLength =
  (max: number): Rule =>
  (value) =>
    value.trim().length > max ? `${max} caractères au plus` : null

/** Montant en euros (« 12,50 ») strictement positif ; vide = erreur. */
export const amount =
  (example: string): Rule =>
  (value) =>
    !value.trim()
      ? 'Saisissez un montant'
      : parseAmountToCents(value) === null
        ? `Montant invalide (exemple : ${example})`
        : null

/** Comme `amount`, mais le champ peut rester vide. */
export const optionalAmount =
  (example: string): Rule =>
  (value) =>
    value.trim() ? amount(example)(value) : null

/** Nombre entier positif ou nul, facultatif (kilométrage…). */
export const optionalInteger =
  (example: string, max: number): Rule =>
  (value) => {
    const text = value.replace(/\s/g, '')
    if (!text) {
      return null
    }
    if (!/^\d+$/.test(text)) {
      return `Nombre invalide (exemple : ${example})`
    }
    return Number(text) > max ? `${max.toLocaleString('fr-FR')} au plus` : null
  }

/** Date (« 2026-11-19 ») qui ne dépasse pas `limit`. */
export const notAfter =
  (limit: string, message: string): Rule =>
  (value) =>
    value && value > limit ? message : null

/** Date qui ne précède pas `limit` ; une date vide est acceptée. */
export const notBefore =
  (limit: string, message: string): Rule =>
  (value) =>
    value && limit && value < limit ? message : null
