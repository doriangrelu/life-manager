<script lang="ts">
  import { formatCents } from '../../money'
  import type { RestToLive } from '../../plan'

  // Reste à vivre, posé sur une carte colorée (bandeau du mois, bilan) : ce qu'il en reste,
  // sur ce qu'il y avait une fois les dépenses récurrentes retirées des rentrées prévues.
  let { rest }: { rest: RestToLive } = $props()

  const exceeded = $derived(rest.leftCents < 0)
  // Part déjà consommée par les dépenses courantes (pleine si le reste à vivre est nul ou dépassé).
  const used = $derived(rest.totalCents > 0 ? Math.min(1, Math.max(0, rest.everydayCents / rest.totalCents)) : 1)
</script>

<div class="rest">
  <div class="head">
    <span class="caption">Reste à vivre</span>
    <span class="amount value">
      {#if exceeded}
        <span class="pill">Dépassé de {formatCents(-rest.leftCents)}</span>
      {:else}
        {formatCents(rest.leftCents)}
      {/if}
      <span class="of">sur {formatCents(rest.totalCents)}</span>
    </span>
  </div>
  <div class="track" aria-hidden="true">
    <div class="fill" class:exceeded style:width="{Math.round(used * 100)}%"></div>
  </div>
  <span class="detail">
    Rentrées prévues {formatCents(rest.expectedIncomeCents)} − dépenses récurrentes {formatCents(rest.recurringCents)}
  </span>
</div>

<style>
  .rest {
    display: grid;
    gap: 6px;
    padding: 10px 12px;
    border-radius: var(--radius-md);
    background: rgb(255 255 255 / 0.16);
  }
  .head {
    display: flex;
    flex-wrap: wrap;
    align-items: baseline;
    justify-content: space-between;
    gap: 2px 12px;
  }
  .caption {
    font-size: 0.82rem;
    font-weight: 600;
  }
  .value {
    font-size: 1.15rem;
    font-weight: 800;
  }
  .of {
    font-size: 0.82rem;
    font-weight: 600;
    opacity: 0.9;
  }
  .pill {
    padding: 2px 10px;
    border-radius: 999px;
    background: var(--color-danger-soft);
    color: var(--color-danger);
    font-size: 0.82rem;
  }
  .track {
    height: 6px;
    border-radius: 3px;
    background: rgb(255 255 255 / 0.25);
    overflow: hidden;
  }
  .fill {
    height: 100%;
    background: #ffffff;
    transition: width 0.3s ease;
  }
  .fill.exceeded {
    background: var(--color-danger-soft);
  }
  .detail {
    font-size: 0.75rem;
    font-weight: 500;
    opacity: 0.9;
  }
</style>
