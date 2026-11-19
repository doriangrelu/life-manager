import { apiBlob, apiRequest, apiUpload } from './http'
import type { Attachment, Charge, ChargeInput, ForecastItem, Occurrence } from './types'

const charges = (householdId: string) => `/households/${householdId}/charges`
const occurrences = (householdId: string, chargeId: string) => `${charges(householdId)}/${chargeId}/occurrences`
const invoices = (householdId: string, chargeId: string, occurrenceId: string) =>
  `${occurrences(householdId, chargeId)}/${occurrenceId}/invoices`

export function listCharges(householdId: string): Promise<Charge[]> {
  return apiRequest('GET', charges(householdId))
}

export function addCharge(householdId: string, charge: ChargeInput): Promise<Charge> {
  return apiRequest('POST', charges(householdId), charge)
}

export function updateCharge(householdId: string, chargeId: string, charge: ChargeInput): Promise<Charge> {
  return apiRequest('PUT', `${charges(householdId)}/${chargeId}`, charge)
}

export function terminateCharge(householdId: string, chargeId: string): Promise<void> {
  return apiRequest('DELETE', `${charges(householdId)}/${chargeId}`)
}

/** Échéances encore attendues entre deux jours inclus (« 2026-11-01 »), dépenses et rentrées. */
export function forecast(householdId: string, from: string, to: string): Promise<ForecastItem[]> {
  return apiRequest('GET', `${charges(householdId)}/forecast?from=${from}&to=${to}`)
}

export function listOccurrences(householdId: string, chargeId: string): Promise<Occurrence[]> {
  return apiRequest('GET', occurrences(householdId, chargeId))
}

export function confirmOccurrence(
  householdId: string,
  chargeId: string,
  occurrenceId: string,
  amountCents: number,
): Promise<Occurrence> {
  return apiRequest('POST', `${occurrences(householdId, chargeId)}/${occurrenceId}/confirm`, { amountCents })
}

export function skipOccurrence(householdId: string, chargeId: string, occurrenceId: string): Promise<Occurrence> {
  return apiRequest('POST', `${occurrences(householdId, chargeId)}/${occurrenceId}/skip`)
}

export function listInvoices(householdId: string, chargeId: string, occurrenceId: string): Promise<Attachment[]> {
  return apiRequest('GET', invoices(householdId, chargeId, occurrenceId))
}

export function uploadInvoice(
  householdId: string,
  chargeId: string,
  occurrenceId: string,
  file: File,
  onprogress: (ratio: number) => void,
): Promise<Attachment> {
  return apiUpload(invoices(householdId, chargeId, occurrenceId), file, onprogress)
}

/** Contenu de la facture, à ouvrir via URL.createObjectURL. */
export function invoiceContent(householdId: string, chargeId: string, occurrenceId: string, id: string): Promise<Blob> {
  return apiBlob(`${invoices(householdId, chargeId, occurrenceId)}/${id}/content`)
}

export function removeInvoice(householdId: string, chargeId: string, occurrenceId: string, id: string): Promise<void> {
  return apiRequest('DELETE', `${invoices(householdId, chargeId, occurrenceId)}/${id}`)
}
