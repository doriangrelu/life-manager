<script lang="ts">
  import { Trash2 } from '@lucide/svelte'
  import { untrack } from 'svelte'
  import { correctIncome, recordIncome } from '../../api/budgetApi'
  import type { Category, Income, IncomeInput, Member } from '../../api/types'
  import { isOther, sortCategories } from '../../categories'
  import { receiverOptions } from '../../members'
  import { centsToInput, parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { displayName } from '../../text'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Saisie (ou correction) d'une rentrée d'argent : même geste qu'une dépense, sans « payé par ».
  let {
    householdId,
    categories,
    members,
    currentUserId,
    income,
    onsaved,
    onclose,
    ondelete,
  }: {
    householdId: string
    categories: Category[]
    members: Member[]
    currentUserId: string
    income: Income | null
    onsaved: () => void
    onclose: () => void
    ondelete: (income: Income) => void
  } = $props()

  // Le formulaire part d'une copie des valeurs reçues à l'ouverture.
  const initial = untrack(() => ({ income, currentUserId }))

  let amount = $state(initial.income ? centsToInput(initial.income.amountCents) : '')
  let categoryId = $state(initial.income?.categoryId ?? '')
  // Une nouvelle rentrée est reçue par la personne qui la saisit ; '' = le foyer entier.
  let receivedBy = $state(initial.income ? (initial.income.receivedBy ?? '') : initial.currentUserId)
  let receivedOn = $state(initial.income?.receivedOn ?? today())
  let label = $state(initial.income?.label ? displayName(initial.income.label) : '')
  let precision = $state('')
  let busy = $state(false)
  let error = $state<string | null>(null)

  const choices = $derived(sortCategories(categories.filter((c) => !c.archived || c.id === initial.income?.categoryId)))
  const otherChosen = $derived(isOther(categories.find((c) => c.id === categoryId)?.name ?? ''))
  const receivers = $derived(receiverOptions(members))

  function input(amountCents: number): IncomeInput {
    const newCategoryName = otherChosen && precision.trim() ? precision.trim() : null
    return {
      amountCents,
      categoryId: newCategoryName ? null : categoryId,
      newCategoryName,
      receivedOn,
      receivedBy: receivedBy || null,
      label: label.trim() || null,
    }
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    const amountCents = parseAmountToCents(amount)
    if (amountCents === null) {
      error = 'Montant invalide (exemple : 1850,00)'
      return
    }
    if (!categoryId) {
      error = 'Choisissez une catégorie'
      return
    }
    busy = true
    error = null
    try {
      if (initial.income) {
        await correctIncome(householdId, initial.income.id, input(amountCents))
      } else {
        await recordIncome(householdId, input(amountCents))
      }
      onsaved()
      onclose()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <AmountInput bind:value={amount} />

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
        <input bind:value={precision} maxlength="40" placeholder="Loyer perçu, Vente…" />
      </label>
    {/if}
  </fieldset>

  <Segmented label="Reçu par" options={receivers} bind:value={receivedBy} />

  <div class="form-row">
    <label>
      Date
      <input type="date" bind:value={receivedOn} max={today()} required />
    </label>
    <label>
      Libellé
      <input bind:value={label} maxlength="140" placeholder="Facultatif" />
    </label>
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <button class="large" disabled={busy} aria-busy={busy}>{initial.income ? 'Enregistrer' : 'Ajouter la rentrée'}</button>
  {#if initial.income}
    <button type="button" class="danger" onclick={() => initial.income && ondelete(initial.income)}>
      <Trash2 size={18} /> Supprimer la rentrée
    </button>
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
</style>
