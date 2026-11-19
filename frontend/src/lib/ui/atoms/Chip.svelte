<script lang="ts">
  import type { Snippet } from 'svelte'

  // Pastille à choisir (catégorie, sous-liste…) : un vrai bouton, état annoncé par aria-pressed.
  let {
    selected = false,
    onclick,
    children,
  }: { selected?: boolean; onclick: () => void; children: Snippet } = $props()
</script>

<button type="button" class="chip" class:selected aria-pressed={selected} {onclick}>
  {@render children()}
</button>

<style>
  .chip {
    min-height: 44px;
    padding: 0 14px;
    border-radius: var(--radius-md);
    border: 1px solid var(--color-border);
    background: var(--color-surface);
    color: var(--color-text);
    font-weight: 600;
  }
  .chip.selected {
    border-color: var(--color-primary);
    background: var(--color-primary);
    color: var(--color-on-primary);
    font-weight: 800;
  }
</style>
