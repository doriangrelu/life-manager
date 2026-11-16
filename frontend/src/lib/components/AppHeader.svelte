<script lang="ts">
  import { logout } from '../auth/auth'
  import type { HouseholdSummary, UserProfile } from '../api/types'

  // Barre du haut : choix du foyer affiché, ajout d'un foyer, utilisateur et déconnexion.
  let {
    profile,
    households,
    selectedId,
    onselect,
    onadd,
  }: {
    profile: UserProfile
    households: HouseholdSummary[]
    selectedId: string | null
    onselect: (householdId: string) => void
    onadd: () => void
  } = $props()
</script>

<header class="header">
  <div class="header-row">
    <strong class="brand">Life Manager</strong>
    <span class="user">
      {profile.displayName}
      <button class="link" onclick={logout}>Se déconnecter</button>
    </span>
  </div>
  {#if households.length > 0}
    <div class="header-row">
      <label class="selector">
        <span class="visually-hidden">Foyer affiché</span>
        <select value={selectedId} onchange={(e) => onselect(e.currentTarget.value)}>
          {#each households as household (household.id)}
            <option value={household.id}>{household.name}</option>
          {/each}
        </select>
      </label>
      <button class="secondary" onclick={onadd}>+ Foyer</button>
    </div>
  {/if}
</header>

<style>
  .header {
    background: var(--brand);
    color: var(--on-brand);
    padding: 12px 16px;
    display: grid;
    gap: 10px;
  }
  .header-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    max-width: 720px;
    width: 100%;
    margin: 0 auto;
  }
  .brand {
    font-size: 1.1rem;
  }
  .user {
    display: flex;
    gap: 12px;
    align-items: center;
    font-size: 0.9rem;
  }
  .user .link {
    color: var(--on-brand);
  }
  .selector {
    flex: 1;
  }
  .selector select {
    width: 100%;
  }
</style>
