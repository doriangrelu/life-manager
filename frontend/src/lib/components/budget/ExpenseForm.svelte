<script lang="ts">
  import { untrack } from 'svelte'
  import { correctExpense, recordExpense } from '../../api/budgetApi'
  import type { Category, Expense, ExpenseInput, Member } from '../../api/types'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { today } from '../../month'

  // Saisie rapide (ou correction) d'une dépense, pensée pour le pouce sur mobile :
  // montant au clavier numérique, catégorie en un clic, le reste pré-rempli.
  let {
    householdId,
    categories,
    members,
    currentUserId,
    expense,
    onsaved,
    onclose,
  }: {
    householdId: string
    categories: Category[]
    members: Member[]
    currentUserId: string
    expense: Expense | null
    onsaved: () => void
    onclose: () => void
  } = $props()

  // Le formulaire part d'une copie des valeurs reçues à l'ouverture, puis vit sa vie.
  const initial = untrack(() => ({ expense, currentUserId }))
  const editing = initial.expense !== null

  let amount = $state(initial.expense ? centsToInput(initial.expense.amountCents) : '')
  let categoryId = $state(initial.expense?.categoryId ?? '')
  let paidBy = $state(initial.expense?.paidBy ?? initial.currentUserId)
  let beneficiary = $state(initial.expense?.beneficiary ?? '') // '' = commun
  let spentOn = $state(initial.expense?.spentOn ?? today())
  let label = $state(initial.expense?.label ?? '')
  let busy = $state(false)
  let error = $state<string | null>(null)
  let confirmation = $state<string | null>(null)
  let amountField: HTMLInputElement | undefined = $state()

  // Catégories proposées : les actives, plus la catégorie actuelle si elle a été archivée depuis.
  const choices = $derived(categories.filter((c) => !c.archived || c.id === initial.expense?.categoryId))

  function input(amountCents: number): ExpenseInput {
    return { amountCents, categoryId, spentOn, paidBy, beneficiary: beneficiary || null, label: label.trim() || null }
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    const amountCents = parseAmountToCents(amount)
    if (amountCents === null) {
      error = 'Montant invalide (exemple : 12,50)'
      return
    }
    if (!categoryId) {
      error = 'Choisissez une catégorie'
      return
    }
    await save(amountCents)
  }

  async function save(amountCents: number) {
    busy = true
    error = null
    try {
      if (initial.expense) {
        await correctExpense(householdId, initial.expense.id, input(amountCents))
        onsaved()
        onclose()
      } else {
        await recordExpense(householdId, input(amountCents))
        onsaved()
        readyForNext(amountCents)
      }
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }

  // Après une saisie : confirmation, puis formulaire prêt pour la suivante.
  function readyForNext(amountCents: number) {
    confirmation = `Dépense de ${formatCents(amountCents)} enregistrée`
    amount = ''
    label = ''
    categoryId = ''
    amountField?.focus()
  }
</script>

<form class="stack" onsubmit={submit}>
  <h1>{editing ? 'Modifier la dépense' : 'Nouvelle dépense'}</h1>

  <label>
    Montant (€)
    <input bind:this={amountField} bind:value={amount} inputmode="decimal" placeholder="0,00" autocomplete="off" />
  </label>

  <fieldset class="categories">
    <legend>Catégorie</legend>
    {#each choices as category (category.id)}
      <button
        type="button"
        class:selected={categoryId === category.id}
        aria-pressed={categoryId === category.id}
        onclick={() => (categoryId = category.id)}>{category.name}</button
      >
    {/each}
  </fieldset>

  <div class="row">
    <label>
      Payé par
      <select bind:value={paidBy}>
        {#each members as member (member.memberId)}
          <option value={member.memberId}>{member.displayName}</option>
        {/each}
      </select>
    </label>
    <label>
      Pour
      <select bind:value={beneficiary}>
        <option value="">Commun</option>
        {#each members as member (member.memberId)}
          <option value={member.memberId}>{member.displayName}</option>
        {/each}
      </select>
    </label>
  </div>

  <div class="row">
    <label>
      Date
      <input type="date" bind:value={spentOn} max={today()} required />
    </label>
    <label>
      Libellé (facultatif)
      <input bind:value={label} maxlength="140" />
    </label>
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if confirmation}
    <p class="confirmation" role="status">{confirmation}</p>
  {/if}

  <button disabled={busy}>{editing ? 'Enregistrer' : 'Ajouter'}</button>
  <button type="button" class="link" onclick={onclose}>{editing ? 'Annuler' : 'Terminé'}</button>
</form>

<style>
  .categories {
    border: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .categories legend {
    margin-bottom: 6px;
  }
  .categories button {
    background: var(--surface);
    color: var(--text);
    border: 1px solid var(--border);
    padding: 8px 12px;
  }
  .categories button.selected {
    background: var(--brand);
    color: var(--on-brand);
    border-color: var(--brand);
  }
  .row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  .row label {
    min-width: 0;
  }
  .confirmation {
    color: var(--brand);
    margin: 0;
    font-weight: 600;
  }
</style>
