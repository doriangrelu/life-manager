<script lang="ts">
  import { TriangleAlert } from '@lucide/svelte'
  import { sameHouseholdName } from '../householdAccess'
  import { displayName } from '../text'
  import Field from '../ui/molecules/Field.svelte'
  import Modal from '../ui/molecules/Modal.svelte'

  // Suppression définitive d'un foyer : le bouton ne s'active qu'une fois le nom du foyer retapé.
  let {
    open = $bindable(false),
    name,
    ondelete,
  }: { open: boolean; name: string; ondelete: (confirmName: string) => Promise<void> } = $props()

  let typed = $state('')
  let busy = $state(false)
  let error = $state<string | null>(null)
  const confirmed = $derived(sameHouseholdName(typed, name))
  // Dès qu'un nom est tapé, on dit s'il ne correspond pas.
  const typedError = $derived(typed.trim() && !confirmed ? 'Ce nom ne correspond pas à celui du foyer' : null)

  // À chaque ouverture, la saisie repart de zéro.
  $effect(() => {
    if (open) {
      typed = ''
      error = null
    }
  })

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    if (!confirmed) {
      return
    }
    busy = true
    error = null
    try {
      await ondelete(typed)
      open = false
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<Modal bind:open title="Supprimer le foyer ?">
  <form class="stack" onsubmit={submit}>
    <div class="warning">
      <TriangleAlert size={22} />
      <p>
        <strong>Suppression définitive, sans retour possible.</strong>
        Toutes les données de « {displayName(name)} » disparaissent pour tous ses membres : dépenses, catégories,
        charges et factures, véhicules et pièces jointes, courses, rappels.
      </p>
    </div>
    <p class="ask">Pour confirmer, tapez le nom du foyer : <strong>{displayName(name)}</strong></p>
    <Field label="Nom du foyer" error={typedError}>
      {#snippet children(id)}
        <input {id} bind:value={typed} maxlength="100" autocomplete="off" data-autofocus />
      {/snippet}
    </Field>
    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}
    <button class="danger large" disabled={!confirmed || busy} aria-busy={busy}>Supprimer définitivement</button>
  </form>
</Modal>

<style>
  .ask {
    margin: 0;
  }
  .warning {
    display: flex;
    gap: 12px;
    padding: 14px;
    border-radius: var(--radius-md);
    background: var(--color-danger-soft);
    color: var(--color-on-danger-soft, var(--color-danger));
  }
  .warning p {
    margin: 0;
    font-size: 0.9rem;
  }
  .warning :global(svg) {
    flex: none;
  }
</style>
