<script lang="ts">
  import { incomeTracking } from '../../householdSettings'
  import { SPACES, sectionToOpen, spaceOf, visibleEntries, type Space } from '../../navigation'
  import { rememberedSpaceSection } from '../../preferences'
  import type { Section } from '../../route'
  import AppVersion from '../atoms/AppVersion.svelte'

  // Navigation principale par espaces (voir lib/navigation.ts).
  // Mobile : barre fixée en bas, une entrée par espace. À partir de 900 px : menu latéral, espaces et tous leurs écrans.
  let { active, onselect }: { active: Section; onselect: (section: Section) => void } = $props()

  const activeSpace = $derived(spaceOf(active).id)

  function openSpace(space: Space) {
    onselect(sectionToOpen(space, active, rememberedSpaceSection(space.id), $incomeTracking === true))
  }
</script>

<nav class="main-nav" aria-label="Navigation principale">
  <span class="brand">
    <img src="/logo.svg" alt="" width="32" height="32" />
    Life Manager
  </span>

  <!-- Mobile : un bouton par espace -->
  <div class="bar">
    {#each SPACES as space (space.id)}
      {@const Icon = space.icon}
      {@const current = activeSpace === space.id}
      <button class:active={current} aria-current={current ? 'page' : undefined} onclick={() => openSpace(space)}>
        <span class="icon"><Icon size={22} strokeWidth={current ? 2.4 : 2} /></span>
        <span class="label">{space.label}</span>
      </button>
    {/each}
  </div>

  <!-- Grand écran : chaque espace avec ses écrans -->
  <div class="side">
    {#each SPACES as space (space.id)}
      {@const entries = visibleEntries(space, $incomeTracking === true)}
      <div class="group">
        <!-- Un espace d'un seul écran du même nom n'a pas besoin de titre. -->
        {#if entries.length > 1 || entries[0].label !== space.label}
          <span class="group-title">{space.label}</span>
        {/if}
        {#each entries as entry (entry.section)}
          {@const Icon = entry.icon}
          {@const current = active === entry.section}
          <button class:active={current} aria-current={current ? 'page' : undefined} onclick={() => onselect(entry.section)}>
            <span class="icon"><Icon size={20} strokeWidth={current ? 2.4 : 2} /></span>
            <span class="label">{entry.label}</span>
          </button>
        {/each}
      </div>
    {/each}
  </div>

  <!-- Grand écran : version en bas du menu (sur mobile, elle est en bas de l'écran Foyer). -->
  <div class="version"><AppVersion /></div>
</nav>

<style>
  /* Mobile : barre fixée en bas. */
  .main-nav {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 10;
    height: calc(var(--nav-height) + env(safe-area-inset-bottom));
    padding: 6px 8px env(safe-area-inset-bottom);
    background: var(--color-surface);
    border-top: 1px solid var(--color-border);
  }
  .brand,
  .side,
  .version {
    display: none;
  }
  .bar {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    height: 100%;
  }
  button {
    flex-direction: column;
    gap: 4px;
    min-height: 60px;
    padding: 0;
    background: transparent;
    color: var(--color-text-muted);
    font-size: 0.75rem;
    font-weight: 600;
    transition: color 0.15s ease;
  }
  /* Pastille derrière l'icône : colorée pour l'espace ouvert, teintée au survol. */
  .icon {
    display: grid;
    place-items: center;
    width: 56px;
    height: 30px;
    border-radius: 15px;
    transition:
      background-color 0.2s ease,
      transform 0.2s ease;
  }
  button.active {
    color: var(--color-primary-text);
    font-weight: 800;
  }
  .bar button.active .icon {
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
  }
  @media (hover: hover) {
    .bar button:not(.active):hover {
      filter: none;
      color: var(--color-primary-text);
    }
    .bar button:not(.active):hover .icon {
      background: color-mix(in srgb, var(--color-primary-soft) 60%, transparent);
      transform: translateY(-1px);
    }
  }

  /* Grand écran : menu latéral groupé. */
  @media (min-width: 900px) {
    .main-nav {
      top: 0;
      right: auto;
      width: 240px;
      height: auto;
      display: flex;
      flex-direction: column;
      gap: 4px;
      padding: 24px 16px;
      border-top: none;
      border-right: 1px solid var(--color-border);
    }
    .bar {
      display: none;
    }
    .brand {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 1.15rem;
      font-weight: 800;
      padding: 0 12px 24px;
    }
    .brand img {
      border-radius: 9px;
    }
    .side {
      display: flex;
      flex-direction: column;
      gap: 18px;
    }
    .version {
      display: block;
      margin-top: auto;
    }
    .version :global(.app-version) {
      text-align: left;
    }
    .group {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .group-title {
      padding: 0 12px 6px;
      font-size: 0.72rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--color-text-muted);
    }
    .side button {
      position: relative;
      flex-direction: row;
      justify-content: flex-start;
      gap: 12px;
      min-height: 44px;
      padding: 0 12px;
      font-size: 0.95rem;
      border-radius: var(--radius-md);
      transition:
        background-color 0.15s ease,
        color 0.15s ease;
    }
    .side .icon {
      width: auto;
      height: auto;
    }
    /* Trait de couleur à gauche de l'écran ouvert. */
    .side button::before {
      content: '';
      position: absolute;
      left: 0;
      top: 11px;
      bottom: 11px;
      width: 3px;
      border-radius: 2px;
      background: var(--color-primary);
      transform: scaleY(0);
      transition: transform 0.2s ease;
    }
    .side button.active {
      background: var(--color-primary-soft);
      color: var(--color-on-primary-soft);
    }
    .side button.active::before {
      transform: scaleY(1);
    }
    @media (hover: hover) {
      .side button:not(.active):hover {
        filter: none;
        background: color-mix(in srgb, var(--color-primary-soft) 55%, transparent);
        color: var(--color-on-primary-soft);
      }
      .side button:not(.active):hover .icon {
        transform: translateX(2px);
      }
    }
  }
</style>
