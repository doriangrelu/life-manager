<script lang="ts">
  import type { UserProfile } from '../api/types'
  import { rise } from '../motion'
  import { navigate, route } from '../route'
  import MainNav from '../ui/MainNav.svelte'
  import BudgetView from './budget/BudgetView.svelte'
  import HouseholdView from './HouseholdView.svelte'
  import RemindersBanner from './RemindersBanner.svelte'
  import ShoppingView from './shopping/ShoppingView.svelte'
  import VehiclesView from './vehicle/VehiclesView.svelte'

  // Le foyer sélectionné : ses quatre sections et la navigation entre elles.
  let {
    householdId,
    profile,
    onaddhousehold,
  }: { householdId: string; profile: UserProfile; onaddhousehold: () => void } = $props()

  // La section vient de l'adresse.
  const section = $derived($route.section)
</script>

<RemindersBanner {householdId} refreshKey={section} />

<!-- Changement de section : le nouvel écran apparaît en fondu (l'ancien disparaît aussitôt). -->
{#key section}
  <div class="stack" in:rise>
    {#if section === 'budget'}
      <BudgetView {householdId} {profile} />
    {:else if section === 'shopping'}
      <ShoppingView {householdId} />
    {:else if section === 'vehicles'}
      <VehiclesView {householdId} />
    {:else}
      <HouseholdView {householdId} {onaddhousehold} />
    {/if}
  </div>
{/key}

<MainNav active={section} onselect={(next) => navigate({ section: next })} />
