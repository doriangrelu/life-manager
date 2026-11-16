<script lang="ts">
  import { onMount } from 'svelte'
  import { accessToken, completeLoginIfReturning, login } from './lib/auth/auth'
  import { listHouseholds } from './lib/api/householdApi'
  import { whoami } from './lib/api/identityApi'
  import type { HouseholdSummary, UserProfile } from './lib/api/types'
  import { rememberedHouseholdId, rememberHouseholdId } from './lib/preferences'
  import AppHeader from './lib/components/AppHeader.svelte'
  import CreateOrJoin from './lib/components/CreateOrJoin.svelte'
  import HouseholdView from './lib/components/HouseholdView.svelte'

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
  <main class="page"><p class="error">{error}</p></main>
{:else if !profile}
  <main class="page"><p class="muted">Chargement…</p></main>
{:else}
  <AppHeader {profile} {households} {selectedId} onselect={select} onadd={() => (addingHousehold = true)} />
  <main class="page">
    {#if addingHousehold || !selectedId}
      <CreateOrJoin
        onadded={onHouseholdAdded}
        oncancel={selectedId ? () => (addingHousehold = false) : undefined}
      />
    {:else}
      {#key selectedId}
        <HouseholdView householdId={selectedId} />
      {/key}
    {/if}
  </main>
{/if}
