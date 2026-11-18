<script lang="ts">
  import { Car, ChevronRight } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { listVehicles, registerVehicle } from '../../api/vehicleApi'
  import type { Vehicle, VehicleInput } from '../../api/types'
  import Fab from '../../ui/Fab.svelte'
  import Modal from '../../ui/Modal.svelte'
  import DeadlineBadge from './DeadlineBadge.svelte'
  import VehicleDetail from './VehicleDetail.svelte'
  import VehicleForm from './VehicleForm.svelte'

  // Onglet Véhicules : la liste avec les échéances en évidence, l'ajout, et l'accès à chaque fiche.
  let { householdId }: { householdId: string } = $props()

  let vehicles = $state<Vehicle[]>([])
  let adding = $state(false)
  let openedId = $state<string | null>(null)
  let error = $state<string | null>(null)
  const openedIndex = $derived(vehicles.findIndex((v) => v.id === openedId))

  onMount(() => run(load))

  async function load() {
    vehicles = await listVehicles(householdId)
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  async function add(input: VehicleInput) {
    await run(async () => {
      await registerVehicle(householdId, input)
      adding = false
      await load()
    })
  }

  function close() {
    openedId = null
    run(load)
  }
</script>

{#if openedIndex >= 0}
  <VehicleDetail {householdId} bind:vehicle={vehicles[openedIndex]} onremoved={close} onclose={close} />
{:else}
  <h1>Véhicules</h1>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if vehicles.length === 0}
    <p class="muted">Aucun véhicule pour l'instant : touchez + pour en ajouter un.</p>
  {/if}

  <ul class="vehicles">
    {#each vehicles as vehicle (vehicle.id)}
      <li>
        <button class="vehicle" onclick={() => (openedId = vehicle.id)}>
          <span class="bubble" aria-hidden="true"><Car size={22} /></span>
          <span class="text">
            <span class="title">
              <strong>{vehicle.name}</strong>
              {#if vehicle.licensePlate}<span class="plate">{vehicle.licensePlate}</span>{/if}
            </span>
            <span class="badges">
              <DeadlineBadge label="CT" due={vehicle.technicalInspectionDue} />
              <DeadlineBadge label="Révision" due={vehicle.serviceDue} />
            </span>
          </span>
          <ChevronRight size={20} />
        </button>
      </li>
    {/each}
  </ul>

  <Fab label="Ajouter un véhicule" onclick={() => (adding = true)} />
{/if}

<Modal bind:open={adding} title="Nouveau véhicule">
  <VehicleForm vehicle={null} onsave={add} />
</Modal>

<style>
  .vehicles {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .vehicle {
    width: 100%;
    justify-content: flex-start;
    gap: 12px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
    color: var(--color-text);
    text-align: left;
  }
  .bubble {
    width: 44px;
    height: 44px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .text {
    flex: 1;
    display: grid;
    gap: 8px;
    min-width: 0;
  }
  .title {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
  }
  .plate {
    padding: 1px 8px;
    border: 1.5px solid currentColor;
    border-radius: 6px;
    font-size: 0.75rem;
    font-weight: 800;
    letter-spacing: 0.06em;
  }
  .badges {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }
</style>
