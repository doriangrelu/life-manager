<script lang="ts">
  import type { UserProfile } from '../api/types'
  import BudgetView from './budget/BudgetView.svelte'
  import HouseholdView from './HouseholdView.svelte'

  // Le foyer sélectionné, en deux onglets : son budget (par défaut) et sa fiche (membres, invitation).
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  let tab = $state<'budget' | 'household'>('budget')
</script>

<nav class="tabs" aria-label="Sections du foyer">
  <button class:active={tab === 'budget'} aria-pressed={tab === 'budget'} onclick={() => (tab = 'budget')}>
    Budget
  </button>
  <button class:active={tab === 'household'} aria-pressed={tab === 'household'} onclick={() => (tab = 'household')}>
    Foyer
  </button>
</nav>

{#if tab === 'budget'}
  <BudgetView {householdId} {profile} />
{:else}
  <HouseholdView {householdId} />
{/if}

<style>
  .tabs {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 16px;
  }
  .tabs button {
    background: var(--surface);
    color: var(--text);
    border: 1px solid var(--border);
  }
  .tabs button.active {
    background: var(--brand);
    color: var(--on-brand);
    border-color: var(--brand);
  }
</style>
