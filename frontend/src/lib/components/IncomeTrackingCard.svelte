<script lang="ts">
  import { ChartColumnBig } from '@lucide/svelte'

  // Réglage du foyer « Suivre les rentrées » : le propriétaire l'active, les autres membres le voient.
  let {
    enabled,
    isOwner,
    ontoggle,
  }: { enabled: boolean; isOwner: boolean; ontoggle: (enabled: boolean) => Promise<void> } = $props()

  let busy = $state(false)

  async function toggle() {
    busy = true
    try {
      await ontoggle(!enabled)
    } finally {
      busy = false
    }
  }
</script>

<section class="card setting">
  <span class="bubble" aria-hidden="true"><ChartColumnBig size={20} /></span>
  <div class="text">
    <strong id="income-tracking-title">Suivre les rentrées</strong>
    <span class="muted">
      Salaires, primes, aides… saisis comme les dépenses, avec un écran Bilan : ce qui est rentré, dépensé et mis de
      côté sur le mois, le trimestre ou l'année.
    </span>
    {#if !isOwner}
      <span class="muted owner-only">Seul le propriétaire du foyer peut changer ce réglage.</span>
    {/if}
  </div>
  <button
    class="switch"
    role="switch"
    aria-checked={enabled}
    aria-labelledby="income-tracking-title"
    disabled={!isOwner || busy}
    aria-busy={busy}
    onclick={toggle}
  >
    <span class="knob"></span>
  </button>
</section>

<style>
  .setting {
    display: flex;
    align-items: flex-start;
    gap: 12px;
  }
  .bubble {
    width: 42px;
    height: 42px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .text {
    flex: 1;
    display: grid;
    gap: 4px;
    min-width: 0;
  }
  .text .muted {
    font-size: 0.85rem;
  }
  .owner-only {
    font-style: italic;
  }
  /* Interrupteur : pastille qui glisse, couleur principale une fois activé. */
  .switch {
    flex: none;
    position: relative;
    width: 52px;
    min-height: 32px;
    height: 32px;
    padding: 0;
    border-radius: 999px;
    background: var(--color-surface-muted);
    border: 2px solid var(--color-border);
    box-shadow: none;
    transition:
      background-color 0.2s ease,
      border-color 0.2s ease;
  }
  .switch[aria-checked='true'] {
    background: var(--color-primary);
    border-color: var(--color-primary);
  }
  .knob {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: var(--color-surface);
    box-shadow: var(--shadow-sm);
    transition: transform 0.2s ease;
  }
  .switch[aria-checked='true'] .knob {
    transform: translateX(20px);
  }
  .switch:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  @media (hover: hover) {
    .switch:not(:disabled):hover {
      filter: none;
      border-color: var(--color-primary);
    }
  }
</style>
