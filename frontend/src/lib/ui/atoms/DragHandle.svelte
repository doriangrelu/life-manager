<script lang="ts">
  import { GripVertical } from '@lucide/svelte'
  import { dragHandle } from 'svelte-dnd-action'

  // Poignée de glisser-déposer : seule cette zone déclenche le déplacement, le reste de la ligne défile normalement au doigt.
  // Au clavier : Entrée ou Espace pour saisir, flèches pour déplacer, Entrée pour poser.
  let { label }: { label: string } = $props()
</script>

<span class="handle" use:dragHandle aria-label={label}>
  <GripVertical size={18} strokeWidth={2.2} />
</span>

<style>
  .handle {
    display: grid;
    place-items: center;
    flex: none;
    width: 32px;
    min-height: 44px;
    margin-left: -8px;
    color: var(--color-text-muted);
    border-radius: var(--radius-sm, 8px);
    cursor: grab;
    /* Le doigt sur la poignée déplace l'élément au lieu de faire défiler la page. */
    touch-action: none;
    transition: color 0.15s ease, background-color 0.15s ease;
  }
  .handle:active {
    cursor: grabbing;
  }
  @media (hover: hover) {
    .handle:hover {
      color: var(--color-primary-text);
      background: var(--color-surface-muted);
    }
  }
  .handle:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
</style>
