<script lang="ts">
  // Barre de proportion (part d'une catégorie dans le mois…) ; ratio entre 0 et 1.
  let { ratio, color }: { ratio: number; color: string } = $props()

  const width = $derived(`${Math.round(Math.min(Math.max(ratio, 0), 1) * 100)}%`)
</script>

<div class="track" aria-hidden="true">
  <div class="fill" style:width style:background={color}></div>
</div>

<style>
  .track {
    height: 8px;
    border-radius: 4px;
    background: var(--color-surface-muted);
    overflow: hidden;
  }
  .fill {
    height: 100%;
    border-radius: 4px;
  }
</style>
