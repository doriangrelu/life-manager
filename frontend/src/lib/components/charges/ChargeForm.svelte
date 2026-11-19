<script lang="ts">
  import { untrack } from 'svelte'
  import type { AmountKind, Category, Charge, ChargeInput, Frequency, Member } from '../../api/types'
  import { sortCategories } from '../../categories'
  import { FREQUENCIES } from '../../charges'
  import { beneficiaryOptions, payerOptions } from '../../members'
  import { centsToInput, parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { displayName } from '../../text'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Saisie ou modification d'une charge récurrente (dans une modale).
  let {
    charge,
    categories,
    members,
    currentUserId,
    onsave,
  }: {
    charge: Charge | null
    categories: Category[]
    members: Member[]
    currentUserId: string
    onsave: (input: ChargeInput) => Promise<void>
  } = $props()

  // Le formulaire part d'une copie des valeurs reçues à l'ouverture.
  const initial = untrack(() => ({ charge, currentUserId }))

  let name = $state(initial.charge ? displayName(initial.charge.name) : '')
  let amount = $state(initial.charge ? centsToInput(initial.charge.amountCents) : '')
  let kind = $state<AmountKind>(initial.charge?.amountKind ?? 'FIXED')
  let frequency = $state<Frequency>(initial.charge?.frequency ?? 'MONTHLY')
  let firstDueOn = $state(initial.charge?.firstDueOn ?? today())
  let categoryId = $state(initial.charge?.budgetCategoryId ?? '')
  let payerId = $state(initial.charge?.payerId ?? initial.currentUserId)
  let beneficiaryId = $state(initial.charge?.beneficiaryId ?? '') // '' = tout le foyer
  let busy = $state(false)
  let error = $state<string | null>(null)

  const choices = $derived(sortCategories(categories.filter((c) => !c.archived || c.id === categoryId)))
  const payers = $derived(payerOptions(members))
  const beneficiaries = $derived(beneficiaryOptions(members))
  // Une charge existante garde sa première échéance, même passée ; une nouvelle commence aujourd'hui ou plus tard.
  const minFirstDueOn = $derived(initial.charge && firstDueOn === initial.charge.firstDueOn ? undefined : today())

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    error = null
    const amountCents = parseAmountToCents(amount)
    if (amountCents === null) {
      error = 'Montant invalide (exemple : 39,99)'
      return
    }
    if (!categoryId) {
      error = 'Choisissez une catégorie du budget'
      return
    }
    busy = true
    try {
      await onsave({
        name: name.trim(),
        amountCents,
        amountKind: kind,
        frequency,
        firstDueOn,
        budgetCategoryId: categoryId,
        payerId,
        beneficiaryId: beneficiaryId || null,
      })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <label>
    Nom
    <input bind:value={name} maxlength="60" placeholder="Ex. : Internet, Électricité…" required data-autofocus />
  </label>

  <AmountInput bind:value={amount} label={kind === 'FIXED' ? 'Montant' : 'Montant prévu'} />

  <Segmented
    label="Montant"
    options={[
      { value: 'FIXED', label: 'Fixe' },
      { value: 'VARIABLE', label: 'Variable' },
    ]}
    bind:value={kind}
  />
  <p class="muted hint">
    {kind === 'FIXED'
      ? 'La dépense est ajoutée au budget toute seule, le jour de chaque échéance.'
      : 'À chaque échéance, vous confirmez le montant de la facture : la dépense est alors ajoutée au budget.'}
  </p>

  <div class="form-row">
    <label>
      Fréquence
      <select bind:value={frequency}>
        {#each FREQUENCIES as option (option.value)}
          <option value={option.value}>{option.label}</option>
        {/each}
      </select>
    </label>
    <label>
      {initial.charge ? 'Première échéance' : 'Prochaine échéance'}
      <input type="date" bind:value={firstDueOn} min={minFirstDueOn} required />
    </label>
  </div>

  <fieldset class="categories">
    <legend>Catégorie du budget</legend>
    <div class="chips">
      {#each choices as category (category.id)}
        <Chip selected={categoryId === category.id} onclick={() => (categoryId = category.id)}>
          {displayName(category.name)}
        </Chip>
      {/each}
    </div>
  </fieldset>

  <div class="form-row">
    <Segmented label="Payé par" options={payers} bind:value={payerId} />
    <Segmented label="Pour" options={beneficiaries} bind:value={beneficiaryId} />
  </div>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <button class="large" disabled={busy || !name.trim()} aria-busy={busy}>
    {initial.charge ? 'Enregistrer' : 'Ajouter la charge'}
  </button>
</form>

<style>
  .hint {
    margin-top: -8px;
    font-size: 0.85rem;
  }
  select {
    appearance: none;
    padding-right: 40px;
    background: var(--color-surface)
      url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%235b5f78' stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m6 9 6 6 6-6'/%3E%3C/svg%3E")
      no-repeat right 14px center / 18px;
    cursor: pointer;
  }
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
</style>
