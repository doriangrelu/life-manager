<script lang="ts">
  import { ChevronLeft, Pencil, Trash2 } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import {
    deleteMaintenance,
    listMaintenance,
    recordMaintenance,
    removeVehicle,
    updateVehicle,
  } from '../../api/vehicleApi'
  import type { MaintenanceEntry, MaintenanceInput, Vehicle, VehicleInput } from '../../api/types'
  import { formatDay, kindLabel } from '../../vehicles'
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'
  import Modal from '../../ui/Modal.svelte'
  import AttachmentsDialog from './AttachmentsDialog.svelte'
  import DeadlineBadge from './DeadlineBadge.svelte'
  import MaintenanceEntryCard from './MaintenanceEntryCard.svelte'
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
  let error = $state<string | null>(null)
  let editing = $state(false)
  let addingEntry = $state(false)
  let confirmingRemoval = $state(false)
  let deletingEntry = $state<MaintenanceEntry | null>(null)
  let confirmingEntryDeletion = $state(false)
  let attachmentsOf = $state<MaintenanceEntry | null>(null)
  let attachmentsOpen = $state(false)

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
    run(async () => {
      await removeVehicle(householdId, vehicle.id)
      onremoved()
    })
  }

  async function addEntry(input: MaintenanceInput): Promise<boolean> {
    const done = await run(async () => {
      await recordMaintenance(householdId, vehicle.id, input)
      entries = await listMaintenance(householdId, vehicle.id)
    })
    if (done) {
      addingEntry = false
    }
    return done
  }

  function showAttachments(entry: MaintenanceEntry) {
    attachmentsOf = entry
    attachmentsOpen = true
  }

  // Après un ajout ou une suppression de fichier : le nombre affiché sur la carte suit.
  function reloadEntries() {
    run(async () => (entries = await listMaintenance(householdId, vehicle.id)))
  }

  function askDeleteEntry(entry: MaintenanceEntry) {
    deletingEntry = entry
    confirmingEntryDeletion = true
  }

  function deleteEntry() {
    const entry = deletingEntry
    if (entry) {
      run(async () => {
        await deleteMaintenance(householdId, vehicle.id, entry.id)
        entries = entries.filter((e) => e.id !== entry.id)
      })
    }
  }
</script>

<div class="top">
  <button class="ghost icon" aria-label="Retour aux véhicules" onclick={onclose}>
    <ChevronLeft size={20} strokeWidth={2.4} />
  </button>
  <div class="identity">
    <h1>{vehicle.name}</h1>
    {#if vehicle.licensePlate}<span class="plate">{vehicle.licensePlate}</span>{/if}
  </div>
  <button class="ghost icon" aria-label="Modifier le véhicule" onclick={() => (editing = true)}>
    <Pencil size={18} />
  </button>
</div>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

<div class="deadlines">
  <DeadlineBadge large label="Contrôle technique" due={vehicle.technicalInspectionDue} />
  <DeadlineBadge large label="Révision" due={vehicle.serviceDue} />
</div>

<section class="stack">
  <div class="section-title">
    <h2>Carnet d'entretien</h2>
    <button class="soft" onclick={() => (addingEntry = true)}>+ Opération</button>
  </div>
  {#if entries.length === 0}
    <p class="muted">Aucune opération pour l'instant.</p>
  {:else}
    <ul class="entries">
      {#each entries as entry (entry.id)}
        <li>
          <MaintenanceEntryCard
            {entry}
            ondelete={() => askDeleteEntry(entry)}
            onattachments={() => showAttachments(entry)}
          />
        </li>
      {/each}
    </ul>
  {/if}
</section>

<button class="danger remove" onclick={() => (confirmingRemoval = true)}>
  <Trash2 size={18} /> Supprimer le véhicule
</button>

<Modal bind:open={editing} title="Modifier le véhicule">
  <VehicleForm {vehicle} onsave={save} />
</Modal>

<Modal bind:open={addingEntry} title="Nouvelle opération">
  <MaintenanceForm onsave={addEntry} />
</Modal>

<ConfirmDialog
  bind:open={confirmingRemoval}
  title="Supprimer « {vehicle.name} » ?"
  message="Le véhicule et tout son carnet d'entretien seront définitivement supprimés."
  onconfirm={remove}
/>

{#if attachmentsOf}
  <AttachmentsDialog
    bind:open={attachmentsOpen}
    {householdId}
    vehicleId={vehicle.id}
    entry={attachmentsOf}
    onchanged={reloadEntries}
  />
{/if}

<ConfirmDialog
  bind:open={confirmingEntryDeletion}
  title="Supprimer l'opération ?"
  message={deletingEntry ? `« ${kindLabel(deletingEntry.kind)} » du ${formatDay(deletingEntry.performedOn)} sera définitivement supprimée.` : ''}
  onconfirm={deleteEntry}
/>

<style>
  .top {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .identity {
    flex: 1;
    display: grid;
    gap: 4px;
    min-width: 0;
  }
  .plate {
    justify-self: start;
    padding: 1px 8px;
    border: 1.5px solid currentColor;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 800;
    letter-spacing: 0.06em;
  }
  .deadlines {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
  .entries {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .remove {
    justify-self: center;
  }
</style>
