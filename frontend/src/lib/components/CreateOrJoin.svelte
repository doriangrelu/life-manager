<script lang="ts">
  import { House, Ticket } from '@lucide/svelte'
  import { createHousehold, joinHousehold } from '../api/householdApi'
  import type { HouseholdSummary } from '../api/types'
  import Field from '../ui/molecules/Field.svelte'
  import { check, required } from '../validation'

  // Créer un foyer ou en rejoindre un avec un code : page d'accueil, ou contenu d'une modale.
  let { onadded }: { onadded: (household: HouseholdSummary) => void } = $props()

  let name = $state('')
  let code = $state('')
  // Formulaire en cours d'envoi : seul son bouton affiche l'attente.
  let busy = $state<'create' | 'join' | null>(null)
  let error = $state<string | null>(null)
  // Contrôles en direct ; chaque formulaire montre ses erreurs après sa propre tentative d'envoi.
  let revealed = $state<'create' | 'join' | null>(null)
  const nameError = $derived(check(name, required('Donnez un nom au foyer')))
  const codeError = $derived(
    check(code, required('Saisissez le code reçu'), (value) => (value.trim().length === 8 ? null : 'Le code fait 8 caractères')),
  )

  async function submit(event: SubmitEvent, form: 'create' | 'join', action: () => Promise<HouseholdSummary>) {
    event.preventDefault()
    if (form === 'create' ? nameError : codeError) {
      revealed = form
      return
    }
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
  <Field label="Nom du foyer" error={nameError} reveal={revealed === 'create'}>
    {#snippet children(id)}
      <input {id} bind:value={name} maxlength="100" placeholder="Ex. : Maison" />
    {/snippet}
  </Field>
  <button disabled={busy !== null} aria-busy={busy === 'create'}>Créer</button>
</form>

<form class="card stack" onsubmit={(e) => submit(e, 'join', () => joinHousehold(code.trim().toUpperCase()))}>
  <h2 class="title"><Ticket size={20} /> Rejoindre un foyer</h2>
  <Field
    label="Code d'invitation"
    error={codeError}
    reveal={revealed === 'join'}
    info="Le code de 8 caractères que le propriétaire du foyer génère dans son écran Foyer. Il est valable 7 jours."
  >
    {#snippet children(id)}
      <input {id} class="code" bind:value={code} maxlength="8" placeholder="8 caractères" autocapitalize="characters" autocomplete="off" spellcheck="false" />
    {/snippet}
  </Field>
  <button class="soft" disabled={busy !== null} aria-busy={busy === 'join'}>Rejoindre</button>
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
