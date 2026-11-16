<script lang="ts">
  import type { UserProfile } from '../api/types'
  import BudgetView from './budget/BudgetView.svelte'
  import HouseholdView from './HouseholdView.svelte'
  import RemindersBanner from './RemindersBanner.svelte'
  import ShoppingView from './shopping/ShoppingView.svelte'
  import VehiclesView from './vehicle/VehiclesView.svelte'

  // Le foyer sélectionné, en onglets : budget (par défaut), courses, véhicules et fiche du foyer.
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  type Tab = 'budget' | 'shopping' | 'vehicles' | 'household'
  const tabs: { id: Tab; label: string }[] = [
    { id: 'budget', label: 'Budget' },
    { id: 'shopping', label: 'Courses' },
    { id: 'vehicles', label: 'Véhicules' },
    { id: 'household', label: 'Foyer' },
  ]

  let tab = $state<Tab>('budget')
</script>

<RemindersBanner {householdId} refreshKey={tab} />

<nav class="tabs" aria-label="Sections du foyer" style:--count={tabs.length}>
  {#each tabs as item (item.id)}
    <button class:active={tab === item.id} aria-pressed={tab === item.id} onclick={() => (tab = item.id)}>
      {item.label}
    </button>
  {/each}
</nav>

{#if tab === 'budget'}
  <BudgetView {householdId} {profile} />
{:else if tab === 'shopping'}
  <ShoppingView {householdId} />
{:else if tab === 'vehicles'}
  <VehiclesView {householdId} />
{:else}
  <HouseholdView {householdId} />
{/if}

<style>
  .tabs {
    display: grid;
    grid-template-columns: repeat(var(--count), 1fr);
    gap: 8px;
    margin-bottom: 16px;
  }
  .tabs button {
    background: var(--surface);
    color: var(--text);
    border: 1px solid var(--border);
    padding: 10px 4px;
  }
  .tabs button.active {
    background: var(--brand);
    color: var(--on-brand);
    border-color: var(--brand);
  }
</style>
