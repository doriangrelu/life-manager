<script lang="ts">
  import { Download, UserX } from '@lucide/svelte'
  import { exportMyData } from '../../api/identityApi'
  import { today } from '../../month'
  import DeleteAccountDialog from './DeleteAccountDialog.svelte'

  // Section « Mon compte » de l'écran Foyer : télécharger ses données, supprimer son compte.
  let { userId, email }: { userId: string; email: string } = $props()

  let downloading = $state(false)
  let error = $state<string | null>(null)
  let deleting = $state(false)

  // Fichier JSON produit à la demande, enregistré par le navigateur (rien n'est gardé sur le serveur).
  async function download() {
    downloading = true
    error = null
    try {
      const url = URL.createObjectURL(await exportMyData())
      const link = document.createElement('a')
      link.href = url
      link.download = `life-manager-mes-donnees-${today()}.json`
      link.click()
      URL.revokeObjectURL(url)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      downloading = false
    }
  }
</script>

<section class="card stack">
  <h2>Mon compte</h2>
  <p class="muted">
    Téléchargez ce que vous avez saisi, dans un fichier JSON. Les factures et pièces jointes y sont décrites par leur
    nom, sans leur contenu.
  </p>
  <button class="soft" disabled={downloading} aria-busy={downloading} onclick={download}>
    <Download size={18} /> Télécharger mes données
  </button>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <p class="links">
    <a href="#/cgu">Conditions d'utilisation</a> · <a href="#/confidentialite">Confidentialité</a>
  </p>
  <button class="danger" onclick={() => (deleting = true)}><UserX size={18} /> Supprimer mon compte</button>
</section>

<DeleteAccountDialog bind:open={deleting} {userId} {email} />

<style>
  p {
    margin: 0;
  }
  .links {
    font-size: 0.9rem;
  }
  .links a {
    color: var(--color-primary-text);
  }
  button {
    justify-self: start;
  }
</style>
