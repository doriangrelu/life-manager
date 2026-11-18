<script lang="ts">
  import { Copy, RefreshCw } from '@lucide/svelte'
  import type { Invitation } from '../api/types'

  // Code d'invitation actif (ou bouton pour en générer un) et copie dans le presse-papiers.
  let { invitation, onrenew }: { invitation: Invitation | null; onrenew: () => Promise<void> } = $props()

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

  const expiresOn = (date: string) => new Date(date).toLocaleDateString('fr-FR')
</script>

<section class="card stack">
  <h2>Inviter quelqu'un</h2>
  {#if invitation}
    <p class="code">{invitation.code}</p>
    <p class="muted">Valable jusqu'au {expiresOn(invitation.expiresAt)}, une seule fois.</p>
    <div class="actions">
      <button onclick={() => copy(invitation.code)}><Copy size={18} /> {copied ? 'Copié !' : 'Copier le code'}</button>
      <button class="soft" disabled={busy} onclick={renew}><RefreshCw size={18} /> Nouveau code</button>
    </div>
  {:else}
    <p class="muted">Générez un code et transmettez-le à la personne à inviter.</p>
    <button disabled={busy} onclick={renew}>Générer un code</button>
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
  .actions button {
    flex: 1 1 140px;
  }
</style>
