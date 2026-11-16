<script lang="ts">
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

<div class="card stack">
  <h2>Inviter quelqu'un</h2>
  {#if invitation}
    <p class="code">{invitation.code}</p>
    <p class="muted">Valable jusqu'au {expiresOn(invitation.expiresAt)}, une seule fois.</p>
    <div class="actions">
      <button onclick={() => copy(invitation.code)}>{copied ? 'Copié !' : 'Copier le code'}</button>
      <button class="secondary" disabled={busy} onclick={renew}>Nouveau code</button>
    </div>
  {:else}
    <p class="muted">Générez un code et transmettez-le à la personne à inviter.</p>
    <button disabled={busy} onclick={renew}>Générer un code</button>
  {/if}
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
</div>

<style>
  .code {
    font-family: ui-monospace, 'SF Mono', Menlo, monospace;
    font-size: 1.8rem;
    letter-spacing: 0.2em;
    margin: 0;
  }
  .actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
</style>
