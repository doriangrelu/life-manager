<script lang="ts">
  import { Ban, Copy, RefreshCw } from '@lucide/svelte'
  import type { Invitation } from '../api/types'
  import { formatDay } from '../dates'

  // Code d'invitation actif (ou bouton pour en générer un), copie et annulation. Réservé au propriétaire.
  let {
    invitation,
    onrenew,
    oncancel,
  }: { invitation: Invitation | null; onrenew: () => Promise<void>; oncancel: () => Promise<void> } = $props()

  async function cancel() {
    busy = true
    error = null
    try {
      await oncancel()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }

  let busy = $state(false)
  let copied = $state(false)
  let error = $state<string | null>(null)

  async function renew() {
    busy = true
    error = null
    copied = false
    try {
      await onrenew()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }

  async function copy(code: string) {
    try {
      await navigator.clipboard.writeText(code)
      copied = true
    } catch {
      error = 'Copie impossible : recopiez le code à la main'
    }
  }

</script>

<section class="card stack">
  <h2>Inviter quelqu'un</h2>
  {#if invitation}
    <p class="code">{invitation.code}</p>
    <p class="muted">Valable jusqu'au {formatDay(invitation.expiresAt)}, une seule fois.</p>
    <div class="actions">
      <button onclick={() => copy(invitation.code)}><Copy size={18} /> {copied ? 'Copié !' : 'Copier le code'}</button>
      <button class="soft" disabled={busy} aria-busy={busy} onclick={renew}><RefreshCw size={18} /> Nouveau code</button>
    </div>
    <button class="link cancel" disabled={busy} onclick={cancel}><Ban size={16} /> Annuler ce code</button>
  {:else}
    <p class="muted">Générez un code et transmettez-le à la personne à inviter.</p>
    <button disabled={busy} aria-busy={busy} onclick={renew}>Générer un code</button>
  {/if}
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
</section>

<style>
  .code {
    margin: 0;
    padding: 14px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    font-family: ui-monospace, 'SF Mono', Menlo, monospace;
    font-size: 1.8rem;
    font-weight: 800;
    letter-spacing: 0.2em;
    text-align: center;
  }
  .actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  /* Texte d'un bouton jamais coupé : s'ils ne tiennent pas côte à côte, ils passent l'un sous l'autre. */
  .actions button {
    flex: 1 1 auto;
    white-space: nowrap;
  }
  .cancel {
    justify-self: start;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    color: var(--color-danger);
  }
</style>
