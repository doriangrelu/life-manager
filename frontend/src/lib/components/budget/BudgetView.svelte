<script lang="ts">
  import { onMount } from 'svelte'
  import { deleteExpense, listCategories, monthlyBudget } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import type { Category, Expense, Member, MonthlyBudget, UserProfile } from '../../api/types'
  import { currentMonth, monthLabel, shiftMonth } from '../../month'
  import CategoryManager from './CategoryManager.svelte'
  import ExpenseForm from './ExpenseForm.svelte'
  import MonthSummary from './MonthSummary.svelte'

  // Onglet Budget : la vue du mois, d'où l'on saisit, corrige ou supprime des dépenses.
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  type Screen = { kind: 'month' } | { kind: 'add' } | { kind: 'edit'; expense: Expense } | { kind: 'categories' }

  let month = $state(currentMonth())
  let budget = $state<MonthlyBudget | null>(null)
  let categories = $state<Category[]>([])
  let members = $state<Member[]>([])
  let screen = $state<Screen>({ kind: 'month' })
  let error = $state<string | null>(null)

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

  function remove(expense: Expense) {
    run(async () => {
      await deleteExpense(householdId, expense.id)
      await loadMonth()
    })
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

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if screen.kind === 'add' || screen.kind === 'edit'}
  <ExpenseForm
    {householdId}
    {categories}
    {members}
    currentUserId={profile.userId}
    expense={screen.kind === 'edit' ? screen.expense : null}
    onsaved={() => run(loadMonth)}
    onclose={() => (screen = { kind: 'month' })}
  />
{:else if screen.kind === 'categories'}
  <CategoryManager {householdId} bind:categories onclose={() => (screen = { kind: 'month' })} />
{:else}
  <section class="stack">
    <div class="month-nav">
      <button class="secondary" aria-label="Mois précédent" onclick={() => changeMonth(-1)}>‹</button>
      <h1>{monthLabel(month)}</h1>
      <button class="secondary" aria-label="Mois suivant" onclick={() => changeMonth(1)}>›</button>
    </div>
    <button onclick={() => (screen = { kind: 'add' })}>+ Ajouter une dépense</button>
    {#if budget}
      <MonthSummary
        {budget}
        {categories}
        {memberName}
        onedit={(expense) => (screen = { kind: 'edit', expense })}
        ondelete={remove}
      />
    {/if}
    <button class="link" onclick={() => (screen = { kind: 'categories' })}>Gérer les catégories</button>
  </section>
{/if}

<style>
  .month-nav {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  }
  .month-nav h1 {
    text-transform: capitalize;
    font-size: 1.3rem;
  }
</style>
