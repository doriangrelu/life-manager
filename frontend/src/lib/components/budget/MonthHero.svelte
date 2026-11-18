<script lang="ts">
  import type { MonthlyBudget } from '../../api/types'
  import { formatCents } from '../../money'

  // Carte principale du mois : le total dépensé et ce que chacun a payé.
  let { budget, memberName }: { budget: MonthlyBudget; memberName: (id: string | null) => string } = $props()
</script>

<section class="hero" aria-label="Total du mois">
  <span class="caption">Dépensé ce mois-ci</span>
  <span class="total amount">{formatCents(budget.totalCents)}</span>
  {#if budget.byPayer.length > 0}
    <div class="payers">
      {#each budget.byPayer as payer (payer.memberId)}
        <div class="payer">
          <span class="caption">Payé par {memberName(payer.memberId)}</span>
          <span class="amount">{formatCents(payer.totalCents)}</span>
        </div>
      {/each}
    </div>
  {:else}
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
  .payers {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
    gap: 10px;
  }
  .payer {
    display: grid;
    gap: 2px;
    padding: 10px 12px;
    border-radius: var(--radius-md);
    background: rgb(255 255 255 / 0.16);
    min-width: 0;
  }
</style>
