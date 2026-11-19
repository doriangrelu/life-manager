<script lang="ts">
  import { tick, untrack } from 'svelte'
  import type { AmountKind, Category, Charge, ChargeInput, FlowDirection, Frequency, Member } from '../../api/types'
  import { sortCategories } from '../../categories'
  import { FREQUENCIES } from '../../charges'
  import { beneficiaryOptions, payerOptions } from '../../members'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { displayName } from '../../text'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Field from '../../ui/molecules/Field.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import {
    allValid,
    amount as amountRule,
    check,
    focusFirstInvalid,
    notBefore,
    optionalAmount,
    required,
  } from '../../validation'

  // Saisie ou modification d'une charge récurrente (dans une modale) : dépense, crédit, ou rentrée (salaire…).
  // Pour une rentrée, « payé par » devient « reçu par » et il n'y a pas de bénéficiaire.
  // Un crédit est mensuel et fixe : on saisit la mensualité du prêt, celle de l'assurance, et sa fin si on la connaît.
  let {
    charge,
    categories,
    members,
    currentUserId,
    direction = 'EXPENSE',
    loan = false,
    onsave,
  }: {
    charge: Charge | null
    /** Catégories du sens de la charge (dépenses ou rentrées). */
    categories: Category[]
    members: Member[]
    currentUserId: string
    /** Sens d'une nouvelle charge ; une charge existante garde le sien. */
    direction?: FlowDirection
    /** Nouvelle charge : crédit ou dépense ordinaire ; une charge existante garde sa nature. */
    loan?: boolean
    onsave: (input: ChargeInput) => Promise<void>
  } = $props()

  // Le formulaire part d'une copie des valeurs reçues à l'ouverture.
  const initial = untrack(() => ({
    charge,
    currentUserId,
    direction: charge?.direction ?? direction,
    loan: charge ? Boolean(charge.loan) : loan,
  }))
  const income = initial.direction === 'INCOME'
  const isLoan = initial.loan && !income
  const insuranceAtStart = initial.charge?.loan?.insuranceCents ?? 0

  let name = $state(initial.charge ? displayName(initial.charge.name) : '')
  // Pour un crédit, ce champ est la mensualité du prêt seule (le total moins l'assurance).
  let amount = $state(initial.charge ? centsToInput(initial.charge.amountCents - (isLoan ? insuranceAtStart : 0)) : '')
  let insurance = $state(insuranceAtStart > 0 ? centsToInput(insuranceAtStart) : '')
  let kind = $state<AmountKind>(initial.charge?.amountKind ?? 'FIXED')
  let frequency = $state<Frequency>(initial.charge?.frequency ?? 'MONTHLY')
  let firstDueOn = $state(initial.charge?.firstDueOn ?? today())
  let lastDueOn = $state(initial.charge?.lastDueOn ?? '')
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
  // Crédit : total payé chaque mois, affiché dès que les deux montants sont lisibles.
  const insuranceCents = $derived(insurance.trim() ? parseAmountToCents(insurance) : 0)
  const loanTotalCents = $derived.by(() => {
    const loanCents = parseAmountToCents(amount)
    return loanCents === null || insuranceCents === null ? null : loanCents + insuranceCents
  })

  // Contrôles en direct, champ par champ ; après un envoi refusé, toutes les erreurs se montrent.
  let revealed = $state(false)
  const errors = $derived({
    name: check(name, required('Donnez un nom')),
    amount: check(amount, amountRule(isLoan ? '588,40' : '39,99')),
    insurance: isLoan ? check(insurance, optionalAmount('24')) : null,
    firstDueOn: check(
      firstDueOn,
      required('Choisissez une date'),
      notBefore(minFirstDueOn ?? '', 'La date ne peut pas être passée'),
    ),
    lastDueOn: isLoan ? check(lastDueOn, notBefore(firstDueOn, 'La dernière mensualité ne peut pas précéder la prochaine')) : null,
    category: categoryId ? null : 'Choisissez une catégorie du budget',
  })

  /** Montant de la charge et, pour un crédit, son assurance (la saisie a déjà été contrôlée). */
  function amounts(): { amountCents: number; insuranceCents: number } {
    const amountCents = parseAmountToCents(amount) ?? 0
    const insured = isLoan ? (insuranceCents ?? 0) : 0
    return { amountCents: amountCents + insured, insuranceCents: insured }
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    error = null
    if (!allValid(errors)) {
      const form = event.currentTarget as HTMLFormElement
      revealed = true
      await tick()
      focusFirstInvalid(form)
      return
    }
    const entered = amounts()
    busy = true
    try {
      await onsave({
        name: name.trim(),
        amountCents: entered.amountCents,
        amountKind: isLoan ? 'FIXED' : kind,
        frequency: isLoan ? 'MONTHLY' : frequency,
        firstDueOn,
        budgetCategoryId: categoryId,
        payerId,
        beneficiaryId: income ? null : beneficiaryId || null,
        direction: initial.direction,
        lastDueOn: isLoan ? lastDueOn || null : (initial.charge?.lastDueOn ?? null),
        loan: isLoan ? { insuranceCents: entered.insuranceCents } : null,
      })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <Field label="Nom" error={errors.name} reveal={revealed}>
    {#snippet children(id)}
    <input
      {id}
      bind:value={name}
      maxlength="60"
      placeholder={income ? 'Ex. : Salaire, Allocations…' : isLoan ? 'Ex. : Prêt immobilier, Crédit auto…' : 'Ex. : Internet, Électricité…'}
      data-autofocus
    />
    {/snippet}
  </Field>

  {#if isLoan}
    <Field error={errors.amount} reveal={revealed}>
      {#snippet children()}
        <AmountInput bind:value={amount} label="Mensualité du prêt" />
      {/snippet}
    </Field>
    <Field
      label="Assurance par mois, en € (facultative)"
      error={errors.insurance}
      reveal={revealed}
      info="L'assurance emprunteur prélevée chaque mois. Elle s'ajoute à la mensualité du prêt : une seule dépense, du total, arrive dans le budget."
    >
      {#snippet children(id)}
        <input {id} bind:value={insurance} inputmode="decimal" placeholder="Ex. : 24" autocomplete="off" />
      {/snippet}
    </Field>
    <p class="muted hint" aria-live="polite">
      {#if loanTotalCents !== null}
        Chaque mois, une dépense de {formatCents(loanTotalCents)} est ajoutée au budget toute seule.
      {:else}
        Chaque mois, une seule dépense (prêt + assurance) est ajoutée au budget toute seule.
      {/if}
    </p>
    <div class="form-row">
      <Field label={initial.charge ? 'Première mensualité' : 'Prochaine mensualité'} error={errors.firstDueOn} reveal={revealed}>
        {#snippet children(id)}
          <input {id} type="date" bind:value={firstDueOn} min={minFirstDueOn} />
        {/snippet}
      </Field>
      <Field
        label="Dernière mensualité (facultative)"
        error={errors.lastDueOn}
        reveal={revealed}
        info="La date de fin du crédit. Si vous la donnez, le crédit s'arrête tout seul après cette mensualité et l'écran affiche combien il en reste."
      >
        {#snippet children(id)}
          <input {id} type="date" bind:value={lastDueOn} min={firstDueOn} />
        {/snippet}
      </Field>
    </div>
    <p class="muted hint">Avec une dernière mensualité, le crédit s'arrête tout seul à cette date.</p>
  {:else}
    <Field error={errors.amount} reveal={revealed}>
      {#snippet children()}
        <AmountInput bind:value={amount} label={kind === 'FIXED' ? 'Montant' : 'Montant prévu'} />
      {/snippet}
    </Field>

    <Segmented
      label="Montant"
      info="Fixe : le même montant à chaque échéance, ajouté au budget tout seul. Variable : le montant change (électricité, eau…), vous le confirmez à chaque facture."
      options={[
        { value: 'FIXED', label: 'Fixe' },
        { value: 'VARIABLE', label: 'Variable' },
      ]}
      bind:value={kind}
    />
    <p class="muted hint">
      {#if income}
        {kind === 'FIXED'
          ? 'La rentrée est ajoutée au budget toute seule, le jour prévu.'
          : 'À chaque échéance, vous confirmez le montant reçu : la rentrée est alors ajoutée au budget.'}
      {:else}
        {kind === 'FIXED'
          ? 'La dépense est ajoutée au budget toute seule, le jour de chaque échéance.'
          : 'À chaque échéance, vous confirmez le montant de la facture : la dépense est alors ajoutée au budget.'}
      {/if}
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
      <Field label={initial.charge ? 'Première échéance' : 'Prochaine échéance'} error={errors.firstDueOn} reveal={revealed}>
        {#snippet children(id)}
          <input {id} type="date" bind:value={firstDueOn} min={minFirstDueOn} />
        {/snippet}
      </Field>
    </div>
  {/if}

  <fieldset class="categories">
    <legend>Catégorie du budget</legend>
    <div class="chips">
      {#each choices as category (category.id)}
        <Chip selected={categoryId === category.id} onclick={() => (categoryId = category.id)}>
          {displayName(category.name)}
        </Chip>
      {/each}
    </div>
    {#if revealed && errors.category}
      <span class="field-error" role="alert">{errors.category}</span>
    {/if}
  </fieldset>

  {#if income}
    <Segmented label="Reçu par" options={payers} bind:value={payerId} />
  {:else}
    <div class="form-row">
      <Segmented label="Payé par" options={payers} bind:value={payerId} />
      <Segmented label="Pour" options={beneficiaries} bind:value={beneficiaryId} />
    </div>
  {/if}

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <button class="large" disabled={busy} aria-busy={busy}>
    {initial.charge ? 'Enregistrer' : income ? 'Ajouter la rentrée' : isLoan ? 'Ajouter le crédit' : 'Ajouter la charge'}
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
