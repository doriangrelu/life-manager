<script lang="ts">
  import { Car, ListChecks, Users, Wallet } from '@lucide/svelte'
  import type { Section } from '../route'

  // Navigation principale : barre d'onglets en bas sur mobile, menu latéral à partir de 900 px.
  let { active, onselect }: { active: Section; onselect: (section: Section) => void } = $props()

  const sections = [
    { id: 'budget', label: 'Budget', icon: Wallet },
    { id: 'shopping', label: 'Courses', icon: ListChecks },
    { id: 'vehicles', label: 'Véhicules', icon: Car },
    { id: 'household', label: 'Foyer', icon: Users },
  ] as const
</script>

<nav class="main-nav" aria-label="Sections">
  <span class="brand" aria-hidden="true">Life Manager</span>
  {#each sections as section (section.id)}
    {@const Icon = section.icon}
    <button
      class:active={active === section.id}
      aria-current={active === section.id ? 'page' : undefined}
      onclick={() => onselect(section.id)}
    >
      <Icon size={24} strokeWidth={active === section.id ? 2.4 : 2} />
      <span>{section.label}</span>
    </button>
  {/each}
</nav>

<style>
  /* Mobile : barre fixée en bas. */
  .main-nav {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 10;
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    height: calc(var(--nav-height) + env(safe-area-inset-bottom));
    padding: 6px 8px env(safe-area-inset-bottom);
    background: var(--color-surface);
    border-top: 1px solid var(--color-border);
  }
  .brand {
    display: none;
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
  }
  button.active {
    color: var(--color-primary-text);
    font-weight: 800;
  }

  /* Grand écran : menu latéral. */
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
    .brand {
      display: block;
      font-size: 1.15rem;
      font-weight: 800;
      padding: 0 12px 20px;
    }
    button {
      flex-direction: row;
      justify-content: flex-start;
      gap: 12px;
      min-height: 48px;
      padding: 0 12px;
      font-size: 0.95rem;
      border-radius: var(--radius-md);
    }
    button.active {
      background: var(--color-primary-soft);
      color: var(--color-on-primary-soft);
    }
  }
</style>
