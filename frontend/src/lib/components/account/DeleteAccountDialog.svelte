<script lang="ts">
  import { Trash2 } from '@lucide/svelte'
  import { getHousehold, listHouseholds, transferOwnership } from '../../api/householdApi'
  import { deleteMyAccount } from '../../api/identityApi'
  import type { HouseholdDetails } from '../../api/types'
  import { logout } from '../../auth/auth'
  import { displayName } from '../../text'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import Field from '../../ui/molecules/Field.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'

  // Suppression du compte : choisir qui reprend chaque foyer partagé dont on est propriétaire (ou laisser
  // au membre arrivé le plus tôt), retaper son e-mail, puis tout est effacé et la session se termine.
  let {
    open = $bindable(false),
    userId,
    email,
  }: { open: boolean; userId: string; email: string } = $props()

  /** Choix vide : le foyer passe au membre arrivé le plus tôt. */
  const OLDEST = ''

  let owned = $state<HouseholdDetails[] | null>(null)
  let soloCount = $state(0)
  let choices = $state<Record<string, string>>({})
  let typed = $state('')
  let busy = $state(false)
  let error = $state<string | null>(null)

  const matches = $derived(typed.trim().toLowerCase() === email.toLowerCase())

  $effect(() => {
    if (open) {
      typed = ''
      error = null
      load()
    }
  })

  async function load() {
    owned = null
    try {
      const details = await Promise.all((await listHouseholds()).map((h) => getHousehold(h.id)))
      soloCount = details.filter((h) => h.members.length === 1).length
      owned = details.filter((h) => h.ownerId === userId && h.members.length > 1)
      choices = Object.fromEntries(owned.map((h) => [h.id, OLDEST]))
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      owned = []
    }
  }

  async function confirm(event: SubmitEvent) {
    event.preventDefault()
    if (!matches) {
      return
    }
    busy = true
    error = null
    try {
      for (const [householdId, memberId] of Object.entries(choices)) {
        if (memberId !== OLDEST) {
          await transferOwnership(householdId, memberId)
        }
      }
      await deleteMyAccount(typed.trim())
      await logout()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      busy = false
    }
  }
</script>

<Modal bind:open title="Supprimer mon compte">
  <form class="stack" onsubmit={confirm}>
    <p>Votre compte et votre profil seront supprimés <strong>définitivement</strong>.</p>
    <ul class="effects">
      {#if soloCount > 0}
        <li>
          {soloCount === 1 ? 'Le foyer dont vous êtes le seul membre est supprimé' : `Les ${soloCount} foyers dont vous êtes le seul membre sont supprimés`},
          avec toutes leurs données.
        </li>
      {/if}
      <li>Vous quittez vos autres foyers. Ce que vous y avez saisi y reste, au nom de « Ancien membre ».</li>
      <li>Pensez à télécharger vos données avant, si vous voulez les garder.</li>
    </ul>

    {#if owned === null}
      <Skeleton variant="rows" count={2} />
    {:else}
      {#each owned as household (household.id)}
        <label class="field">
          <span>Qui reprend « {displayName(household.name)} » ?</span>
          <select bind:value={choices[household.id]}>
            <option value={OLDEST}>Le membre arrivé le plus tôt</option>
            {#each household.members.filter((m) => m.memberId !== userId) as member (member.memberId)}
              <option value={member.memberId}>{member.displayName}</option>
            {/each}
          </select>
        </label>
      {/each}
    {/if}

    <Field label="Retapez votre adresse e-mail pour confirmer">
      {#snippet children(id)}
        <input {id} type="email" autocomplete="off" bind:value={typed} placeholder={email} />
      {/snippet}
    </Field>

    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}
    <button class="danger large" disabled={!matches || busy || owned === null} aria-busy={busy}>
      <Trash2 size={18} /> Supprimer définitivement mon compte
    </button>
  </form>
</Modal>

<style>
  p {
    margin: 0;
  }
  .effects {
    margin: 0;
    padding-left: 20px;
    display: grid;
    gap: 6px;
    color: var(--color-text-muted);
  }
  .field {
    display: grid;
    gap: 6px;
    font-weight: 600;
  }
</style>
