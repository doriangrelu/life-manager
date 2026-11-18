<script lang="ts">
  import type { UserProfile } from '../api/types'
  import MainNav, { type Section } from '../ui/MainNav.svelte'
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

  let section = $state<Section>('budget')
</script>

<RemindersBanner {householdId} refreshKey={section} />

{#if section === 'budget'}
  <BudgetView {householdId} {profile} />
{:else if section === 'shopping'}
  <ShoppingView {householdId} />
{:else if section === 'vehicles'}
  <VehiclesView {householdId} />
{:else}
  <HouseholdView {householdId} {onaddhousehold} />
{/if}

<MainNav active={section} onselect={(next) => (section = next)} />
