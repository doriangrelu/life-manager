<script lang="ts">
  import { ChevronLeft, ChevronRight } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { deleteExpense, listCategories, monthlyBudget } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import type { Category, Expense, Member, MonthlyBudget, UserProfile } from '../../api/types'
  import { formatCents } from '../../money'
  import { currentMonth, monthLabel, shiftMonth } from '../../month'
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'
  import Fab from '../../ui/Fab.svelte'
  import Modal from '../../ui/Modal.svelte'
  import CategoryBreakdown from './CategoryBreakdown.svelte'
  import CategoryManager from './CategoryManager.svelte'
  import ExpenseForm from './ExpenseForm.svelte'
  import ExpenseList from './ExpenseList.svelte'
  import MonthHero from './MonthHero.svelte'

  // Onglet Budget : la vue du mois, d'où l'on saisit, corrige ou supprime des dépenses.
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  let month = $state(currentMonth())
  let budget = $state<MonthlyBudget | null>(null)
  let categories = $state<Category[]>([])
  let members = $state<Member[]>([])
  let error = $state<string | null>(null)

  let editorOpen = $state(false)
  let editing = $state<Expense | null>(null)
  let managingCategories = $state(false)
  let deleting = $state<Expense | null>(null)
  let confirmingDelete = $state(false)

  onMount(() =>
    run(async () => {
      const [household, loadedCategories] = await Promise.all([getHousehold(householdId), listCategories(householdId)])
      members = household.members
      categories = loadedCategories
      await loadMonth()
    }),
  )

  async function loadMonth() {
    budget = await monthlyBudget(householdId, month)
  }

  function changeMonth(delta: number) {
    month = shiftMonth(month, delta)
    run(loadMonth)
  }

  function openEditor(expense: Expense | null) {
    editing = expense
    editorOpen = true
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
  function memberName(memberId: string | null): string {
    if (memberId === null) {
      return 'Commun'
    }
    return members.find((m) => m.memberId === memberId)?.displayName ?? 'Membre'
  }
</script>

<div class="month-nav">
  <button class="ghost icon" aria-label="Mois précédent" onclick={() => changeMonth(-1)}>
    <ChevronLeft size={20} strokeWidth={2.4} />
  </button>
  <h1>{monthLabel(month)}</h1>
  <button class="ghost icon" aria-label="Mois suivant" onclick={() => changeMonth(1)}>
    <ChevronRight size={20} strokeWidth={2.4} />
  </button>
</div>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if budget}
  <MonthHero {budget} {memberName} />
  {#if budget.expenses.length > 0}
    <CategoryBreakdown {budget} {memberName} onmanage={() => (managingCategories = true)} />
  {/if}
  <ExpenseList expenses={budget.expenses} {categories} {memberName} onopen={openEditor} />
  {#if budget.expenses.length === 0}
    <button class="link manage" onclick={() => (managingCategories = true)}>Gérer les catégories</button>
  {/if}
{/if}

<Fab label="Ajouter une dépense" onclick={() => openEditor(null)} />

<Modal bind:open={editorOpen} title={editing ? 'Modifier la dépense' : 'Nouvelle dépense'}>
  <ExpenseForm
    {householdId}
    {categories}
    {members}
    currentUserId={profile.userId}
    expense={editing}
    onsaved={() => run(loadMonth)}
    onclose={() => (editorOpen = false)}
    ondelete={askDelete}
  />
</Modal>

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
  .manage {
    justify-self: center;
  }
</style>
