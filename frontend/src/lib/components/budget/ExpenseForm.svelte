<script lang="ts">
  import { displayName } from '../../text'
  import { Trash2 } from '@lucide/svelte'
  import { tick, untrack } from 'svelte'
  import { correctExpense, recordExpense } from '../../api/budgetApi'
  import type { Category, Expense, ExpenseInput, Member, Project } from '../../api/types'
  import { isOther } from '../../categories'
  import { beneficiaryOptions, payerOptions } from '../../members'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { scrollToTop } from '../../scroll'
  import { today } from '../../month'
  import { rememberedKeepAdding, rememberKeepAdding } from '../../preferences'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Field from '../../ui/molecules/Field.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import { allValid, amount as amountRule, check, focusFirstInvalid, notAfter, required } from '../../validation'

  // Saisie rapide (ou correction) d'une dépense, affichée dans une modale :
  // montant au clavier numérique, catégorie en un geste, le reste pré-rempli.
  let {
    householdId,
    categories,
    members,
    projects = [],
    currentUserId,
    expense,
    onsaved,
    onclose,
    ondelete,
  }: {
    householdId: string
    categories: Category[]
    members: Member[]
    /** Projets du foyer : on rattache une dépense à un projet en cours. */
    projects?: Project[]
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
  let projectId = $state(initial.expense?.projectId ?? '') // '' = aucun projet
  let busy = $state(false)
  let error = $state<string | null>(null)
  let confirmation = $state<string | null>(null)
  let amountField: HTMLInputElement | undefined = $state()
  // Contrôles en direct : chaque champ dit ce qui ne va pas ; après un envoi refusé, tout se montre.
  let revealed = $state(false)
  // Change à chaque nouvelle saisie enchaînée : les champs repartent sans erreur affichée.
  let round = $state(0)
  const errors = $derived({
    amount: check(amount, amountRule('12,50')),
    category: categoryId ? null : 'Choisissez une catégorie',
    spentOn: check(spentOn, required('Choisissez une date'), notAfter(today(), 'La date ne peut pas être dans le futur')),
  })
  // Nouvelle dépense : enchaîner sur une autre (fenêtre gardée) ou refermer après l'enregistrement.
  let keepAdding = $state(rememberedKeepAdding())

  // Catégories proposées : les actives, plus la catégorie actuelle si elle a été archivée depuis.
  const choices = $derived(categories.filter((c) => !c.archived || c.id === initial.expense?.categoryId))
  const otherChosen = $derived(isOther(categories.find((c) => c.id === categoryId)?.name ?? ''))
  // Projets proposés : ceux en cours, plus le projet actuel de la dépense s'il est clos depuis.
  const projectChoices = $derived(
    projects.filter((p) => p.status === 'ACTIVE' || p.id === initial.expense?.projectId),
  )
  const payers = $derived(payerOptions(members))
  const beneficiaries = $derived(beneficiaryOptions(members))

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
      projectId: projectId || null,
    }
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    const form = event.currentTarget as HTMLFormElement
    const amountCents = parseAmountToCents(amount)
    if (!allValid(errors) || amountCents === null) {
      revealed = true
      await tick()
      focusFirstInvalid(form)
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
        if (keepAdding) {
          readyForNext(amountCents)
        } else {
          onclose()
        }
      }
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }

  // Après une saisie : confirmation, puis formulaire prêt pour la suivante.
  async function readyForNext(amountCents: number) {
    confirmation = `Dépense de ${formatCents(amountCents)} enregistrée`
    amount = ''
    label = ''
    precision = ''
    categoryId = ''
    revealed = false
    round += 1
    await tick()
    // Le formulaire repart en haut, montant prêt à la saisie (le clavier ne doit pas faire défiler).
    scrollToTop(amountField)
    amountField?.focus({ preventScroll: true })
  }
</script>

<form class="stack" onsubmit={submit}>
  {#key round}
    <Field error={errors.amount} reveal={revealed}>
      {#snippet children()}
        <AmountInput bind:value={amount} bind:field={amountField} />
      {/snippet}
    </Field>
  {/key}

  <fieldset class="categories">
    <legend>Catégorie</legend>
    <div class="chips">
      {#each choices as category (category.id)}
        <Chip selected={categoryId === category.id} onclick={() => (categoryId = category.id)}>{displayName(category.name)}</Chip>
      {/each}
    </div>
    {#if revealed && errors.category}
      <span class="field-error" role="alert">{errors.category}</span>
    {/if}
    {#if otherChosen}
      <label class="precision">
        Préciser (facultatif) — crée la catégorie
        <input bind:value={precision} maxlength="40" placeholder="Cadeaux, Animaux…" />
      </label>
    {/if}
  </fieldset>

  <div class="form-row">
    <Segmented label="Payé par" options={payers} bind:value={paidBy} />
    <Segmented
      label="Pour"
      options={beneficiaries}
      bind:value={beneficiary}
      info="À qui profite la dépense : à tout le foyer (Commun) ou à une seule personne. Sert à la répartition « Pour qui » du mois."
    />
  </div>

  <div class="form-row">
    <Field label="Date" error={errors.spentOn} reveal={revealed}>
      {#snippet children(id)}
        <input {id} type="date" bind:value={spentOn} max={today()} />
      {/snippet}
    </Field>
    <label>
      Libellé
      <input bind:value={label} maxlength="140" placeholder="Facultatif" />
    </label>
  </div>

  {#if projectChoices.length > 0}
    <Field
      label="Projet (facultatif)"
      info="Rattacher la dépense à un projet l'ajoute au total de sa fiche. Elle compte dans le budget du mois, sauf si le projet a un budget à part."
    >
      {#snippet children(id)}
        <select {id} bind:value={projectId}>
          <option value="">Aucun</option>
          {#each projectChoices as project (project.id)}
            <option value={project.id}>{displayName(project.name)}{project.countsInBudget ? '' : ' (budget à part)'}</option>
          {/each}
        </select>
      {/snippet}
    </Field>
  {/if}

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if confirmation}
    <p class="confirmation" role="status">{confirmation}</p>
  {/if}

  {#if !initial.expense}
    <label class="keep">
      <input type="checkbox" bind:checked={keepAdding} onchange={() => rememberKeepAdding(keepAdding)} />
      Saisir une autre dépense ensuite
    </label>
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
  /* Case « saisir une autre » : toute la ligne est cliquable. */
  .keep {
    display: flex;
    align-items: center;
    gap: 12px;
    min-height: 44px;
    padding: 0 4px;
    font-size: 0.95rem;
    font-weight: 600;
    color: var(--color-text);
    cursor: pointer;
  }
  .keep input {
    accent-color: var(--color-primary);
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
