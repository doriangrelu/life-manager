<script lang="ts">
  import { Crown, LogOut, Pencil, Plus, Trash2, UserMinus } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import {
    cancelInvitation,
    deleteHousehold,
    getHousehold,
    issueInvitation,
    leaveHousehold,
    removeMember,
    renameHousehold,
    setIncomeTracking,
    transferOwnership,
  } from '../api/householdApi'
  import type { HouseholdDetails, Member } from '../api/types'
  import { formatDay } from '../dates'
  import { incomeTracking } from '../householdSettings'
  import { displayName } from '../text'
  import AppVersion from '../ui/atoms/AppVersion.svelte'
  import Skeleton from '../ui/atoms/Skeleton.svelte'
  import ConfirmDialog from '../ui/molecules/ConfirmDialog.svelte'
  import TextDialog from '../ui/molecules/TextDialog.svelte'
  import DeleteHouseholdDialog from './DeleteHouseholdDialog.svelte'
  import IncomeTrackingCard from './IncomeTrackingCard.svelte'
  import InvitationCard from './InvitationCard.svelte'

  // Onglet Foyer : ses membres et son propriétaire, l'invitation (propriétaire), quitter ou supprimer le foyer.
  let {
    householdId,
    currentUserId,
    onaddhousehold,
    ongone,
    onrenamed,
  }: {
    householdId: string
    currentUserId: string
    onaddhousehold: () => void
    ongone: (message: string) => void
    onrenamed: () => void
  } = $props()

  let household = $state<HouseholdDetails | null>(null)
  let error = $state<string | null>(null)
  // Gestes à confirmer : un membre visé (retrait, transmission), ou le foyer (départ, renommage, suppression).
  let removing = $state<Member | null>(null)
  let handingOver = $state<Member | null>(null)
  let confirmRemove = $state(false)
  let confirmHandOver = $state(false)
  let confirmLeave = $state(false)
  let renaming = $state(false)
  let deleting = $state(false)

  const isOwner = $derived(household?.ownerId === currentUserId)

  onMount(() => run(load))

  async function load() {
    household = await getHousehold(householdId)
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Réglage du suivi des rentrées : enregistré, puis partagé avec les menus (écran Bilan).
  async function toggleIncomeTracking(enabled: boolean) {
    await run(async () => {
      await setIncomeTracking(householdId, enabled)
      if (household) {
        household.incomeTracking = enabled
      }
      incomeTracking.set(enabled)
    })
  }

  async function newCode() {
    if (household) {
      household.activeInvitation = await issueInvitation(household.id)
    }
  }

  async function cancelCode() {
    await cancelInvitation(householdId)
    if (household) {
      household.activeInvitation = null
    }
  }

  function askRemove(member: Member) {
    removing = member
    confirmRemove = true
  }

  function askHandOver(member: Member) {
    handingOver = member
    confirmHandOver = true
  }

  function remove() {
    const member = removing
    if (member) {
      run(async () => {
        await removeMember(householdId, member.memberId)
        await load()
      })
    }
  }

  function handOver() {
    const member = handingOver
    if (member) {
      run(async () => {
        await transferOwnership(householdId, member.memberId)
        await load()
      })
    }
  }

  function rename(name: string) {
    run(async () => {
      household = await renameHousehold(householdId, name)
      onrenamed()
    })
  }

  function leave() {
    const name = household ? displayName(household.name) : ''
    run(async () => {
      await leaveHousehold(householdId)
      ongone(`Vous avez quitté le foyer « ${name} ».`)
    })
  }

  async function destroy(confirmName: string) {
    const name = household ? displayName(household.name) : ''
    await deleteHousehold(householdId, confirmName)
    ongone(`Le foyer « ${name} » a été supprimé.`)
  }
</script>

{#if !household && error}
  <p class="error" role="alert">{error}</p>
{:else if !household}
  <Skeleton variant="cards" count={2} />
{:else}
  <div class="title">
    <h1>{displayName(household.name)}</h1>
    {#if isOwner}
      <button class="ghost icon" aria-label="Renommer le foyer" onclick={() => (renaming = true)}><Pencil size={18} /></button>
    {/if}
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <section class="card stack">
    <h2>Membres</h2>
    <ul class="members">
      {#each household.members as member (member.memberId)}
        {@const owner = member.memberId === household.ownerId}
        {@const me = member.memberId === currentUserId}
        <li>
          <span class="avatar" aria-hidden="true">{member.displayName.charAt(0).toUpperCase()}</span>
          <span class="who">
            <strong>
              {member.displayName}{me ? ' (vous)' : ''}
              {#if owner}<span class="badge"><Crown size={12} strokeWidth={2.6} /> Propriétaire</span>{/if}
            </strong>
            <span class="muted">membre depuis le {formatDay(member.joinedAt)}</span>
          </span>
          {#if isOwner && !owner}
            <span class="member-actions">
              <button class="ghost icon" aria-label="Transmettre la propriété à {member.displayName}" title="Transmettre la propriété" onclick={() => askHandOver(member)}>
                <Crown size={18} />
              </button>
              <button class="ghost icon danger-text" aria-label="Retirer {member.displayName} du foyer" title="Retirer du foyer" onclick={() => askRemove(member)}>
                <UserMinus size={18} />
              </button>
            </span>
          {/if}
        </li>
      {/each}
    </ul>
  </section>

  <IncomeTrackingCard enabled={household.incomeTracking} {isOwner} ontoggle={toggleIncomeTracking} />

  {#if isOwner}
    <InvitationCard invitation={household.activeInvitation} onrenew={newCode} oncancel={cancelCode} />
  {:else}
    <p class="muted hint">Seul le propriétaire du foyer peut inviter de nouvelles personnes.</p>
  {/if}

  <button class="ghost" onclick={onaddhousehold}><Plus size={18} /> Créer ou rejoindre un autre foyer</button>

  <section class="card stack danger-zone">
    <h2>Zone sensible</h2>
    {#if isOwner}
      <p class="muted">
        Pour quitter ce foyer, transmettez d'abord la propriété à un autre membre. Vous pouvez aussi le supprimer
        définitivement, avec toutes ses données.
      </p>
      <button class="danger" onclick={() => (deleting = true)}><Trash2 size={18} /> Supprimer le foyer</button>
    {:else}
      <p class="muted">Vous n'aurez plus accès à ce foyer ni à ses données. Vos dépenses passées y restent.</p>
      <button class="danger" onclick={() => (confirmLeave = true)}><LogOut size={18} /> Quitter le foyer</button>
    {/if}
  </section>

  <!-- Mobile : version en bas de l'écran Foyer (sur grand écran, elle est dans le menu latéral). -->
  <div class="mobile-version"><AppVersion /></div>

  <TextDialog bind:open={renaming} title="Renommer le foyer" label="Nom" initial={displayName(household.name)} maxlength={100} onsubmit={rename} />

  <ConfirmDialog
    bind:open={confirmRemove}
    title="Retirer {removing?.displayName ?? ''} ?"
    message="Cette personne perd aussitôt l'accès au foyer. Ses dépenses passées restent ; les charges qu'elle payait passent à vous."
    confirmLabel="Retirer"
    onconfirm={remove}
  />

  <ConfirmDialog
    bind:open={confirmHandOver}
    title="Transmettre la propriété ?"
    message="{handingOver?.displayName ?? ''} deviendra propriétaire : lui seul pourra inviter, retirer un membre ou supprimer le foyer. Vous resterez membre."
    confirmLabel="Transmettre"
    onconfirm={handOver}
  />

  <ConfirmDialog
    bind:open={confirmLeave}
    title="Quitter « {displayName(household.name)} » ?"
    message="Vous n'aurez plus accès à ce foyer. Pour y revenir, il faudra un nouveau code d'invitation."
    confirmLabel="Quitter"
    onconfirm={leave}
  />

  <DeleteHouseholdDialog bind:open={deleting} name={household.name} ondelete={destroy} />
{/if}

<style>
  .title {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .members {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 12px;
  }
  .members li {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .avatar {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .who {
    flex: 1;
    min-width: 0;
    display: grid;
    font-size: 0.9rem;
  }
  .badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    margin-left: 6px;
    padding: 1px 8px;
    border-radius: 999px;
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
    font-size: 0.72rem;
    font-weight: 800;
    vertical-align: middle;
  }
  .member-actions {
    display: flex;
    gap: 2px;
  }
  .danger-text {
    color: var(--color-danger);
  }
  .hint {
    font-size: 0.9rem;
    margin: 0;
  }
  @media (min-width: 900px) {
    .mobile-version {
      display: none;
    }
  }
  .danger-zone {
    border: 1px solid color-mix(in srgb, var(--color-danger) 35%, transparent);
  }
  .danger-zone button {
    justify-self: start;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
</style>
