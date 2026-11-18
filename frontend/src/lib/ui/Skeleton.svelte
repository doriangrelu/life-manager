<script lang="ts">
  // Formes grises animées à la place du contenu pendant son chargement : la page ne saute pas à l'arrivée des données.
  // « rows » : lignes d'une liste ; « cards » : cartes empilées ; « hero » : grand bloc de synthèse.
  let { variant = 'rows', count = 4 }: { variant?: 'rows' | 'cards' | 'hero'; count?: number } = $props()
</script>

<div class="skeletons {variant}" aria-busy="true">
  <span class="visually-hidden" role="status">Chargement…</span>
  {#if variant === 'hero'}
    <div class="skeleton hero-block"></div>
  {:else}
    {#each { length: count } as _, index (index)}
      <div class="skeleton item"></div>
    {/each}
  {/if}
</div>

<style>
  .skeletons {
    display: grid;
    gap: 10px;
  }
  .rows {
    gap: 0;
    padding: 4px 0;
    background: var(--color-surface);
    border-radius: var(--radius-lg);
  }
  .rows .item {
    height: 20px;
    margin: 18px 16px;
    border-radius: 8px;
  }
  /* Longueurs variées : on devine du texte. */
  .rows .item:nth-child(3n) {
    width: 55%;
  }
  .rows .item:nth-child(3n + 1) {
    width: 75%;
  }
  .cards .item {
    height: 76px;
    border-radius: var(--radius-lg);
  }
  .hero-block {
    height: 150px;
    border-radius: var(--radius-xl);
  }
</style>
