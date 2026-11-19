<script lang="ts">
  import { TrendingDown, TrendingUp } from '@lucide/svelte'
  import type { MonthlyBudget } from '../../api/types'
  import { formatCents } from '../../money'

  // Carte principale du mois : le total dépensé et ce que chacun a payé.
  // Avec le suivi des rentrées : le résultat du mois (rentré − dépensé) en premier, positif ou négatif.
  let {
    budget,
    memberName,
    incomeTracking = false,
  }: { budget: MonthlyBudget; memberName: (id: string | null) => string; incomeTracking?: boolean } = $props()

  const positive = $derived(budget.resultCents >= 0)
</script>

<section class="hero" aria-label="Total du mois">
  {#if incomeTracking}
    <span class="caption">Résultat du mois</span>
    <div class="result">
      <span class="total amount">{positive ? '+' : '−'}{formatCents(Math.abs(budget.resultCents))}</span>
      <span class="trend" class:negative={!positive}>
        {#if positive}<TrendingUp size={16} strokeWidth={2.6} /> Dans le vert{:else}<TrendingDown size={16} strokeWidth={2.6} /> Dans le rouge{/if}
      </span>
    </div>
    <div class="tiles">
      <div class="tile">
        <span class="caption">Rentré</span>
        <span class="amount">{formatCents(budget.incomeCents)}</span>
      </div>
      <div class="tile">
        <span class="caption">Dépensé</span>
        <span class="amount">{formatCents(budget.totalCents)}</span>
      </div>
    </div>
  {:else}
    <span class="caption">Dépensé ce mois-ci</span>
    <span class="total amount">{formatCents(budget.totalCents)}</span>
  {/if}
  {#if budget.byPayer.length > 0}
    <div class="tiles">
      {#each budget.byPayer as payer (payer.memberId)}
        <div class="tile">
          <span class="caption">Payé par {memberName(payer.memberId)}</span>
          <span class="amount">{formatCents(payer.totalCents)}</span>
        </div>
      {/each}
    </div>
  {:else if !incomeTracking || budget.incomes.length === 0}
    <span class="caption">Aucune dépense pour l'instant : touchez + pour en ajouter une.</span>
  {/if}
</section>

<style>
  .hero {
    display: grid;
    gap: 14px;
    padding: 20px;
    border-radius: var(--radius-xl);
    background: var(--color-primary);
    color: var(--color-on-primary);
  }
  .caption {
    font-size: 0.82rem;
    font-weight: 600;
  }
  .total {
    font-size: 2.4rem;
    letter-spacing: -0.02em;
  }
  .result {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px 12px;
    margin-top: -10px;
  }
  /* Pastille claire et lisible sur le fond coloré, dans les deux thèmes. */
  .trend {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 12px;
    border-radius: 999px;
    background: var(--color-success-soft);
    color: var(--color-on-success-soft);
    font-size: 0.82rem;
    font-weight: 800;
  }
  .trend.negative {
    background: var(--color-danger-soft);
    color: var(--color-danger);
  }
  .tiles {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
    gap: 10px;
  }
  .tile {
    display: grid;
    gap: 2px;
    padding: 10px 12px;
    border-radius: var(--radius-md);
    background: rgb(255 255 255 / 0.16);
    min-width: 0;
  }
</style>
