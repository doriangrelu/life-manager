<script lang="ts">
  import { ClipboardCheck, CircleDot, Droplet, Settings, Trash2, Wrench, Hammer } from '@lucide/svelte'
  import type { MaintenanceEntry, MaintenanceKind } from '../../api/types'
  import { formatCents } from '../../money'
  import { formatDay, kindLabel } from '../../vehicles'

  // Une opération du carnet : type (avec son icône), date, kilométrage, coût et note.
  let { entry, ondelete }: { entry: MaintenanceEntry; ondelete: () => void } = $props()

  const ICONS: Record<MaintenanceKind, typeof Wrench> = {
    SERVICE: Settings,
    OIL_CHANGE: Droplet,
    TECHNICAL_INSPECTION: ClipboardCheck,
    TIRES: CircleDot,
    REPAIR: Hammer,
    OTHER: Wrench,
  }
  const Icon = $derived(ICONS[entry.kind])
  const details = $derived(
    [formatDay(entry.performedOn), entry.mileageKm !== null ? `${entry.mileageKm.toLocaleString('fr-FR')} km` : null]
      .filter(Boolean)
      .join(' · '),
  )
</script>

<article class="entry">
  <span class="bubble" aria-hidden="true"><Icon size={20} /></span>
  <div class="text">
    <strong>{kindLabel(entry.kind)}</strong>
    <span class="muted">{details}</span>
    {#if entry.note}<span class="note">{entry.note}</span>{/if}
  </div>
  {#if entry.costCents !== null}<span class="amount">{formatCents(entry.costCents)}</span>{/if}
  <button class="link icon" aria-label="Supprimer l'opération {kindLabel(entry.kind)}" onclick={ondelete}>
    <Trash2 size={18} />
  </button>
</article>

<style>
  .entry {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 14px 8px 14px 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
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
    gap: 2px;
    min-width: 0;
    font-size: 0.9rem;
  }
  .text strong {
    font-size: 1rem;
  }
  .note {
    overflow-wrap: anywhere;
  }
  .amount {
    padding-top: 2px;
  }
  .link {
    color: var(--color-text-muted);
    margin-top: -8px;
  }
</style>
