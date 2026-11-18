// Pièces jointes (carnet, factures) : mêmes limites que le serveur, vérifiées avant l'envoi pour éviter une attente inutile.

import type { Attachment } from './api/types'

/** Types acceptés par le serveur (vérifiés côté serveur d'après le contenu). */
export const ACCEPTED_TYPES = 'application/pdf,image/jpeg,image/png,image/webp'
export const MAX_BYTES = 10 * 1024 * 1024
export const MAX_PER_ENTRY = 10

/** « 850 o », « 12 Ko », « 2,4 Mo ». */
export function formatSize(bytes: number): string {
  if (bytes < 1024) {
    return `${bytes} o`
  }
  if (bytes < 1024 * 1024) {
    return `${Math.round(bytes / 1024)} Ko`
  }
  return `${(bytes / (1024 * 1024)).toLocaleString('fr-FR', { maximumFractionDigits: 1 })} Mo`
}

/** Raison de refuser un fichier avant l'envoi, ou null s'il peut partir. */
export function rejectionOf(file: { size: number }): string | null {
  if (file.size === 0) {
    return 'Fichier vide'
  }
  if (file.size > MAX_BYTES) {
    return 'Fichier trop lourd (10 Mo au plus)'
  }
  return null
}

/** Nombre de fichiers encore autorisés pour une opération qui en a déjà {@code count}. */
export function remainingSlots(count: number): number {
  return Math.max(0, MAX_PER_ENTRY - count)
}

/** « 1 pièce jointe », « 3 pièces jointes ». */
export function attachmentCountLabel(count: number): string {
  return count === 1 ? '1 pièce jointe' : `${count} pièces jointes`
}

/** Appels à l'API des fichiers joints d'un objet (opération du carnet, échéance de charge). */
export interface AttachmentApi {
  list(): Promise<Attachment[]>
  upload(file: File, onprogress: (ratio: number) => void): Promise<Attachment>
  content(attachmentId: string): Promise<Blob>
  remove(attachmentId: string): Promise<void>
}
