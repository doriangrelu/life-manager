<script lang="ts">
  import { onMount } from 'svelte'
  import { get } from 'svelte/store'
  import { navigate, route } from './lib/route'
  import { accessToken, completeLoginIfReturning, login } from './lib/auth/auth'
  import { listHouseholds } from './lib/api/householdApi'
  import { whoami } from './lib/api/identityApi'
  import type { HouseholdSummary, UserProfile } from './lib/api/types'
  import { HOUSEHOLD_MAYBE_LOST } from './lib/householdAccess'
  import { legalPage } from './lib/legal'
  import LegalView from './lib/components/legal/LegalView.svelte'
  import TermsGate from './lib/components/legal/TermsGate.svelte'
  import { rememberedHouseholdId, rememberHouseholdId } from './lib/preferences'
  import { displayName } from './lib/text'
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
  // Message après la perte d'un foyer (retiré, supprimé, quitté).
  let notice = $state<string | null>(null)

  // Une réponse 404 sur un foyer : on vérifie qu'on y a toujours accès.
  onMount(() => {
    const onMaybeLost = (event: Event) => {
      if ((event as CustomEvent<string>).detail === selectedId) {
        recheckHouseholds()
      }
    }
    window.addEventListener(HOUSEHOLD_MAYBE_LOST, onMaybeLost)
    return () => window.removeEventListener(HOUSEHOLD_MAYBE_LOST, onMaybeLost)
  })

  /**
   * Recharge les foyers ; si celui affiché n'en fait plus partie, bascule sur un autre (ou sur l'accueil).
   * @param message texte à afficher, sinon un message par défaut s'il a disparu
   */
  async function recheckHouseholds(message?: string) {
    const lost = households.find((h) => h.id === selectedId)
    households = await listHouseholds()
    if (!households.some((h) => h.id === selectedId)) {
      notice = message ?? `Vous n'avez plus accès au foyer « ${lost ? displayName(lost.name) : ''} ».`
      selectedId = null
      select(initialHouseholdId())
    }
  }

  // Au lancement : connexion si besoin, puis profil (domaine identité) et foyers (domaine foyer).
  // Sur une page légale (#/cgu…), rien ne démarre : elle se lit sans compte ; le départ se fait en la quittant.
  let started = false
  $effect(() => {
    if (!$legalPage && !started) {
      started = true
      start()
    }
  })

  async function start() {
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
  }

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

{#if $legalPage}
  <LegalView page={$legalPage} />
{:else if error}
  <main class="content"><p class="error" role="alert">{error}</p></main>
{:else if !profile}
  <main class="content stack"><Skeleton variant="hero" /><Skeleton variant="rows" /></main>
{:else if !profile.termsAccepted}
  <TermsGate version={profile.termsVersion} onaccepted={() => profile && (profile = { ...profile, termsAccepted: true })} />
{:else if !selectedId}
  <main class="content welcome">
    {#if notice}
      <p class="notice" role="status">{notice}</p>
    {/if}
    <h1>Bienvenue, {profile.displayName}</h1>
    <p class="muted">Créez votre foyer ou rejoignez celui d'un proche avec son code d'invitation.</p>
    <CreateOrJoin onadded={onHouseholdAdded} />
  </main>
{:else}
  <div class="shell">
    <AppHeader {profile} {households} {selectedId} onselect={select} onadd={() => (addingHousehold = true)} />
    <main class="content">
      {#if notice}
        <div class="notice" role="status">
          <span>{notice}</span>
          <button class="link" onclick={() => (notice = null)}>OK</button>
        </div>
      {/if}
      {#key selectedId}
        <HouseholdTabs
          householdId={selectedId}
          {profile}
          onaddhousehold={() => (addingHousehold = true)}
          onhouseholdgone={(message) => recheckHouseholds(message)}
          onhouseholdchanged={() => recheckHouseholds()}
        />
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
  /* Information après la perte d'un foyer : visible, sans alarmer. */
  .notice {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin: 0;
    padding: 12px 16px;
    border-radius: var(--radius-lg);
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
    font-weight: 600;
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
