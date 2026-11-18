<script lang="ts">
  import { Plus } from '@lucide/svelte'

  // Bouton d'ajout flottant, au-dessus de la barre d'onglets sur mobile.
  let { label, onclick }: { label: string; onclick: () => void } = $props()
</script>

<button class="fab" aria-label={label} {onclick}>
  <Plus size={26} strokeWidth={2.6} />
</button>

<style>
  .fab {
    position: fixed;
    right: 20px;
    bottom: calc(var(--nav-height) + 20px + env(safe-area-inset-bottom));
    width: 60px;
    height: 60px;
    padding: 0;
    border-radius: 20px;
    box-shadow: var(--shadow-lg);
    z-index: 5;
  }
  @media (min-width: 900px) {
    .fab {
      right: 32px;
      bottom: 32px;
    }
  }
</style>
