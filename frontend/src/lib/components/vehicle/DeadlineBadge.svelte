<script lang="ts">
  import { today } from '../../month'
  import { deadlineStatus, deadlineText, formatDay } from '../../vehicles'

  // Une échéance (CT, révision) colorée selon l'urgence : en pastille dans les listes, en carte sur la fiche.
  let { label, due, large = false }: { label: string; due: string | null; large?: boolean } = $props()

  const status = $derived(deadlineStatus(due, today()))
  const text = $derived(deadlineText(due, today()))
</script>

{#if large}
  <div class="card-deadline {status}">
    <span class="label">{label}</span>
    <strong>{due ? formatDay(due) : '—'}</strong>
    <span class="pill">{text}</span>
  </div>
{:else}
  <span class="pill {status}">{label} · {text}</span>
{/if}

<style>
  .pill {
    align-self: flex-start;
    display: inline-block;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 0.8rem;
    font-weight: 800;
  }
  .card-deadline {
    display: grid;
    gap: 6px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
  }
  .card-deadline .label {
    font-size: 0.8rem;
    font-weight: 700;
  }
  .card-deadline strong {
    font-size: 1.1rem;
  }
  .card-deadline .pill {
    justify-self: start;
  }
  /* Couleurs selon l'urgence : la pastille seule, ou la carte entière. */
  .pill.none,
  .none .pill {
    background: var(--color-surface-muted);
    color: var(--color-text-muted);
  }
  .pill.later,
  .later .pill {
    background: var(--color-success-soft);
    color: var(--color-on-success-soft);
  }
  .pill.soon,
  .soon .pill,
  .card-deadline.soon {
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
  }
  .pill.overdue,
  .overdue .pill,
  .card-deadline.overdue {
    background: var(--color-danger-soft);
    color: var(--color-danger);
  }
</style>
