<script lang="ts">
  import { House, Ticket } from '@lucide/svelte'
  import { createHousehold, joinHousehold } from '../api/householdApi'
  import type { HouseholdSummary } from '../api/types'

  // Créer un foyer ou en rejoindre un avec un code : page d'accueil, ou contenu d'une modale.
  let { onadded }: { onadded: (household: HouseholdSummary) => void } = $props()

  let name = $state('')
  let code = $state('')
  // Formulaire en cours d'envoi : seul son bouton affiche l'attente.
  let busy = $state<'create' | 'join' | null>(null)
  let error = $state<string | null>(null)

  async function submit(event: SubmitEvent, form: 'create' | 'join', action: () => Promise<HouseholdSummary>) {
    event.preventDefault()
    busy = form
    error = null
    try {
      onadded(await action())
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = null
    }
  }
</script>

<form class="card stack" onsubmit={(e) => submit(e, 'create', () => createHousehold(name))}>
  <h2 class="title"><House size={20} /> Créer un foyer</h2>
  <label>
    Nom du foyer
    <input bind:value={name} maxlength="100" required placeholder="Ex. : Maison" />
  </label>
  <button disabled={busy !== null || !name.trim()} aria-busy={busy === 'create'}>Créer</button>
</form>

<form class="card stack" onsubmit={(e) => submit(e, 'join', () => joinHousehold(code.trim().toUpperCase()))}>
  <h2 class="title"><Ticket size={20} /> Rejoindre un foyer</h2>
  <label>
    Code d'invitation
    <input class="code" bind:value={code} required maxlength="8" placeholder="8 caractères" autocapitalize="characters" autocomplete="off" spellcheck="false" />
  </label>
  <button class="soft" disabled={busy !== null || !code.trim()} aria-busy={busy === 'join'}>Rejoindre</button>
</form>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

<style>
  /* Code saisi comme il est affiché sur la carte d'invitation. */
  .code:not(:placeholder-shown) {
    font-family: ui-monospace, 'SF Mono', Menlo, monospace;
    font-weight: 800;
    letter-spacing: 0.2em;
    text-transform: uppercase;
  }
  .title {
    display: flex;
    align-items: center;
    gap: 8px;
  }
</style>
