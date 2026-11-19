<script lang="ts">
  import { untrack } from 'svelte'
  import type { AmountKind, Category, Charge, ChargeInput, FlowDirection, Frequency, Member } from '../../api/types'
  import { sortCategories } from '../../categories'
  import { FREQUENCIES } from '../../charges'
  import { beneficiaryOptions, payerOptions } from '../../members'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { displayName } from '../../text'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'

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

  /** Montant de la charge et, pour un crédit, son assurance ; null (avec un message) si la saisie est invalide. */
  function amounts(): { amountCents: number; insuranceCents: number } | null {
    const amountCents = parseAmountToCents(amount)
    if (amountCents === null) {
      error = isLoan ? 'Mensualité du prêt invalide (exemple : 588,40)' : 'Montant invalide (exemple : 39,99)'
      return null
    }
    if (!isLoan) {
      return { amountCents, insuranceCents: 0 }
    }
    if (insuranceCents === null) {
      error = 'Assurance invalide (exemple : 24)'
      return null
    }
    return { amountCents: amountCents + insuranceCents, insuranceCents }
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    error = null
    const entered = amounts()
    if (entered === null) {
      return
    }
    if (!categoryId) {
      error = 'Choisissez une catégorie du budget'
      return
    }
    if (isLoan && lastDueOn && lastDueOn < firstDueOn) {
      error = 'La dernière mensualité ne peut pas précéder la prochaine'
      return
    }
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
  <label>
    Nom
    <input
      bind:value={name}
      maxlength="60"
      placeholder={income ? 'Ex. : Salaire, Allocations…' : isLoan ? 'Ex. : Prêt immobilier, Crédit auto…' : 'Ex. : Internet, Électricité…'}
      required
      data-autofocus
    />
  </label>

  {#if isLoan}
    <AmountInput bind:value={amount} label="Mensualité du prêt" />
    <label>
      Assurance par mois, en € (facultative)
      <input bind:value={insurance} inputmode="decimal" placeholder="Ex. : 24" autocomplete="off" />
    </label>
    <p class="muted hint" aria-live="polite">
      {#if loanTotalCents !== null}
        Chaque mois, une dépense de {formatCents(loanTotalCents)} est ajoutée au budget toute seule.
      {:else}
        Chaque mois, une seule dépense (prêt + assurance) est ajoutée au budget toute seule.
      {/if}
    </p>
    <div class="form-row">
      <label>
        {initial.charge ? 'Première mensualité' : 'Prochaine mensualité'}
        <input type="date" bind:value={firstDueOn} min={minFirstDueOn} required />
      </label>
      <label>
        Dernière mensualité (facultative)
        <input type="date" bind:value={lastDueOn} min={firstDueOn} />
      </label>
    </div>
    <p class="muted hint">Avec une dernière mensualité, le crédit s'arrête tout seul à cette date.</p>
  {:else}
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
      <label>
        {initial.charge ? 'Première échéance' : 'Prochaine échéance'}
        <input type="date" bind:value={firstDueOn} min={minFirstDueOn} required />
      </label>
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

  <button class="large" disabled={busy || !name.trim()} aria-busy={busy}>
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
