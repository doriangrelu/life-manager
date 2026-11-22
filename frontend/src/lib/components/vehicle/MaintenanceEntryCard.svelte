<script lang="ts">
  import {
    Check,
    ClipboardCheck,
    CircleDot,
    Droplet,
    Hammer,
    Paperclip,
    Settings,
    Trash2,
    Wallet,
    Wrench,
  } from '@lucide/svelte'
  import type { MaintenanceEntry, MaintenanceKind } from '../../api/types'
  import { attachmentCountLabel } from '../../attachments'
  import { formatCents } from '../../money'
  import { entryLabel, formatDay } from '../../vehicles'

  // Une opération du carnet : type (avec son icône), date, kilométrage, coût, note et pièces jointes.
  // Avec un coût : mention « Dans le budget », ou bouton pour l'y ajouter après coup.
  let {
    entry,
    ondelete,
    onattachments,
    onbudget,
  }: { entry: MaintenanceEntry; ondelete: () => void; onattachments: () => void; onbudget?: () => void } = $props()
  const hasCost = $derived((entry.costCents ?? 0) > 0)

  const ICONS: Record<MaintenanceKind, typeof Wrench> = {
    SERVICE: Settings,
    OIL_CHANGE: Droplet,
    TECHNICAL_INSPECTION: ClipboardCheck,
    TIRES: CircleDot,
    REPAIR: Hammer,
    OTHER: Wrench,
  }
  const Icon = $derived(ICONS[entry.kind])
  const label = $derived(entryLabel(entry))
  const details = $derived(
    [formatDay(entry.performedOn), entry.mileageKm !== null ? `${entry.mileageKm.toLocaleString('fr-FR')} km` : null]
      .filter(Boolean)
      .join(' · '),
  )
</script>

<article class="entry">
  <span class="bubble" aria-hidden="true"><Icon size={20} /></span>
  <div class="text">
    <span class="head">
      <strong>{label}</strong>
      {#if entry.costCents !== null}<span class="amount">{formatCents(entry.costCents)}</span>{/if}
    </span>
    <span class="muted">{details}</span>
    {#if entry.note}<span class="note">{entry.note}</span>{/if}
    <div class="actions">
      <button class="files" onclick={onattachments}>
        <Paperclip size={15} />
        {entry.attachmentCount > 0 ? attachmentCountLabel(entry.attachmentCount) : 'Joindre un fichier'}
      </button>
      {#if hasCost && entry.inBudget}
        <span class="in-budget"><Check size={15} strokeWidth={2.6} /> Dans le budget</span>
      {:else if hasCost && onbudget}
        <button class="files" onclick={onbudget}><Wallet size={15} /> Ajouter au budget</button>
      {/if}
    </div>
  </div>
  <button class="link icon" aria-label="Supprimer l'opération {label}" onclick={ondelete}>
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
  .head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 8px;
  }
  .text strong {
    font-size: 1rem;
  }
  .note {
    overflow-wrap: anywhere;
  }
  .actions {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 4px 12px;
    margin-top: 4px;
  }
  .in-budget {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 0.8rem;
    font-weight: 700;
    color: var(--color-success);
    white-space: nowrap;
  }
  .files {
    justify-self: start;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 36px;
    padding: 0 12px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    box-shadow: none;
    font-size: 0.85rem;
    font-weight: 700;
    white-space: nowrap;
  }
  .link {
    color: var(--color-text-muted);
    margin-top: -8px;
  }
</style>
