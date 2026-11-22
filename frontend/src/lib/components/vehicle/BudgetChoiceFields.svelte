<script lang="ts">
  import type { BudgetContext } from '../../vehicles'
  import { payerOptions } from '../../members'
  import { displayName } from '../../text'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Catégorie de la dépense et qui a payé : utilisé à la saisie d'une opération et pour l'ajout après coup.
  let {
    context,
    categoryId = $bindable(),
    payerId = $bindable(),
  }: { context: BudgetContext; categoryId: string; payerId: string } = $props()

  const categories = $derived(context.categories.filter((c) => !c.archived))
  const payers = $derived(payerOptions(context.members))
</script>

<div class="fields">
  <label>
    Catégorie
    <select bind:value={categoryId}>
      {#each categories as category (category.id)}
        <option value={category.id}>{displayName(category.name)}</option>
      {/each}
    </select>
  </label>
  {#if payers.length > 1}
    <Segmented label="Payé par" options={payers} bind:value={payerId} />
  {/if}
</div>

<style>
  .fields {
    display: grid;
    gap: 12px;
  }
</style>
