<script lang="ts">
  import { displayName } from '../../text'
  import type { MonthlyBudget } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { formatCents } from '../../money'
  import ProgressBar from '../../ui/ProgressBar.svelte'

  // Répartition du mois : par catégorie (barres proportionnelles à la plus grosse), puis pour qui.
  let {
    budget,
    memberName,
    onmanage,
  }: { budget: MonthlyBudget; memberName: (id: string | null) => string; onmanage: () => void } = $props()

  const largest = $derived(Math.max(1, ...budget.byCategory.map((c) => c.totalCents)))
</script>

<section class="card stack">
  <div class="section-title">
    <h2>Par catégorie</h2>
    <button class="link" onclick={onmanage}>Gérer</button>
  </div>
  <ul class="rows">
    {#each budget.byCategory as category (category.categoryId)}
      <li>
        <div class="line">
          <span>{displayName(category.name)}</span>
          <span class="amount">{formatCents(category.totalCents)}</span>
        </div>
        <ProgressBar ratio={category.totalCents / largest} color={categoryStyle(category.name).bar} />
      </li>
    {/each}
  </ul>
</section>

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

<style>
  .rows {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 12px;
  }
  .rows li {
    display: grid;
    gap: 6px;
  }
  .line {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    font-size: 0.95rem;
    font-weight: 600;
  }
</style>
