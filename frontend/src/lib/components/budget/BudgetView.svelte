<script lang="ts">
  import { ms, rise } from '../../motion'
  import { CalendarCheck, ChevronLeft, ChevronRight } from '@lucide/svelte'
  import { scale } from 'svelte/transition'
  import { onMount } from 'svelte'
  import { deleteExpense, deleteIncome, listCategories, monthlyBudget } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import type { Category, Expense, FlowDirection, Income, Member, MonthlyBudget, UserProfile } from '../../api/types'
  import { incomeTracking } from '../../householdSettings'
  import { memberName as nameOf, receiverName as receiverOf } from '../../members'
  import { formatCents } from '../../money'
  import { currentMonth, monthLabel, shiftMonth } from '../../month'
  import { navigate, route } from '../../route'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Fab from '../../ui/molecules/Fab.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import CategoryBreakdown from './CategoryBreakdown.svelte'
  import CategoryManager from './CategoryManager.svelte'
  import ExpenseForm from './ExpenseForm.svelte'
  import ExpenseList from './ExpenseList.svelte'
  import IncomeForm from './IncomeForm.svelte'
  import OperationList from './OperationList.svelte'
  import IncomeTrackingPromo from '../IncomeTrackingPromo.svelte'
  import MonthHero from './MonthHero.svelte'

  // Onglet Budget : la vue du mois, d'où l'on saisit, corrige ou supprime des dépenses.
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  // Le mois vient de l'adresse ; mois courant par défaut.
  const month = $derived($route.month ?? currentMonth())
  let budget = $state<MonthlyBudget | null>(null)
  let categories = $state<Category[]>([])
  let members = $state<Member[]>([])
  // Propriétaire : lui seul peut activer « Rentrées et bilan » depuis la carte de mise en avant.
  let ownerId = $state<string | null>(null)
  let error = $state<string | null>(null)
  // Mois en cours de chargement : l'ancien reste affiché, estompé.
  let loadingMonth = $state(false)

  let editorOpen = $state(false)
  let editing = $state<Expense | null>(null)
  let managingCategories = $state(false)
  let deleting = $state<Expense | null>(null)
  let confirmingDelete = $state(false)
  // Suivi des rentrées : nouvelle saisie en dépense ou en rentrée, rentrée ouverte, rentrée à supprimer.
  let entryKind = $state<FlowDirection>('EXPENSE')
  let editingIncome = $state<Income | null>(null)
  let incomeCategories = $state<Category[]>([])
  let deletingIncome = $state<Income | null>(null)
  let confirmingIncomeDelete = $state(false)

  const modalTitle = $derived(
    editingIncome ? 'Modifier la rentrée' : editing ? 'Modifier la dépense' : $incomeTracking ? 'Nouvelle saisie' : 'Nouvelle dépense',
  )

  // Catégories de rentrées : chargées seulement si le foyer suit ses rentrées.
  $effect(() => {
    if ($incomeTracking) {
      run(async () => {
        incomeCategories = await listCategories(householdId, 'INCOME')
      })
    }
  })

  onMount(() =>
    run(async () => {
      const [household, loadedCategories] = await Promise.all([getHousehold(householdId), listCategories(householdId)])
      members = household.members
      ownerId = household.ownerId
      categories = loadedCategories
    }),
  )

  // Chaque changement de mois (flèches, Précédent / Suivant) recharge le budget.
  $effect(() => {
    const shown = month
    run(() => loadMonth(shown))
  })

  async function loadMonth(shown: string = month) {
    loadingMonth = true
    try {
      const loaded = await monthlyBudget(householdId, shown)
      // Réponse d'un mois quitté entre-temps : ignorée.
      if (shown === month) {
        budget = loaded
      }
    } finally {
      if (shown === month) {
        loadingMonth = false
      }
    }
  }

  // Après une saisie : le mois, et les catégories (une précision sous « Autre » a pu en créer une).
  async function reloadAfterSave() {
    const [loadedCategories, loadedIncomeCategories] = await Promise.all([
      listCategories(householdId),
      $incomeTracking ? listCategories(householdId, 'INCOME') : Promise.resolve(incomeCategories),
      loadMonth(),
    ])
    categories = loadedCategories
    incomeCategories = loadedIncomeCategories
  }

  function changeMonth(delta: number) {
    const next = shiftMonth(month, delta)
    navigate({ section: 'budget', month: next === currentMonth() ? undefined : next })
  }

  function openEditor(expense: Expense | null) {
    editing = expense
    editingIncome = null
    entryKind = 'EXPENSE'
    editorOpen = true
  }

  function openIncome(income: Income) {
    editing = null
    editingIncome = income
    entryKind = 'INCOME'
    editorOpen = true
  }

  function askDeleteIncome(income: Income) {
    deletingIncome = income
    confirmingIncomeDelete = true
  }

  function removeIncome() {
    const income = deletingIncome
    if (income) {
      editorOpen = false
      run(async () => {
        await deleteIncome(householdId, income.id)
        await loadMonth()
      })
    }
  }

  function askDelete(expense: Expense) {
    deleting = expense
    confirmingDelete = true
  }

  function remove() {
    const expense = deleting
    if (expense) {
      editorOpen = false
      run(async () => {
        await deleteExpense(householdId, expense.id)
        await loadMonth()
      })
    }
  }

  // Exécute une action et affiche son erreur éventuelle.
  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  /** Nom d'un membre ; null désigne le foyer entier (« Commun »). */
  const memberName = (memberId: string | null) => nameOf(members, memberId)
  /** Qui reçoit une rentrée ; null = le foyer entier. */
  const receiverName = (memberId: string | null) => receiverOf(members, memberId)
</script>

<div class="month-nav">
  <button class="ghost icon" aria-label="Mois précédent" onclick={() => changeMonth(-1)}>
    <ChevronLeft size={20} strokeWidth={2.4} />
  </button>
  <div class="month-title">
    <h1>{monthLabel(month)}</h1>
    {#if month !== currentMonth()}
      <button class="today soft" transition:scale={{ duration: ms(150), start: 0.8 }} onclick={() => navigate({ section: 'budget' })}>
        <CalendarCheck size={15} strokeWidth={2.4} /> Aujourd'hui
      </button>
    {/if}
  </div>
  <button class="ghost icon" aria-label="Mois suivant" onclick={() => changeMonth(1)}>
    <ChevronRight size={20} strokeWidth={2.4} />
  </button>
</div>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if budget}
  <div class="stack" class:reloading={loadingMonth} aria-busy={loadingMonth} in:rise>
    <MonthHero {budget} {memberName} incomeTracking={$incomeTracking === true} />
    {#if ownerId}
      <IncomeTrackingPromo {householdId} isOwner={ownerId === profile.userId} />
    {/if}
    {#if budget.expenses.length > 0}
      <CategoryBreakdown {budget} {memberName} onmanage={() => (managingCategories = true)} />
    {/if}
    {#if $incomeTracking}
      <OperationList
        expenses={budget.expenses}
        incomes={budget.incomes}
        {categories}
        {incomeCategories}
        {memberName}
        {receiverName}
        onopenexpense={openEditor}
        onopenincome={openIncome}
      />
    {:else}
      <ExpenseList expenses={budget.expenses} {categories} {memberName} onopen={openEditor} />
    {/if}
    {#if budget.expenses.length === 0}
      <button class="link manage" onclick={() => (managingCategories = true)}>Gérer les catégories</button>
    {/if}
  </div>
{:else if !error}
  <Skeleton variant="hero" />
  <Skeleton variant="rows" count={5} />
{/if}

<Fab label={$incomeTracking ? 'Ajouter une dépense ou une rentrée' : 'Ajouter une dépense'} onclick={() => openEditor(null)} />

<Modal bind:open={editorOpen} title={modalTitle}>
  {#if $incomeTracking && !editing && !editingIncome}
    <div class="entry-kind">
      <Segmented
        label="Type de saisie"
        hideLabel
        options={[
          { value: 'EXPENSE', label: 'Dépense' },
          { value: 'INCOME', label: 'Rentrée' },
        ]}
        bind:value={entryKind}
      />
    </div>
  {/if}
  {#if entryKind === 'INCOME'}
    <IncomeForm
      {householdId}
      categories={incomeCategories}
      {members}
      currentUserId={profile.userId}
      income={editingIncome}
      onsaved={() => run(reloadAfterSave)}
      onclose={() => (editorOpen = false)}
      ondelete={askDeleteIncome}
    />
  {:else}
  <ExpenseForm
    {householdId}
    {categories}
    {members}
    currentUserId={profile.userId}
    expense={editing}
    onsaved={() => run(reloadAfterSave)}
    onclose={() => (editorOpen = false)}
    ondelete={askDelete}
  />
  {/if}
</Modal>

<ConfirmDialog
  bind:open={confirmingIncomeDelete}
  title="Supprimer la rentrée ?"
  message={deletingIncome ? `La rentrée de ${formatCents(deletingIncome.amountCents)} sera définitivement supprimée.` : ''}
  onconfirm={removeIncome}
/>

<Modal bind:open={managingCategories} title="Catégories">
  <CategoryManager {householdId} bind:categories />
</Modal>

<ConfirmDialog
  bind:open={confirmingDelete}
  title="Supprimer la dépense ?"
  message={deleting ? `La dépense de ${formatCents(deleting.amountCents)} sera définitivement supprimée.` : ''}
  onconfirm={remove}
/>

<style>
  .month-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  }
  .month-nav h1 {
    font-size: 1.1rem;
    text-transform: capitalize;
  }
  /* Titre du mois, et dessous le retour au mois en cours quand on consulte un autre mois. */
  .month-title {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
  }
  .today {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 32px;
    padding: 0 12px;
    border-radius: 999px;
    font-size: 0.8rem;
    font-weight: 800;
  }
  .manage {
    justify-self: center;
  }
  .entry-kind {
    margin-bottom: 16px;
  }
</style>
