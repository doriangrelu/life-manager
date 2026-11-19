<script lang="ts">
  import { Plus } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { getHousehold, issueInvitation } from '../api/householdApi'
  import type { HouseholdDetails } from '../api/types'
  import { formatDay } from '../dates'
  import { displayName } from '../text'
  import Skeleton from '../ui/atoms/Skeleton.svelte'
  import InvitationCard from './InvitationCard.svelte'

  // Onglet Foyer : ses membres, le code pour inviter quelqu'un, et l'accès aux autres foyers.
  let { householdId, onaddhousehold }: { householdId: string; onaddhousehold: () => void } = $props()

  let household = $state<HouseholdDetails | null>(null)
  let error = $state<string | null>(null)

  onMount(async () => {
    try {
      household = await getHousehold(householdId)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  })

  async function newCode() {
    if (household) {
      household.activeInvitation = await issueInvitation(household.id)
    }
  }

</script>

{#if error}
  <p class="error" role="alert">{error}</p>
{:else if !household}
  <Skeleton variant="cards" count={2} />
{:else}
  <h1>{displayName(household.name)}</h1>

  <section class="card stack">
    <h2>Membres</h2>
    <ul class="members">
      {#each household.members as member (member.memberId)}
        <li>
          <span class="avatar" aria-hidden="true">{member.displayName.charAt(0).toUpperCase()}</span>
          <span class="who">
            <strong>{member.displayName}</strong>
            <span class="muted">membre depuis le {formatDay(member.joinedAt)}</span>
          </span>
        </li>
      {/each}
    </ul>
  </section>

  <InvitationCard invitation={household.activeInvitation} onrenew={newCode} />

  <button class="ghost" onclick={onaddhousehold}><Plus size={18} /> Créer ou rejoindre un autre foyer</button>
{/if}

<style>
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
    display: grid;
    font-size: 0.9rem;
  }
</style>
