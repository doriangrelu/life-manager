<script lang="ts">
  import { FolderKanban } from '@lucide/svelte'

  // Sous une dépense rattachée : le nom de son projet, et « Budget à part » si elle ne compte pas dans le mois
  // (« hors budget » désigne déjà, ailleurs, les catégories sans budget prévu).
  let { name, offBudget }: { name: string | null; offBudget: boolean } = $props()
</script>

{#if name || offBudget}
  <span class="tags">
    {#if name}<span class="project"><FolderKanban size={12} aria-hidden="true" />{name}</span>{/if}
    {#if offBudget}<span class="off" title="Ne compte pas dans les totaux du mois">Budget à part</span>{/if}
  </span>
{/if}

<style>
  .tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 4px;
  }
  .project,
  .off {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    max-width: 100%;
    padding: 1px 8px;
    border-radius: 999px;
    font-size: 0.72rem;
    font-weight: 700;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .project {
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
  }
  .off {
    background: var(--color-warning-soft);
    color: var(--color-text);
  }
</style>
