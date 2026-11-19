<script lang="ts">
  import type { ForecastItem } from '../../api/types'
  import { formatDay } from '../../dates'
  import { formatCents } from '../../money'
  import { displayName } from '../../text'

  // Ce qui est encore attendu d'ici la fin de la période : rentrées (+) et charges (−) récurrentes.
  let { items }: { items: ForecastItem[] } = $props()

  const statusLabel = (item: ForecastItem) => (item.status === 'TO_CONFIRM' ? ' · à confirmer' : '')
</script>

<section class="card stack">
  <h2>Encore attendu</h2>
  {#if items.length === 0}
    <p class="muted">Aucune rentrée ni charge récurrente n'est encore attendue sur cette période.</p>
  {:else}
    <ul class="rows">
      {#each items as item (item.chargeId + item.dueOn)}
        {@const income = item.direction === 'INCOME'}
        <li>
          <span class="text">
            <strong>{displayName(item.name)}</strong>
            <span class="muted">{formatDay(item.dueOn)}{statusLabel(item)}</span>
          </span>
          <span class="amount" class:positive={income}>{income ? '+' : '−'}{formatCents(item.expectedCents)}</span>
        </li>
      {/each}
    </ul>
  {/if}
</section>

<style>
  .rows {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .rows li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }
  .text {
    display: grid;
    min-width: 0;
  }
  .text strong {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .text .muted {
    font-size: 0.82rem;
  }
  .positive {
    color: var(--color-on-success-soft);
  }
  p {
    margin: 0;
  }
</style>
