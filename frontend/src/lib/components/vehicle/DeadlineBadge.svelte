<script lang="ts">
  import { today } from '../../month'
  import { deadlineStatus, formatDay } from '../../vehicles'

  // Une échéance (CT, révision) colorée selon qu'elle est passée, proche (30 jours) ou lointaine.
  let { label, due }: { label: string; due: string | null } = $props()

  const status = $derived(deadlineStatus(due, today()))
  const suffix = { none: '', overdue: ' — en retard', soon: ' — bientôt', later: '' }
</script>

<span class="badge {status}">
  {label} : {due ? formatDay(due) : 'non renseigné'}{suffix[status]}
</span>

<style>
  .badge {
    display: inline-block;
    font-size: 0.9rem;
    padding: 2px 8px;
    border-radius: 999px;
    border: 1px solid var(--border);
  }
  .none {
    color: var(--muted);
  }
  .overdue {
    border-color: var(--error);
    color: var(--error);
    font-weight: 600;
  }
  .soon {
    border-color: var(--warning);
    color: var(--warning);
    font-weight: 600;
  }
</style>
