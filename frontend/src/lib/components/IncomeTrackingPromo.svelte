<script lang="ts">
  import { ChartColumnBig, X } from '@lucide/svelte'
  import { untrack } from 'svelte'
  import { fade } from 'svelte/transition'
  import { setIncomeTracking } from '../api/householdApi'
  import { INCOME_OPTION_TEXT, INCOME_OPTION_TITLE, incomeTracking } from '../householdSettings'
  import { ms } from '../motion'
  import { dismissIncomePromo, incomePromoDismissed } from '../preferences'

  // Mise en avant discrète de l'option « Rentrées et bilan », tant qu'elle n'est pas active.
  // card : petite carte (Budget) ; line : une seule ligne (Récurrents). Refermée pour de bon sur l'appareil.
  let {
    householdId,
    isOwner,
    variant = 'card',
  }: { householdId: string; isOwner: boolean; variant?: 'card' | 'line' } = $props()

  // Le foyer ne change pas pendant la vie du composant (il est recréé à chaque changement de foyer).
  let dismissed = $state(untrack(() => incomePromoDismissed(householdId)))
  let busy = $state(false)
  let error = $state<string | null>(null)

  function close() {
    dismissIncomePromo(householdId)
    dismissed = true
  }

  async function activate() {
    busy = true
    error = null
    try {
      await setIncomeTracking(householdId, true)
      incomeTracking.set(true)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

{#if $incomeTracking === false && !dismissed}
  <aside class="promo {variant}" out:fade={{ duration: ms(150) }} aria-label={INCOME_OPTION_TITLE}>
    <span class="bubble" aria-hidden="true"><ChartColumnBig size={variant === 'card' ? 20 : 16} /></span>
    <div class="text">
      {#if variant === 'card'}
        <strong>{INCOME_OPTION_TITLE}</strong>
        <span class="muted">{INCOME_OPTION_TEXT}</span>
      {:else}
        <span class="muted">Salaires, aides… ? Activez <strong>{INCOME_OPTION_TITLE}</strong>.</span>
      {/if}
      {#if !isOwner}
        <span class="muted owner">Demandez au propriétaire du foyer de l'activer.</span>
      {/if}
      {#if error}
        <span class="error" role="alert">{error}</span>
      {/if}
      <!-- Carte : le bouton sous le texte, pour lui laisser toute la largeur sur mobile. -->
      {#if isOwner && variant === 'card'}
        <button class="soft activate" disabled={busy} aria-busy={busy} onclick={activate}>Activer</button>
      {/if}
    </div>
    {#if isOwner && variant === 'line'}
      <button class="soft activate" disabled={busy} aria-busy={busy} onclick={activate}>Activer</button>
    {/if}
    <button class="ghost icon close" aria-label="Ne plus proposer" title="Ne plus proposer" onclick={close}>
      <X size={16} />
    </button>
  </aside>
{/if}

<style>
  .promo {
    display: flex;
    align-items: center;
    gap: 12px;
    border-radius: var(--radius-lg);
  }
  .card {
    align-items: flex-start;
    padding: 14px 8px 14px 14px;
    background: var(--color-surface);
    border: 1px dashed color-mix(in srgb, var(--color-primary) 45%, transparent);
  }
  .line {
    padding: 6px 4px 6px 10px;
    background: transparent;
    border: 1px dashed var(--color-border);
  }
  .bubble {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
  }
  .card .bubble {
    width: 40px;
    height: 40px;
  }
  .line .bubble {
    width: 28px;
    height: 28px;
  }
  .text {
    flex: 1;
    display: grid;
    gap: 2px;
    min-width: 0;
    font-size: 0.88rem;
  }
  .line .text {
    font-size: 0.85rem;
  }
  .owner {
    font-style: italic;
  }
  .error {
    margin: 0;
  }
  .card .activate {
    justify-self: start;
    margin-top: 8px;
  }
  .activate {
    flex: none;
    min-height: 36px;
    padding: 0 14px;
    font-size: 0.85rem;
  }
  .close {
    flex: none;
    min-width: 36px;
    min-height: 36px;
    color: var(--color-text-muted);
  }
</style>
