<script lang="ts">
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import { listMove, ms, rise } from '../../motion'
  import { ChevronLeft, Pencil, Trash2 } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import {
    attachmentContent,
    deleteMaintenance,
    listAttachments,
    listMaintenance,
    listVehicles,
    recordMaintenance,
    removeAttachment,
    removeVehicle,
    sendMaintenanceToBudget,
    updateVehicle,
    uploadAttachment,
  } from '../../api/vehicleApi'
  import type { MaintenanceEntry, MaintenanceInput, Vehicle, VehicleInput } from '../../api/types'
  import { defaultBudgetCategory, entryLabel, formatDay, type BudgetContext } from '../../vehicles'
  import { listCategories } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import { formatCents } from '../../money'
  import BudgetChoiceFields from './BudgetChoiceFields.svelte'
  import WearPartsSection from './WearPartsSection.svelte'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import AttachmentsDialog from '../AttachmentsDialog.svelte'
  import DeadlineBadge from './DeadlineBadge.svelte'
  import MaintenanceEntryCard from './MaintenanceEntryCard.svelte'
  import MaintenanceForm from './MaintenanceForm.svelte'
  import VehicleForm from './VehicleForm.svelte'

  // Fiche d'un véhicule : échéances, modification, suppression et carnet d'entretien.
  let {
    householdId,
    currentUserId,
    vehicle = $bindable(),
    onremoved,
    onclose,
  }: {
    householdId: string
    currentUserId: string
    vehicle: Vehicle
    onremoved: () => void
    onclose: () => void
  } = $props()

  // Catégories et membres, pour « Ajouter au budget » (sans eux, le carnet marche quand même).
  let budget = $state<BudgetContext | null>(null)
  let budgetOf = $state<MaintenanceEntry | null>(null)
  let budgetOpen = $state(false)
  let budgetCategoryId = $state('')
  let budgetPayerId = $state('')
  let budgetBusy = $state(false)
  let budgetError = $state<string | null>(null)

  let entries = $state<MaintenanceEntry[]>([])
  let entriesLoaded = $state(false)
  let error = $state<string | null>(null)
  let editing = $state(false)
  let addingEntry = $state(false)
  let confirmingRemoval = $state(false)
  let deletingEntry = $state<MaintenanceEntry | null>(null)
  let confirmingEntryDeletion = $state(false)
  let attachmentsOf = $state<MaintenanceEntry | null>(null)
  let attachmentsOpen = $state(false)

  onMount(() => {
    run(async () => {
      entries = await listMaintenance(householdId, vehicle.id)
      entriesLoaded = true
    })
    loadBudgetContext()
  })

  async function loadBudgetContext() {
    try {
      const [household, categories] = await Promise.all([getHousehold(householdId), listCategories(householdId)])
      budget = { categories, members: household.members, currentUserId }
    } catch {
      budget = null // le carnet reste utilisable, sans l'ajout au budget
    }
  }

  function askBudget(entry: MaintenanceEntry) {
    if (!budget) {
      return
    }
    budgetOf = entry
    budgetCategoryId = defaultBudgetCategory(budget.categories)
    budgetPayerId = currentUserId
    budgetError = null
    budgetOpen = true
  }

  async function sendToBudget() {
    const entry = budgetOf
    if (!entry) {
      return
    }
    budgetBusy = true
    budgetError = null
    try {
      await sendMaintenanceToBudget(householdId, vehicle.id, entry.id, {
        categoryId: budgetCategoryId,
        payerId: budgetPayerId,
      })
      entries = entries.map((e) => (e.id === entry.id ? { ...e, inBudget: true } : e))
      budgetOpen = false
    } catch (e) {
      budgetError = e instanceof Error ? e.message : String(e)
    } finally {
      budgetBusy = false
    }
  }

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

  // Un refus remonte au formulaire, qui l'affiche dans sa fenêtre.
  async function save(input: VehicleInput) {
    vehicle = await updateVehicle(householdId, vehicle.id, input)
    editing = false
  }

  function remove() {
    run(async () => {
      await removeVehicle(householdId, vehicle.id)
      onremoved()
    })
  }

  // L'écriture d'abord : un refus remonte au formulaire. Une fois enregistrée, la fenêtre se ferme
  // tout de suite (un nouvel envoi créerait un doublon), puis l'écran se recharge.
  async function addEntry(input: MaintenanceInput): Promise<boolean> {
    await recordMaintenance(householdId, vehicle.id, input)
    addingEntry = false
    await run(async () => {
      entries = await listMaintenance(householdId, vehicle.id)
      // Un contrôle technique ou une révision a pu reporter une échéance : on relit le véhicule.
      vehicle = (await listVehicles(householdId)).find((v) => v.id === vehicle.id) ?? vehicle
    })
    return true
  }

  // Une pièce posée a pu relever le kilométrage : on relit la fiche.
  function reloadVehicle() {
    run(async () => {
      vehicle = (await listVehicles(householdId)).find((v) => v.id === vehicle.id) ?? vehicle
    })
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

<WearPartsSection {householdId} {vehicle} onmileage={reloadVehicle} />

<section class="stack">
  <div class="section-title">
    <h2>Carnet d'entretien</h2>
    <button class="soft" onclick={() => (addingEntry = true)}>+ Opération</button>
  </div>
  {#if !entriesLoaded && !error}
    <Skeleton variant="cards" count={2} />
  {:else if entries.length === 0}
    <p class="muted">Aucune opération pour l'instant.</p>
  {:else}
    <ul class="entries">
      {#each entries as entry (entry.id)}
        <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
          <MaintenanceEntryCard
            {entry}
            ondelete={() => askDeleteEntry(entry)}
            onattachments={() => showAttachments(entry)}
            onbudget={budget ? () => askBudget(entry) : undefined}
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
  <MaintenanceForm onsave={addEntry} {budget} />
</Modal>

<Modal bind:open={budgetOpen} title="Ajouter au budget">
  {#if budgetOf && budget}
    <div class="stack">
      <p class="muted">
        {entryLabel(budgetOf)} du {formatDay(budgetOf.performedOn)} :
        <strong>{formatCents(budgetOf.costCents ?? 0)}</strong> en dépense du mois de l'opération.
      </p>
      <BudgetChoiceFields context={budget} bind:categoryId={budgetCategoryId} bind:payerId={budgetPayerId} />
      {#if budgetError}
        <p class="error" role="alert">{budgetError}</p>
      {/if}
      <button class="large" disabled={budgetBusy || !budgetCategoryId} aria-busy={budgetBusy} onclick={sendToBudget}>
        Ajouter au budget
      </button>
    </div>
  {/if}
</Modal>

<ConfirmDialog
  bind:open={confirmingRemoval}
  title="Supprimer « {vehicle.name} » ?"
  message="Le véhicule et tout son carnet d'entretien seront définitivement supprimés."
  onconfirm={remove}
/>

{#if attachmentsOf}
  {@const entryId = attachmentsOf.id}
  <AttachmentsDialog
    bind:open={attachmentsOpen}
    subtitle="{entryLabel(attachmentsOf)} du {formatDay(attachmentsOf.performedOn)}"
    files={{
      list: () => listAttachments(householdId, vehicle.id, entryId),
      upload: (file, onprogress) => uploadAttachment(householdId, vehicle.id, entryId, file, onprogress),
      content: (id) => attachmentContent(householdId, vehicle.id, entryId, id),
      remove: (id) => removeAttachment(householdId, vehicle.id, entryId, id),
    }}
    onchanged={reloadEntries}
  />
{/if}

<ConfirmDialog
  bind:open={confirmingEntryDeletion}
  title="Supprimer l'opération ?"
  message={deletingEntry ? `« ${entryLabel(deletingEntry)} » du ${formatDay(deletingEntry.performedOn)} sera définitivement supprimée.` : ''}
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
