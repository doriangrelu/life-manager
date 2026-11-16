<script lang="ts">
  import { onMount } from 'svelte'
  import {
    deleteMaintenance,
    listMaintenance,
    recordMaintenance,
    removeVehicle,
    updateVehicle,
  } from '../../api/vehicleApi'
  import type { MaintenanceEntry, MaintenanceInput, Vehicle, VehicleInput } from '../../api/types'
  import { formatCents } from '../../money'
  import { formatDay, kindLabel } from '../../vehicles'
  import DeadlineBadge from './DeadlineBadge.svelte'
  import MaintenanceForm from './MaintenanceForm.svelte'
  import VehicleForm from './VehicleForm.svelte'

  // Fiche d'un véhicule : échéances, modification, suppression et carnet d'entretien.
  let {
    householdId,
    vehicle = $bindable(),
    onremoved,
    onclose,
  }: { householdId: string; vehicle: Vehicle; onremoved: () => void; onclose: () => void } = $props()

  let entries = $state<MaintenanceEntry[]>([])
  let editing = $state(false)
  let error = $state<string | null>(null)

  onMount(() => run(async () => (entries = await listMaintenance(householdId, vehicle.id))))

  // Exécute une action, affiche son erreur éventuelle et dit si elle a réussi.
  async function run(action: () => Promise<unknown>): Promise<boolean> {
    error = null
    try {
      await action()
      return true
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      return false
    }
  }

  async function save(input: VehicleInput) {
    if (await run(async () => (vehicle = await updateVehicle(householdId, vehicle.id, input)))) {
      editing = false
    }
  }

  function remove() {
    if (confirm(`Supprimer « ${vehicle.name} » et tout son carnet ? C'est définitif.`)) {
      run(async () => {
        await removeVehicle(householdId, vehicle.id)
        onremoved()
      })
    }
  }

  function addEntry(input: MaintenanceInput): Promise<boolean> {
    return run(async () => {
      await recordMaintenance(householdId, vehicle.id, input)
      entries = await listMaintenance(householdId, vehicle.id)
    })
  }

  function deleteEntry(entry: MaintenanceEntry) {
    if (confirm(`Supprimer « ${kindLabel(entry.kind)} » du ${formatDay(entry.performedOn)} ?`)) {
      run(async () => {
        await deleteMaintenance(householdId, vehicle.id, entry.id)
        entries = entries.filter((e) => e.id !== entry.id)
      })
    }
  }
</script>

<section class="stack">
  <button class="link back" onclick={onclose}>‹ Tous les véhicules</button>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if editing}
    <VehicleForm {vehicle} onsave={save} oncancel={() => (editing = false)} />
  {:else}
    <div class="card stack">
      <h1>{vehicle.name}</h1>
      {#if vehicle.licensePlate}<p class="muted">{vehicle.licensePlate}</p>{/if}
      <div class="badges">
        <DeadlineBadge label="Contrôle technique" due={vehicle.technicalInspectionDue} />
        <DeadlineBadge label="Révision" due={vehicle.serviceDue} />
      </div>
      <div class="actions">
        <button class="secondary" onclick={() => (editing = true)}>Modifier</button>
        <button class="link danger" onclick={remove}>Supprimer</button>
      </div>
    </div>
  {/if}

  <MaintenanceForm onsave={addEntry} />

  <div class="card stack">
    <h2>Carnet d'entretien</h2>
    {#if entries.length === 0}
      <p class="muted">Aucune opération pour l'instant.</p>
    {:else}
      <ul class="entries">
        {#each entries as entry (entry.id)}
          <li>
            <div>
              <strong>{kindLabel(entry.kind)}</strong> — {formatDay(entry.performedOn)}
              <div class="muted">
                {[
                  entry.mileageKm !== null ? `${entry.mileageKm.toLocaleString('fr-FR')} km` : null,
                  entry.costCents !== null ? formatCents(entry.costCents) : null,
                  entry.note,
                ]
                  .filter(Boolean)
                  .join(' · ')}
              </div>
            </div>
            <button class="link" aria-label="Supprimer l'opération" onclick={() => deleteEntry(entry)}>Supprimer</button>
          </li>
        {/each}
      </ul>
    {/if}
  </div>
</section>

<style>
  .back {
    justify-self: start;
  }
  .badges {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .actions {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .danger {
    color: var(--error);
  }
  .entries {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 12px;
  }
  .entries li {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 12px;
  }
</style>
