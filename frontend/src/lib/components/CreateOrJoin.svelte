<script lang="ts">
  import { createHousehold, joinHousehold } from '../api/householdApi'
  import type { HouseholdSummary } from '../api/types'

  // Écran « Créer ou rejoindre un foyer ». oncancel est absent quand l'utilisateur n'a encore aucun foyer.
  let {
    onadded,
    oncancel,
  }: {
    onadded: (household: HouseholdSummary) => void
    oncancel?: () => void
  } = $props()

  let name = $state('')
  let code = $state('')
  let busy = $state(false)
  let error = $state<string | null>(null)

  async function submit(action: () => Promise<HouseholdSummary>) {
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

<section class="stack">
  <h1>Créer ou rejoindre un foyer</h1>

  <form class="card stack" onsubmit={(e) => { e.preventDefault(); submit(() => createHousehold(name)) }}>
    <h2>Créer un foyer</h2>
    <label>
      Nom du foyer
      <input bind:value={name} maxlength="100" required placeholder="Maison" />
    </label>
    <button disabled={busy || !name.trim()}>Créer</button>
  </form>

  <form class="card stack" onsubmit={(e) => { e.preventDefault(); submit(() => joinHousehold(code)) }}>
    <h2>Rejoindre un foyer</h2>
    <label>
      Code d'invitation
      <input bind:value={code} required placeholder="ABCD2345" autocapitalize="characters" autocomplete="off" />
    </label>
    <button disabled={busy || !code.trim()}>Rejoindre</button>
  </form>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if oncancel}
    <button class="link" onclick={oncancel}>Annuler</button>
  {/if}
</section>
