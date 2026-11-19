<script lang="ts">
  import { displayName } from '../text'
  import { Check, ChevronDown, House, LogOut, Plus, UserCog } from '@lucide/svelte'
  import { accountConsoleUrl, logout } from '../auth/auth'
  import type { HouseholdSummary, UserProfile } from '../api/types'
  import Modal from '../ui/molecules/Modal.svelte'

  // En-tête : foyer affiché (changement de foyer dans une modale) et profil (déconnexion).
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

  let choosingHousehold = $state(false)
  let showingProfile = $state(false)
  const current = $derived(households.find((h) => h.id === selectedId))
  const initial = $derived(profile.displayName.trim().charAt(0).toUpperCase())

  function choose(householdId: string) {
    choosingHousehold = false
    onselect(householdId)
  }

  function add() {
    choosingHousehold = false
    onadd()
  }
</script>

<header class="app-header">
  <button class="household" onclick={() => (choosingHousehold = true)}>
    <span class="logo" aria-hidden="true"><House size={22} color="#ffffff" /></span>
    <span class="names">
      <span class="caption">Foyer</span>
      <span class="name">{current ? displayName(current.name) : 'Choisir un foyer'} <ChevronDown size={16} strokeWidth={2.6} /></span>
    </span>
  </button>
  <button class="avatar" aria-label="Profil de {profile.displayName}" onclick={() => (showingProfile = true)}>
    {initial}
  </button>
</header>

<Modal bind:open={choosingHousehold} title="Mes foyers">
  <ul class="choices">
    {#each households as household (household.id)}
      <li>
        <button class="ghost choice" aria-pressed={household.id === selectedId} onclick={() => choose(household.id)}>
          <span>{displayName(household.name)}</span>
          {#if household.id === selectedId}<Check size={20} strokeWidth={2.6} />{/if}
        </button>
      </li>
    {/each}
  </ul>
  <button class="soft" onclick={add}><Plus size={18} /> Créer ou rejoindre un foyer</button>
</Modal>

<Modal bind:open={showingProfile} title="Mon profil">
  <div class="profile">
    <span class="avatar big" aria-hidden="true">{initial}</span>
    <strong>{profile.displayName}</strong>
    <span class="muted">{profile.email}</span>
  </div>
  <button class="soft" onclick={() => (window.location.href = accountConsoleUrl())}>
    <UserCog size={18} /> Gérer mon compte
  </button>
  <p class="muted hint">Mot de passe, nom, adresse e-mail et appareils connectés.</p>
  <button class="danger" onclick={logout}><LogOut size={18} /> Se déconnecter</button>
</Modal>

<style>
  .app-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 20px 20px 8px;
    max-width: 760px;
    margin: 0 auto;
  }
  .household {
    background: transparent;
    color: var(--color-text);
    padding: 0;
    gap: 10px;
    min-width: 0;
  }
  .logo {
    width: 40px;
    height: 40px;
    border-radius: var(--radius-sm);
    background: var(--color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .names {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    min-width: 0;
  }
  .caption {
    font-size: 0.75rem;
    font-weight: 600;
    color: var(--color-text-muted);
  }
  .name {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 1.1rem;
    font-weight: 800;
    max-width: 60vw;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .avatar {
    width: 44px;
    height: 44px;
    padding: 0;
    border-radius: 50%;
    background: #ffe4d6;
    color: #9a3412;
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .avatar.big {
    width: 64px;
    height: 64px;
    font-size: 1.6rem;
  }
  .choices {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .choice {
    width: 100%;
    justify-content: space-between;
    min-height: 52px;
  }
  .choice[aria-pressed='true'] {
    color: var(--color-primary-text);
  }
  .hint {
    margin-top: -8px;
    font-size: 0.85rem;
    text-align: center;
  }
  .profile {
    display: grid;
    justify-items: center;
    gap: 6px;
    text-align: center;
  }
</style>
