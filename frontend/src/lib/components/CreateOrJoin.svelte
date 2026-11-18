<script lang="ts">
  import { House, Ticket } from '@lucide/svelte'
  import { createHousehold, joinHousehold } from '../api/householdApi'
  import type { HouseholdSummary } from '../api/types'

  // Créer un foyer ou en rejoindre un avec un code : page d'accueil, ou contenu d'une modale.
  let { onadded }: { onadded: (household: HouseholdSummary) => void } = $props()

  let name = $state('')
  let code = $state('')
  let busy = $state(false)
  let error = $state<string | null>(null)

  async function submit(event: SubmitEvent, action: () => Promise<HouseholdSummary>) {
    event.preventDefault()
    busy = true
    error = null
    try {
      onadded(await action())
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="card stack" onsubmit={(e) => submit(e, () => createHousehold(name))}>
  <h2 class="title"><House size={20} /> Créer un foyer</h2>
  <label>
    Nom du foyer
    <input bind:value={name} maxlength="100" required placeholder="Maison" />
  </label>
  <button disabled={busy || !name.trim()} aria-busy={busy}>Créer</button>
</form>

<form class="card stack" onsubmit={(e) => submit(e, () => joinHousehold(code))}>
  <h2 class="title"><Ticket size={20} /> Rejoindre un foyer</h2>
  <label>
    Code d'invitation
    <input bind:value={code} required placeholder="ABCD2345" autocapitalize="characters" autocomplete="off" />
  </label>
  <button class="soft" disabled={busy || !code.trim()} aria-busy={busy}>Rejoindre</button>
</form>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

<style>
  .title {
    display: flex;
    align-items: center;
    gap: 8px;
  }
</style>
