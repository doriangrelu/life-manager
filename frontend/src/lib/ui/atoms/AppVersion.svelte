<script lang="ts">
  import { onMount } from 'svelte'
  import { loadApiVersion, PWA_VERSION, versionText } from '../../version'

  // Pied de page discret : version déployée de l'application.
  let api = $state<string | null>(null)

  onMount(async () => {
    api = await loadApiVersion()
  })
</script>

<p class="app-version">Life Manager {versionText(PWA_VERSION, api)}</p>

<style>
  .app-version {
    margin: 0;
    padding: 8px 12px;
    font-size: 0.75rem;
    color: var(--color-text-muted);
    text-align: center;
  }
</style>
