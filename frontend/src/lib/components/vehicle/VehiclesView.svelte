<script lang="ts">
  import { onMount } from 'svelte'
  import { listVehicles, registerVehicle } from '../../api/vehicleApi'
  import type { Vehicle, VehicleInput } from '../../api/types'
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
  <section class="stack">
    <h1>Véhicules</h1>
    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}

    {#if adding}
      <VehicleForm vehicle={null} onsave={add} oncancel={() => (adding = false)} />
    {:else}
      <button onclick={() => (adding = true)}>+ Ajouter un véhicule</button>
    {/if}

    {#if vehicles.length === 0 && !adding}
      <p class="muted">Aucun véhicule pour l'instant.</p>
    {/if}

    {#each vehicles as vehicle (vehicle.id)}
      <button class="card vehicle" onclick={() => (openedId = vehicle.id)}>
        <span class="title">
          <strong>{vehicle.name}</strong>
          {#if vehicle.licensePlate}<span class="muted">{vehicle.licensePlate}</span>{/if}
        </span>
        <span class="badges">
          <DeadlineBadge label="CT" due={vehicle.technicalInspectionDue} />
          <DeadlineBadge label="Révision" due={vehicle.serviceDue} />
        </span>
      </button>
    {/each}
  </section>
{/if}

<style>
  .vehicle {
    display: grid;
    gap: 8px;
    text-align: left;
    color: var(--text);
    width: 100%;
  }
  .title {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }
  .badges {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
</style>
