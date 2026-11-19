<script lang="ts">
  import { acceptTerms } from '../../api/identityApi'
  import TermsText from './TermsText.svelte'

  // Avant d'entrer dans l'application : lire et accepter les conditions en vigueur (1re fois, puis à chaque version).
  let { version, onaccepted }: { version: string; onaccepted: () => void } = $props()

  let busy = $state(false)
  let error = $state<string | null>(null)

  async function accept() {
    busy = true
    error = null
    try {
      await acceptTerms(version)
      onaccepted()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<main class="gate stack">
  <div class="brand">
    <img src="/logo.svg" alt="" width="36" height="36" />
    <span>Life Manager</span>
  </div>
  <h1>Conditions d'utilisation</h1>
  <p class="muted">
    Avant de continuer, merci de lire les conditions d'utilisation. Vos données sont traitées comme expliqué dans la
    <a href="#/confidentialite">politique de confidentialité</a>.
  </p>
  <article class="card text">
    <TermsText />
  </article>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy} onclick={accept}>J'accepte les conditions</button>
</main>

<style>
  .gate {
    max-width: 760px;
    margin: 0 auto;
    padding: 32px 20px 48px;
  }
  .brand {
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 800;
  }
  .brand img {
    border-radius: 9px;
  }
  h1,
  p {
    margin: 0;
  }
  p a {
    color: var(--color-primary-text);
  }
  /* Le texte défile dans sa carte : le bouton reste à portée sans tout faire défiler. */
  .text {
    max-height: min(55vh, 520px);
    overflow-y: auto;
  }
</style>
