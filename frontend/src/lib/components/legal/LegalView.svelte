<script lang="ts">
  import { ArrowLeft } from '@lucide/svelte'
  import type { LegalPage } from '../../legal'
  import PrivacyText from './PrivacyText.svelte'
  import TermsText from './TermsText.svelte'

  // Page légale lisible sans être connecté (#/cgu, #/confidentialite).
  let { page }: { page: LegalPage } = $props()

  // Retour à la page précédente, ou à l'application si la page a été ouverte directement.
  function back() {
    if (history.length > 1) {
      history.back()
    } else {
      location.hash = '#/'
    }
  }
</script>

<main class="legal stack">
  <button class="ghost back" onclick={back}><ArrowLeft size={18} /> Retour</button>
  <div class="brand">
    <img src="/logo.svg" alt="" width="36" height="36" />
    <span>Life Manager</span>
  </div>
  <h1>{page === 'cgu' ? "Conditions d'utilisation" : 'Politique de confidentialité'}</h1>
  <article class="card">
    {#if page === 'cgu'}
      <TermsText />
    {:else}
      <PrivacyText />
    {/if}
  </article>
</main>

<style>
  .legal {
    max-width: 760px;
    margin: 0 auto;
    padding: 16px 20px 48px;
  }
  .back {
    justify-self: start;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    box-shadow: none;
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
  h1 {
    margin: 0;
  }
</style>
