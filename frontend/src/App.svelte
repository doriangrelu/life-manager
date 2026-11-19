<script lang="ts">
  import { onMount } from 'svelte'
  import { get } from 'svelte/store'
  import { navigate, route } from './lib/route'
  import { accessToken, completeLoginIfReturning, login } from './lib/auth/auth'
  import { listHouseholds } from './lib/api/householdApi'
  import { whoami } from './lib/api/identityApi'
  import type { HouseholdSummary, UserProfile } from './lib/api/types'
  import { rememberedHouseholdId, rememberHouseholdId } from './lib/preferences'
  import AppHeader from './lib/components/AppHeader.svelte'
  import CreateOrJoin from './lib/components/CreateOrJoin.svelte'
  import HouseholdTabs from './lib/components/HouseholdTabs.svelte'
  import Modal from './lib/ui/molecules/Modal.svelte'
  import Skeleton from './lib/ui/atoms/Skeleton.svelte'

  let profile = $state<UserProfile | null>(null)
  let households = $state<HouseholdSummary[]>([])
  let selectedId = $state<string | null>(null)
  let addingHousehold = $state(false)
  let error = $state<string | null>(null)

  // Au lancement : connexion si besoin, puis profil (domaine identité) et foyers (domaine foyer).
  onMount(async () => {
    try {
      await completeLoginIfReturning()
      if (!(await accessToken())) {
        await login()
        return
      }
      profile = await whoami()
      households = await listHouseholds()
      select(initialHouseholdId())
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  })

  // Dernier foyer choisi sur cet appareil s'il existe encore, sinon le premier.
  function initialHouseholdId(): string | null {
    const remembered = rememberedHouseholdId()
    return households.some((h) => h.id === remembered) ? remembered : (households[0]?.id ?? null)
  }

  function select(householdId: string | null) {
    // Changement de foyer : même section, sans mois ni véhicule (propres à l'ancien foyer).
    if (selectedId !== null && householdId !== selectedId) {
      navigate({ section: get(route).section })
    }
    selectedId = householdId
    addingHousehold = false
    if (householdId) {
      rememberHouseholdId(householdId)
    }
  }

  function onHouseholdAdded(household: HouseholdSummary) {
    households = [...households, household]
    select(household.id)
  }
</script>

{#if error}
  <main class="content"><p class="error" role="alert">{error}</p></main>
{:else if !profile}
  <main class="content stack"><Skeleton variant="hero" /><Skeleton variant="rows" /></main>
{:else if !selectedId}
  <main class="content welcome">
    <h1>Bienvenue, {profile.displayName}</h1>
    <p class="muted">Créez votre foyer ou rejoignez celui d'un proche avec son code d'invitation.</p>
    <CreateOrJoin onadded={onHouseholdAdded} />
  </main>
{:else}
  <div class="shell">
    <AppHeader {profile} {households} {selectedId} onselect={select} onadd={() => (addingHousehold = true)} />
    <main class="content">
      {#key selectedId}
        <HouseholdTabs householdId={selectedId} {profile} onaddhousehold={() => (addingHousehold = true)} />
      {/key}
    </main>
  </div>
  <Modal bind:open={addingHousehold} title="Créer ou rejoindre un foyer">
    <CreateOrJoin onadded={onHouseholdAdded} />
  </Modal>
{/if}

<style>
  .content {
    max-width: 760px;
    margin: 0 auto;
    padding: 8px 20px calc(var(--nav-height) + 110px);
    display: grid;
    gap: 16px;
  }
  .welcome {
    padding-top: 48px;
  }
  @media (min-width: 900px) {
    /* Place pour le menu latéral. */
    .shell {
      padding-left: 240px;
    }
    .content {
      padding-bottom: 48px;
    }
  }
</style>
