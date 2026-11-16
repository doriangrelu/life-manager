<script lang="ts">
  import { onMount } from 'svelte'
  import { getHousehold, issueInvitation } from '../api/householdApi'
  import type { HouseholdDetails } from '../api/types'
  import InvitationCard from './InvitationCard.svelte'

  // Écran d'un foyer : ses membres et le code pour inviter quelqu'un.
  let { householdId }: { householdId: string } = $props()

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

  const joinedOn = (date: string) => new Date(date).toLocaleDateString('fr-FR')
</script>

{#if error}
  <p class="error">{error}</p>
{:else if !household}
  <p class="muted">Chargement…</p>
{:else}
  <section class="stack">
    <h1>{household.name}</h1>

    <div class="card stack">
      <h2>Membres</h2>
      <ul class="members">
        {#each household.members as member (member.memberId)}
          <li>
            <span>{member.displayName}</span>
            <span class="muted">depuis le {joinedOn(member.joinedAt)}</span>
          </li>
        {/each}
      </ul>
    </div>

    <InvitationCard invitation={household.activeInvitation} onrenew={newCode} />
  </section>
{/if}

<style>
  .members {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .members li {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }
</style>
