<script lang="ts">
  import { spaceOf } from '../../navigation'
  import type { Section } from '../../route'

  // Sous-onglets de l'espace ouvert (ex. Argent : Budget · Charges), en pastilles sous l'en-tête.
  // Mobile seulement : sur grand écran, le menu latéral montre déjà tous les écrans.
  let { active, onselect }: { active: Section; onselect: (section: Section) => void } = $props()

  const space = $derived(spaceOf(active))
</script>

{#if space.entries.length > 1}
  <nav class="space-tabs" aria-label="Écrans de l'espace {space.label}">
    {#each space.entries as entry (entry.section)}
      {@const Icon = entry.icon}
      {@const current = entry.section === active}
      <button class:active={current} aria-current={current ? 'page' : undefined} onclick={() => onselect(entry.section)}>
        <Icon size={16} strokeWidth={current ? 2.6 : 2.2} />
        {entry.label}
      </button>
    {/each}
  </nav>
{/if}

<style>
  .space-tabs {
    display: flex;
    gap: 6px;
    padding: 4px;
    overflow-x: auto;
    scrollbar-width: none;
    background: var(--color-surface-muted);
    border-radius: 999px;
    align-self: flex-start;
    max-width: 100%;
  }
  button {
    flex: none;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 40px;
    padding: 0 16px;
    border-radius: 999px;
    background: transparent;
    color: var(--color-text-muted);
    font-size: 0.9rem;
    font-weight: 700;
    box-shadow: none;
    transition:
      background-color 0.2s ease,
      color 0.2s ease,
      box-shadow 0.2s ease;
  }
  button.active {
    background: var(--color-surface);
    color: var(--color-primary-text);
    font-weight: 800;
    box-shadow: var(--shadow-sm);
  }
  @media (hover: hover) {
    button:not(.active):hover {
      filter: none;
      color: var(--color-text);
    }
  }
  @media (min-width: 900px) {
    .space-tabs {
      display: none;
    }
  }
</style>
