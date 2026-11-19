<script lang="ts">
  import { displayName } from '../../text'
  import type { MonthlyBudget } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'
  import { planTotals, type CategoryProgress } from '../../plan'
  import ProgressBar from '../../ui/atoms/ProgressBar.svelte'

  // Répartition du mois : par catégorie, puis pour qui.
  // Sans budget prévu, la barre compare la catégorie à la plus grosse du mois. Avec un prévu, elle montre
  // le dépensé sur le prévu (orange à 80 %, rouge au-delà) et, en plus clair, les récurrents encore à venir.
  // Toucher une catégorie ouvre le réglage de son budget prévu.
  let {
    budget,
    progress,
    memberName,
    onmanage,
    onplan,
  }: {
    budget: MonthlyBudget
    progress: CategoryProgress[]
    memberName: (id: string | null) => string
    onmanage: () => void
    onplan: (line: CategoryProgress) => void
  } = $props()

  const largest = $derived(Math.max(1, ...progress.map((line) => line.spentCents)))
  const anyPlan = $derived(progress.some((line) => line.plannedCents !== null))
  // Synthèse : dépensé dans les catégories prévues, face au total prévu (absente sans aucun prévu).
  const totals = $derived(planTotals(progress))

  const percent = (part: number, whole: number) => `${Math.min(100, Math.round((part / whole) * 100))}%`

  function barColor(line: CategoryProgress): string {
    if (line.status === 'over') {
      return 'var(--color-danger)'
    }
    return line.status === 'warning' ? 'var(--color-warning)' : categoryStyle(line.name).bar
  }

  /** Dépassement attendu en fin de mois, une fois les récurrents à venir payés (0 s'il n'y en a pas). */
  function expectedOverrun(line: CategoryProgress): number {
    return line.plannedCents === null ? 0 : Math.max(0, line.spentCents + line.upcomingCents - line.plannedCents)
  }
</script>

<section class="card stack">
  <div class="section-title">
    <h2>Par catégorie</h2>
    <button class="link" onclick={onmanage}>Gérer</button>
  </div>
  {#if totals}
    <div class="total">
      <span class="line">
        <span class="name">
          Budget prévu
          {#if totals.badge}<span class="badge" class:over={totals.status === 'over'}>{totals.badge}</span>{/if}
        </span>
        <span class="amount">
          {formatCents(totals.spentCents)}
          <span class="planned">/ {formatCents(totals.plannedCents)}</span>
        </span>
      </span>
      <span class="track" aria-hidden="true">
        <span
          class="fill"
          style:width={percent(totals.spentCents, totals.plannedCents)}
          style:background={totals.status === 'over' ? 'var(--color-danger)' : totals.status === 'warning' ? 'var(--color-warning)' : 'var(--color-primary)'}
        ></span>
      </span>
      {#if totals.offPlanCents > 0}
        <span class="hint">+ {formatCents(totals.offPlanCents)} hors budget (catégories sans prévu)</span>
      {/if}
    </div>
  {/if}
  <ul class="rows">
    {#each progress as line (line.categoryId)}
      {@const planned = line.plannedCents}
      <li>
        <button
          class="row"
          onclick={() => onplan(line)}
          aria-label="Budget prévu de {displayName(line.name)}{planned === null ? '' : ` : ${formatCents(line.spentCents)} sur ${formatCents(planned)}`}"
        >
          <span class="line">
            <span class="name">
              {displayName(line.name)}
              {#if line.badge}<span class="badge" class:over={line.status === 'over'}>{line.badge}</span>{/if}
            </span>
            <span class="amount">
              {formatCents(line.spentCents)}
              {#if planned !== null}<span class="planned">/ {formatCents(planned)}</span>{/if}
            </span>
          </span>
          {#if planned !== null}
            <span class="track" aria-hidden="true">
              <span class="fill" style:width={percent(line.spentCents, planned)} style:background={barColor(line)}></span>
              {#if line.upcomingCents > 0 && line.spentCents < planned}
                <span
                  class="fill upcoming"
                  style:width={percent(Math.min(line.upcomingCents, planned - line.spentCents), planned)}
                  style:background={barColor(line)}
                ></span>
              {/if}
            </span>
            {#if line.upcomingCents > 0}
              <span class="hint" class:alert={expectedOverrun(line) > 0}>
                {#if expectedOverrun(line) > 0 && line.status !== 'over'}
                  {formatCents(line.upcomingCents)} à venir : dépassement prévu de {formatCents(expectedOverrun(line))}
                {:else}
                  dont {formatCents(line.upcomingCents)} encore à venir ce mois
                {/if}
              </span>
            {/if}
          {:else}
            <ProgressBar ratio={line.spentCents / largest} color={categoryStyle(line.name).bar} />
          {/if}
        </button>
      </li>
    {/each}
  </ul>
  {#if !anyPlan}
    <p class="muted tip">Touchez une catégorie pour lui fixer un budget prévu.</p>
  {/if}
</section>

{#if budget.byBeneficiary.length > 0}
  <section class="card stack">
    <h2>Pour qui</h2>
    <ul class="rows">
      {#each budget.byBeneficiary as beneficiary (beneficiary.memberId ?? 'commun')}
        <li>
          <div class="line">
            <span>{memberName(beneficiary.memberId)}</span>
            <span class="amount">{formatCents(beneficiary.totalCents)}</span>
          </div>
        </li>
      {/each}
    </ul>
  </section>
{/if}

<style>
  .rows {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 12px;
  }
  /* Ligne entière cliquable, sans l'apparence d'un bouton. */
  .row {
    display: grid;
    /* Les boutons centrent leur contenu : ici la ligne occupe toute la largeur. */
    grid-template-columns: minmax(0, 1fr);
    justify-content: stretch;
    align-items: start;
    min-height: 0;
    gap: 6px;
    margin: -6px -8px;
    padding: 6px 8px;
    width: calc(100% + 16px);
    border-radius: var(--radius-md);
    background: transparent;
    color: inherit;
    box-shadow: none;
    text-align: left;
    font: inherit;
    transition: background-color 0.15s ease;
  }
  @media (hover: hover) {
    .row:hover {
      filter: none;
      background: var(--color-surface-muted);
    }
  }
  /* Synthèse du mois, séparée des catégories par un filet. */
  .total {
    display: grid;
    gap: 6px;
    padding-bottom: 14px;
    border-bottom: 1px solid var(--color-border);
  }
  .total .line {
    font-weight: 800;
  }
  .line {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    gap: 12px;
    font-size: 0.95rem;
    font-weight: 600;
  }
  .name {
    display: inline-flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 4px 8px;
    min-width: 0;
  }
  .amount {
    white-space: nowrap;
  }
  .planned {
    color: var(--color-text-muted);
    font-weight: 500;
  }
  .badge {
    padding: 1px 8px;
    border-radius: 999px;
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
    font-size: 0.75rem;
    font-weight: 800;
    white-space: nowrap;
  }
  .badge.over {
    background: var(--color-danger-soft);
    color: var(--color-danger);
  }
  .track {
    display: flex;
    height: 8px;
    border-radius: 4px;
    background: var(--color-surface-muted);
    overflow: hidden;
  }
  .fill {
    height: 100%;
    transition: width 0.3s ease;
  }
  .fill.upcoming {
    opacity: 0.35;
  }
  .hint {
    font-size: 0.78rem;
    font-weight: 500;
    color: var(--color-text-muted);
  }
  .hint.alert {
    color: var(--color-on-warning-soft);
    font-weight: 700;
  }
  .tip {
    margin: 0;
    font-size: 0.82rem;
  }
</style>
