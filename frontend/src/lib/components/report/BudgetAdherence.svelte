<script lang="ts">
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'
  import { adherenceTotals, type Adherence } from '../../plan'
  import { displayName } from '../../text'

  // Rapport « Budgets » du Bilan : pour chaque catégorie qui a un budget prévu sur la période,
  // le dépensé face au prévu (mois écoulés seulement), l'écart, et le nombre de mois où le budget a été tenu.
  let { lines }: { lines: Adherence[] } = $props()

  const totals = $derived(adherenceTotals(lines))
  const percent = (part: number, whole: number) => `${Math.min(100, Math.round((part / whole) * 100))}%`

  function barColor(line: { status: Adherence['status']; name?: string }): string {
    if (line.status === 'over') {
      return 'var(--color-danger)'
    }
    return line.status === 'warning' ? 'var(--color-warning)' : line.name ? categoryStyle(line.name).bar : 'var(--color-primary)'
  }
</script>

<section class="card stack" aria-label="Respect des budgets prévus">
  <div class="summary">
    <div class="line">
      <h2>Budgets prévus</h2>
      <span class="amount">
        {formatCents(totals.spentCents)} <span class="planned">/ {formatCents(totals.plannedCents)}</span>
      </span>
    </div>
    <div class="track" aria-hidden="true">
      <div class="fill" style:width={percent(totals.spentCents, totals.plannedCents)} style:background={barColor(totals)}></div>
    </div>
    <p class="verdict" class:over={totals.status === 'over'}>
      {#if totals.keptCategories === totals.categories}
        Tous les budgets sont tenus.
      {:else}
        {totals.keptCategories} catégorie{totals.keptCategories > 1 ? 's' : ''} sur {totals.categories} dans le budget.
      {/if}
      {totals.gapCents >= 0 ? `Il reste ${formatCents(totals.gapCents)}.` : `Dépassement de ${formatCents(-totals.gapCents)}.`}
    </p>
  </div>

  <ul class="rows">
    {#each lines as line (line.categoryId)}
      <li>
        <div class="line">
          <span class="name">{displayName(line.name)}</span>
          <span class="amount">
            {formatCents(line.spentCents)} <span class="planned">/ {formatCents(line.plannedCents)}</span>
          </span>
        </div>
        <div class="track" aria-hidden="true">
          <div class="fill" style:width={percent(line.spentCents, line.plannedCents)} style:background={barColor(line)}></div>
        </div>
        <div class="line detail">
          <span class:over={line.gapCents < 0}>
            {line.gapCents >= 0 ? `Reste ${formatCents(line.gapCents)}` : `Dépassé de ${formatCents(-line.gapCents)}`}
          </span>
          {#if line.months > 1}
            <span>Tenu {line.keptMonths} mois sur {line.months}</span>
          {/if}
        </div>
      </li>
    {/each}
  </ul>
</section>

<style>
  .summary {
    display: grid;
    gap: 8px;
  }
  .rows {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 14px;
  }
  .rows li {
    display: grid;
    gap: 6px;
  }
  .line {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    gap: 12px;
    font-size: 0.95rem;
    font-weight: 600;
  }
  .amount {
    white-space: nowrap;
  }
  .planned {
    color: var(--color-text-muted);
    font-weight: 500;
  }
  .track {
    height: 8px;
    border-radius: 4px;
    background: var(--color-surface-muted);
    overflow: hidden;
  }
  .fill {
    height: 100%;
    border-radius: 4px;
  }
  .verdict {
    margin: 0;
    font-size: 0.88rem;
    font-weight: 600;
    color: var(--color-text-muted);
  }
  .detail {
    font-size: 0.8rem;
    font-weight: 500;
    color: var(--color-text-muted);
  }
  .over {
    color: var(--color-danger);
    font-weight: 700;
  }
</style>
