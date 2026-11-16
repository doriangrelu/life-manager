import { apiRequest } from './http'
import type { MaintenanceEntry, MaintenanceInput, Vehicle, VehicleInput } from './types'

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
