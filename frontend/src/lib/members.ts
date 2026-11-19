// Membres du foyer à l'affichage : nom d'un membre, « Commun » pour tout le foyer, choix payeur / bénéficiaire.
import type { Member } from './api/types'

/** Libellé du foyer entier comme bénéficiaire. */
export const SHARED = 'Commun'

/** Nom d'un membre ; null désigne le foyer entier. Un membre parti s'affiche « Membre ». */
export function memberName(members: Member[], memberId: string | null): string {
  if (memberId === null) {
    return SHARED
  }
  return members.find((m) => m.memberId === memberId)?.displayName ?? 'Membre'
}

/** Choix « payé par » : un bouton par membre. */
export function payerOptions(members: Member[]): { value: string; label: string }[] {
  return members.map((m) => ({ value: m.memberId, label: m.displayName }))
}

/** Choix « pour » : le foyer entier (valeur vide), puis chaque membre. */
export function beneficiaryOptions(members: Member[]): { value: string; label: string }[] {
  return [{ value: '', label: SHARED }, ...payerOptions(members)]
}
