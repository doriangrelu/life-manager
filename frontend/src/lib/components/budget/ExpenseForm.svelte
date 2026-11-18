<script lang="ts">
  import { displayName } from '../../text'
  import { Trash2 } from '@lucide/svelte'
  import { untrack } from 'svelte'
  import { correctExpense, recordExpense } from '../../api/budgetApi'
  import type { Category, Expense, ExpenseInput, Member } from '../../api/types'
  import { isOther } from '../../categories'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import AmountInput from '../../ui/AmountInput.svelte'
  import Chip from '../../ui/Chip.svelte'
  import Segmented from '../../ui/Segmented.svelte'

  // Saisie rapide (ou correction) d'une dépense, affichée dans une modale :
  // montant au clavier numérique, catégorie en un geste, le reste pré-rempli.
  let {
    householdId,
    categories,
    members,
    currentUserId,
    expense,
    onsaved,
    onclose,
    ondelete,
  }: {
    householdId: string
    categories: Category[]
    members: Member[]
    currentUserId: string
    expense: Expense | null
    onsaved: () => void
    onclose: () => void
    ondelete: (expense: Expense) => void
  } = $props()

  // Le formulaire part d'une copie des valeurs reçues à l'ouverture, puis vit sa vie.
  const initial = untrack(() => ({ expense, currentUserId }))

  let amount = $state(initial.expense ? centsToInput(initial.expense.amountCents) : '')
  let categoryId = $state(initial.expense?.categoryId ?? '')
  let paidBy = $state(initial.expense?.paidBy ?? initial.currentUserId)
  let beneficiary = $state(initial.expense?.beneficiary ?? '') // '' = commun
  let spentOn = $state(initial.expense?.spentOn ?? today())
  let label = $state(initial.expense?.label ? displayName(initial.expense.label) : '')
  let precision = $state('') // nom tapé sous « Autre » : crée (ou retrouve) la catégorie de ce nom
  let busy = $state(false)
  let error = $state<string | null>(null)
  let confirmation = $state<string | null>(null)
  let amountField: HTMLInputElement | undefined = $state()

  // Catégories proposées : les actives, plus la catégorie actuelle si elle a été archivée depuis.
  const choices = $derived(categories.filter((c) => !c.archived || c.id === initial.expense?.categoryId))
  const otherChosen = $derived(isOther(categories.find((c) => c.id === categoryId)?.name ?? ''))
  const payers = $derived(members.map((m) => ({ value: m.memberId, label: m.displayName })))
  const beneficiaries = $derived([{ value: '', label: 'Commun' }, ...payers])

  // Sous « Autre », une précision remplace la catégorie choisie par une catégorie de ce nom.
  function input(amountCents: number): ExpenseInput {
    const newCategoryName = otherChosen && precision.trim() ? precision.trim() : null
    return {
      amountCents,
      categoryId: newCategoryName ? null : categoryId,
      newCategoryName,
      spentOn,
      paidBy,
      beneficiary: beneficiary || null,
      label: label.trim() || null,
    }
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
    precision = ''
    categoryId = ''
    amountField?.focus()
  }
</script>

<form class="stack" onsubmit={submit}>
  <AmountInput bind:value={amount} bind:field={amountField} />

  <fieldset class="categories">
    <legend>Catégorie</legend>
    <div class="chips">
      {#each choices as category (category.id)}
        <Chip selected={categoryId === category.id} onclick={() => (categoryId = category.id)}>{displayName(category.name)}</Chip>
      {/each}
    </div>
    {#if otherChosen}
      <label class="precision">
        Préciser (facultatif) — crée la catégorie
        <input bind:value={precision} maxlength="40" placeholder="Cadeaux, Animaux…" />
      </label>
    {/if}
  </fieldset>

  <div class="form-row">
    <Segmented label="Payé par" options={payers} bind:value={paidBy} />
    <Segmented label="Pour" options={beneficiaries} bind:value={beneficiary} />
  </div>

  <div class="form-row">
    <label>
      Date
      <input type="date" bind:value={spentOn} max={today()} required />
    </label>
    <label>
      Libellé
      <input bind:value={label} maxlength="140" placeholder="Facultatif" />
    </label>
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if confirmation}
    <p class="confirmation" role="status">{confirmation}</p>
  {/if}

  <button class="large" disabled={busy} aria-busy={busy}>{initial.expense ? 'Enregistrer' : 'Ajouter la dépense'}</button>
  {#if initial.expense}
    <button type="button" class="danger" onclick={() => initial.expense && ondelete(initial.expense)}>
      <Trash2 size={18} /> Supprimer la dépense
    </button>
  {:else if confirmation}
    <button type="button" class="ghost" onclick={onclose}>Terminé</button>
  {/if}
</form>

<style>
  .categories {
    border: none;
    padding: 0;
    margin: 0;
  }
  .categories legend {
    font-size: 0.85rem;
    font-weight: 700;
    color: var(--color-text-muted);
    margin-bottom: 8px;
  }
  .chips {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  .precision {
    margin-top: 12px;
  }
  .precision input {
    border: 2px solid var(--color-primary);
  }
  .confirmation {
    margin: 0;
    font-weight: 700;
    color: var(--color-on-success-soft);
    background: var(--color-success-soft);
    padding: 10px 14px;
    border-radius: var(--radius-md);
  }
</style>
