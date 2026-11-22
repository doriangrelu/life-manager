<script lang="ts">
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import { listMove, ms, rise } from '../../motion'
  import { Car, ChevronRight } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { listVehicles, registerVehicle } from '../../api/vehicleApi'
  import type { Vehicle, VehicleInput } from '../../api/types'
  import { navigate, route } from '../../route'
  import Fab from '../../ui/molecules/Fab.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import DeadlineBadge from './DeadlineBadge.svelte'
  import VehicleDetail from './VehicleDetail.svelte'
  import VehicleForm from './VehicleForm.svelte'

  // Onglet Véhicules : la liste avec les échéances en évidence, l'ajout, et l'accès à chaque fiche.
  let { householdId, currentUserId }: { householdId: string; currentUserId: string } = $props()

  let vehicles = $state<Vehicle[]>([])
  let loaded = $state(false)
  let adding = $state(false)
  let error = $state<string | null>(null)
  // Le véhicule ouvert vient de l'adresse.
  const openedId = $derived($route.vehicleId ?? null)
  const openedIndex = $derived(vehicles.findIndex((v) => v.id === openedId))

  onMount(() => run(load))

  // Véhicule inconnu du foyer (supprimé, autre foyer) : retour à la liste.
  $effect(() => {
    if (loaded && openedId !== null && openedIndex < 0) {
      navigate({ section: 'vehicles' }, { replace: true })
    }
  })

  // Retour à la liste (bouton de la fiche ou Précédent) : liste rafraîchie.
  let wasOpen = false
  $effect(() => {
    const open = openedId !== null
    if (wasOpen && !open) {
      run(load)
    }
    wasOpen = open
  })

  async function load() {
    vehicles = await listVehicles(householdId)
    loaded = true
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Un refus remonte au formulaire, qui l'affiche dans sa fenêtre.
  async function add(input: VehicleInput) {
    await registerVehicle(householdId, input)
    adding = false
    await run(load)
  }

  function open(vehicleId: string) {
    navigate({ section: 'vehicles', vehicleId })
  }

  // Après une suppression, l'adresse du véhicule est remplacée : Précédent n'y ramène pas.
  function close(replace = false) {
    navigate({ section: 'vehicles' }, { replace })
  }
</script>

{#if openedIndex >= 0}
  <div class="stack" in:rise>
  <VehicleDetail
    {householdId}
    {currentUserId}
    bind:vehicle={vehicles[openedIndex]}
    onremoved={() => close(true)}
    onclose={() => close()}
  />
  </div>
{:else}
  <div class="stack" in:rise>
  <h1>Véhicules</h1>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if !loaded && !error}
    <Skeleton variant="cards" count={2} />
  {:else if vehicles.length === 0}
    <p class="muted">Aucun véhicule pour l'instant : touchez + pour en ajouter un.</p>
  {/if}

  <ul class="vehicles">
    {#each vehicles as vehicle (vehicle.id)}
      <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
        <button class="vehicle" onclick={() => open(vehicle.id)}>
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

  </div>
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
  @media (hover: hover) {
    .vehicle:hover {
      filter: none;
      box-shadow: var(--shadow-sm), 0 0 0 2px var(--color-primary-soft);
    }
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
