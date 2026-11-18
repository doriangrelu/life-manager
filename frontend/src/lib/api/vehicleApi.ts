import { apiBlob, apiRequest, apiUpload } from './http'
import type { Attachment, MaintenanceEntry, MaintenanceInput, Vehicle, VehicleInput } from './types'

const base = (householdId: string) => `/households/${householdId}/vehicles`

export function listVehicles(householdId: string): Promise<Vehicle[]> {
  return apiRequest('GET', base(householdId))
}

export function registerVehicle(householdId: string, vehicle: VehicleInput): Promise<Vehicle> {
  return apiRequest('POST', base(householdId), vehicle)
}

export function updateVehicle(householdId: string, vehicleId: string, vehicle: VehicleInput): Promise<Vehicle> {
  return apiRequest('PUT', `${base(householdId)}/${vehicleId}`, vehicle)
}

export function removeVehicle(householdId: string, vehicleId: string): Promise<void> {
  return apiRequest('DELETE', `${base(householdId)}/${vehicleId}`)
}

export function listMaintenance(householdId: string, vehicleId: string): Promise<MaintenanceEntry[]> {
  return apiRequest('GET', `${base(householdId)}/${vehicleId}/maintenance`)
}

export function recordMaintenance(
  householdId: string,
  vehicleId: string,
  entry: MaintenanceInput,
): Promise<MaintenanceEntry> {
  return apiRequest('POST', `${base(householdId)}/${vehicleId}/maintenance`, entry)
}

export function deleteMaintenance(householdId: string, vehicleId: string, entryId: string): Promise<void> {
  return apiRequest('DELETE', `${base(householdId)}/${vehicleId}/maintenance/${entryId}`)
}

// --- Pièces jointes d'une opération du carnet ---

const attachments = (householdId: string, vehicleId: string, entryId: string) =>
  `${base(householdId)}/${vehicleId}/maintenance/${entryId}/attachments`

export function listAttachments(householdId: string, vehicleId: string, entryId: string): Promise<Attachment[]> {
  return apiRequest('GET', attachments(householdId, vehicleId, entryId))
}

/** Envoie un fichier ; {@code onprogress} reçoit l'avancement entre 0 et 1. */
export function uploadAttachment(
  householdId: string,
  vehicleId: string,
  entryId: string,
  file: File,
  onprogress: (ratio: number) => void,
): Promise<Attachment> {
  return apiUpload(attachments(householdId, vehicleId, entryId), file, onprogress)
}

/** Contenu du fichier, à ouvrir via URL.createObjectURL. */
export function attachmentContent(householdId: string, vehicleId: string, entryId: string, attachmentId: string): Promise<Blob> {
  return apiBlob(`${attachments(householdId, vehicleId, entryId)}/${attachmentId}/content`)
}

export function removeAttachment(householdId: string, vehicleId: string, entryId: string, attachmentId: string): Promise<void> {
  return apiRequest('DELETE', `${attachments(householdId, vehicleId, entryId)}/${attachmentId}`)
}
