// Pièces d'usure d'un véhicule : libellés, état selon les km parcourus, tri (pièces montées puis historique).

import type { WearPart, WearPartKind } from './api/types'

export const WEAR_PART_KINDS: { kind: WearPartKind; label: string; hint?: string }[] = [
  { kind: 'TIRES_FRONT', label: 'Pneus avant' },
  { kind: 'TIRES_REAR', label: 'Pneus arrière' },
  { kind: 'PADS_FRONT', label: 'Plaquettes avant' },
  { kind: 'PADS_REAR', label: 'Plaquettes arrière' },
  { kind: 'DISCS_FRONT', label: 'Disques avant' },
  { kind: 'DISCS_REAR', label: 'Disques arrière' },
  { kind: 'BATTERY', label: 'Batterie' },
  { kind: 'TIMING_BELT', label: 'Courroie de distribution', hint: 'Inutile si le moteur a une chaîne de distribution.' },
]

export function wearPartLabel(kind: WearPartKind): string {
  return WEAR_PART_KINDS.find((k) => k.kind === kind)?.label ?? kind
}

/** « à prévoir » à partir de 90 % de l'intervalle, « à changer » à 100 % ; sans intervalle ou sans km : rien. */
export type WearStatus = 'ok' | 'soon' | 'due'

export function wearStatus(part: Pick<WearPart, 'kmDone' | 'replaceAfterKm'>): WearStatus | null {
  if (part.replaceAfterKm === null || part.kmDone === null) {
    return null
  }
  if (part.kmDone >= part.replaceAfterKm) {
    return 'due'
  }
  return part.kmDone >= part.replaceAfterKm * 0.9 ? 'soon' : 'ok'
}

/** Part de l'intervalle déjà faite, entre 0 et 1 (pour la barre). */
export function wearRatio(part: Pick<WearPart, 'kmDone' | 'replaceAfterKm'>): number | null {
  if (part.replaceAfterKm === null || part.kmDone === null) {
    return null
  }
  return Math.min(1, Math.max(0, part.kmDone / part.replaceAfterKm))
}

/** Kilométrage lisible : 54300 → « 54 300 km ». */
export function km(value: number): string {
  return `${value.toLocaleString('fr-FR')} km`
}

/** Pièces montées dans l'ordre de la liste des types ; l'historique, la plus récemment déposée d'abord. */
export function splitWearParts(parts: WearPart[]): { mounted: WearPart[]; history: WearPart[] } {
  const order = (kind: WearPartKind) => WEAR_PART_KINDS.findIndex((k) => k.kind === kind)
  const mounted = parts.filter((p) => p.removedOn === null).sort((a, b) => order(a.kind) - order(b.kind))
  const history = parts
    .filter((p) => p.removedOn !== null)
    .sort((a, b) => (b.removedOn ?? '').localeCompare(a.removedOn ?? ''))
  return { mounted, history }
}

/** Types qu'on peut encore ajouter (aucune pièce montée de ce type). */
export function freeKinds(parts: WearPart[]): WearPartKind[] {
  const mounted = new Set(parts.filter((p) => p.removedOn === null).map((p) => p.kind))
  return WEAR_PART_KINDS.map((k) => k.kind).filter((kind) => !mounted.has(kind))
}
