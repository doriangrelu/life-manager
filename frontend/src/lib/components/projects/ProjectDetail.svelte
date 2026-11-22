<script lang="ts">
  import { ChevronLeft, Pencil, Trash2 } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { flip } from 'svelte/animate'
  import { dragHandleZone, type DndEvent } from 'svelte-dnd-action'
  import { listCategories } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import {
    addTask,
    deleteProject,
    deleteTask,
    getProject,
    markTask,
    projectExpenses,
    reorderTasks,
    updateProject,
    updateTask,
  } from '../../api/projectApi'
  import type { Category, Member, Project, ProjectExpenses, ProjectInput, ProjectTask, TaskInput } from '../../api/types'
  import { formatDay } from '../../dates'
  import { memberName } from '../../members'
  import { formatCents } from '../../money'
  import { ms } from '../../motion'
  import { planStatus } from '../../plan'
  import { periodText, PROJECT_COLORS, statusLabel } from '../../projects'
  import { remindersChanged } from '../../reminders'
  import { dndItems, dndOptions, idsOf, isAfterDrop, isShadow } from '../../reorder'
  import { displayName } from '../../text'
  import DragHandle from '../../ui/atoms/DragHandle.svelte'
  import ProgressBar from '../../ui/atoms/ProgressBar.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import ProjectForm from './ProjectForm.svelte'
  import TaskForm from './TaskForm.svelte'

  // Fiche d'un projet : où en est l'argent (dépensé face au prévu), les tâches, les dépenses rattachées.
  let {
    householdId,
    projectId,
    onclose,
    onremoved,
  }: { householdId: string; projectId: string; onclose: () => void; onremoved: () => void } = $props()

  let project = $state<Project | null>(null)
  let tasks = $state<ProjectTask[]>([])
  let spending = $state<ProjectExpenses | null>(null)
  let members = $state<Member[]>([])
  let categories = $state<Category[]>([])
  let error = $state<string | null>(null)
  let editing = $state(false)
  let confirmingRemoval = $state(false)
  let editingTask = $state<ProjectTask | null>(null)
  let taskOpen = $state(false)
  let deletingTask = $state<ProjectTask | null>(null)
  let confirmingTaskDeletion = $state(false)
  let newTask = $state('')
  let adding = $state(false)

  onMount(() =>
    run(async () => {
      const [household, loadedCategories] = await Promise.all([getHousehold(householdId), listCategories(householdId), load()])
      members = household.members
      categories = loadedCategories
    }),
  )

  async function load() {
    const [sheet, money] = await Promise.all([getProject(householdId, projectId), projectExpenses(householdId, projectId)])
    project = sheet.project
    if (!dragging) {
      tasks = sheet.tasks
    }
    spending = money
  }

  async function run(action: () => Promise<unknown>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Après un changement de tâche : la fiche, et le bandeau des rappels (une tâche datée en fait partie).
  async function reloadTasks() {
    await load()
    remindersChanged.update((n) => n + 1)
  }

  async function save(input: ProjectInput) {
    await updateProject(householdId, projectId, input)
    editing = false
    await run(reloadTasks)
  }

  function remove() {
    run(async () => {
      await deleteProject(householdId, projectId)
      remindersChanged.update((n) => n + 1)
      onremoved()
    })
  }

  async function add(event: SubmitEvent) {
    event.preventDefault()
    const title = newTask.trim()
    if (!title || adding) {
      return
    }
    adding = true
    await run(async () => {
      await addTask(householdId, projectId, { title, assigneeId: null, dueOn: null })
      newTask = ''
      await load()
    })
    adding = false
  }

  function toggle(task: ProjectTask) {
    // Coche visible aussitôt ; la fiche se recharge ensuite.
    tasks = tasks.map((t) => (t.id === task.id ? { ...t, done: !t.done } : t))
    run(async () => {
      await markTask(householdId, projectId, task.id, !task.done)
      await reloadTasks()
    })
  }

  function openTask(task: ProjectTask) {
    editingTask = task
    taskOpen = true
  }

  async function saveTask(input: TaskInput) {
    if (editingTask) {
      await updateTask(householdId, projectId, editingTask.id, input)
    }
    taskOpen = false
    await run(reloadTasks)
  }

  function askDeleteTask(task: ProjectTask) {
    deletingTask = task
    confirmingTaskDeletion = true
  }

  function removeTask() {
    const target = deletingTask
    if (target) {
      taskOpen = false
      run(async () => {
        await deleteTask(householdId, projectId, target.id)
        await reloadTasks()
      })
    }
  }

  // Glisser-déposer des tâches (poignée) : l'ordre suit le doigt, puis il est enregistré.
  let dragging = false
  function consider(event: CustomEvent<DndEvent<ProjectTask>>) {
    if (isAfterDrop(event)) {
      return
    }
    dragging = true
    tasks = event.detail.items
  }

  function finalize(event: CustomEvent<DndEvent<ProjectTask>>) {
    tasks = dndItems(event)
    dragging = false
    const ids = idsOf(tasks)
    run(() => reorderTasks(householdId, projectId, ids))
  }

  const color = $derived(project ? PROJECT_COLORS[project.color] : 'var(--color-primary)')
  const spent = $derived(spending?.totalCents ?? 0)
  const status = $derived(project ? planStatus(spent, project.plannedCents) : 'none')
  const barColor = $derived(status === 'over' ? 'var(--color-danger)' : status === 'warning' ? 'var(--color-warning)' : color)
  const period = $derived(project ? periodText(project.startOn, project.targetOn, formatDay) : null)
  const categoryName = (id: string) => displayName(categories.find((c) => c.id === id)?.name ?? 'Catégorie')
  const assigneeName = (id: string | null) => (id ? memberName(members, id) : null)
</script>

<div class="top">
  <button class="ghost icon" aria-label="Retour aux projets" onclick={onclose}>
    <ChevronLeft size={20} strokeWidth={2.4} />
  </button>
  <div class="identity">
    <h1><span class="dot" style:background={color} aria-hidden="true"></span>{project ? displayName(project.name) : ''}</h1>
    {#if project}
      <span class="meta">
        <span class="status" class:closed={project.status !== 'ACTIVE'}>{statusLabel(project.status)}</span>
        {#if period}<span>{period}</span>{/if}
      </span>
    {/if}
  </div>
  <button class="ghost icon" aria-label="Modifier le projet" disabled={!project} onclick={() => (editing = true)}>
    <Pencil size={18} />
  </button>
</div>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

{#if !project && !error}
  <Skeleton variant="hero" />
  <Skeleton variant="rows" count={4} />
{:else if project}
  {#if project.description}
    <p class="description">{project.description}</p>
  {/if}

  <section class="card stack money">
    <div class="money-line">
      <span class="label">Dépensé</span>
      <strong class:over={status === 'over'}>{formatCents(spent)}</strong>
      {#if project.plannedCents !== null}
        <span class="muted">sur {formatCents(project.plannedCents)} prévus</span>
      {/if}
    </div>
    {#if project.plannedCents !== null}
      <ProgressBar ratio={project.plannedCents === 0 ? 1 : spent / project.plannedCents} color={barColor} />
      <span class="muted small">
        {#if status === 'over'}
          Dépassé de {formatCents(spent - project.plannedCents)}
        {:else}
          Reste {formatCents(project.plannedCents - spent)}
        {/if}
      </span>
    {/if}
    {#if !project.countsInBudget}
      <span class="off-budget">Budget à part : ses dépenses ne comptent pas dans les totaux du mois.</span>
    {/if}
  </section>

  <section class="stack">
    <h2>Tâches</h2>
    <form class="add-task" onsubmit={add}>
      <input bind:value={newTask} maxlength="120" placeholder="Nouvelle tâche…" aria-label="Nouvelle tâche" />
      <button class="soft" disabled={!newTask.trim() || adding} aria-busy={adding}>Ajouter</button>
    </form>
    {#if tasks.length === 0}
      <p class="muted">Aucune tâche pour l'instant : listez ce qu'il reste à faire.</p>
    {:else}
      <ul class="tasks" use:dragHandleZone={dndOptions(tasks, `tasks-${projectId}`)} onconsider={consider} onfinalize={finalize}>
        {#each tasks as task (task.id)}
          {@const who = assigneeName(task.assigneeId)}
          <li class:shadow={isShadow(task)} class:done={task.done} animate:flip={{ duration: ms(180) }}>
            <DragHandle label="Déplacer {task.title}" />
            <input type="checkbox" checked={task.done} aria-label="Fait : {task.title}" onchange={() => toggle(task)} />
            <button class="task" onclick={() => openTask(task)}>
              <span class="title">{task.title}</span>
              {#if who || task.dueOn}
                <span class="task-meta">{[who, task.dueOn ? formatDay(task.dueOn) : null].filter(Boolean).join(' · ')}</span>
              {/if}
            </button>
            <button class="icon ghost" aria-label="Supprimer {task.title}" title="Supprimer" onclick={() => askDeleteTask(task)}>
              <Trash2 size={17} />
            </button>
          </li>
        {/each}
      </ul>
    {/if}
  </section>

  <section class="stack">
    <h2>Dépenses</h2>
    {#if !spending || spending.expenses.length === 0}
      <p class="muted">Aucune dépense rattachée. Choisissez ce projet en saisissant une dépense dans le Budget.</p>
    {:else}
      <ul class="expenses">
        {#each spending.expenses as expense (expense.id)}
          <li class="card">
            <span class="text">
              <strong>{expense.label ? displayName(expense.label) : categoryName(expense.categoryId)}</strong>
              <span class="muted small">{categoryName(expense.categoryId)} · {formatDay(expense.spentOn)}</span>
            </span>
            <span class="amount">−{formatCents(expense.amountCents)}</span>
          </li>
        {/each}
      </ul>
    {/if}
  </section>

  <button class="danger remove" onclick={() => (confirmingRemoval = true)}>
    <Trash2 size={18} /> Supprimer le projet
  </button>

  <Modal bind:open={editing} title="Modifier le projet">
    <ProjectForm {project} onsave={save} />
  </Modal>
{/if}

<Modal bind:open={taskOpen} title="Modifier la tâche">
  {#if editingTask}
    {#key editingTask.id}
      <TaskForm task={editingTask} {members} onsave={saveTask} />
    {/key}
  {/if}
</Modal>

<ConfirmDialog
  bind:open={confirmingRemoval}
  title="Supprimer « {project ? displayName(project.name) : ''} » ?"
  message="Le projet et ses tâches seront définitivement supprimés. Ses dépenses restent dans le budget, sans projet."
  onconfirm={remove}
/>

<ConfirmDialog
  bind:open={confirmingTaskDeletion}
  title="Supprimer la tâche ?"
  message={deletingTask ? `« ${deletingTask.title} » sera définitivement supprimée.` : ''}
  onconfirm={removeTask}
/>

<style>
  .top {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .identity {
    flex: 1;
    display: grid;
    gap: 4px;
    min-width: 0;
  }
  h1 {
    display: flex;
    align-items: center;
    gap: 10px;
    overflow-wrap: anywhere;
  }
  .dot {
    width: 14px;
    height: 14px;
    border-radius: 50%;
    flex-shrink: 0;
  }
  .meta {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    align-items: center;
    font-size: 0.85rem;
    color: var(--color-text-muted);
  }
  .status {
    padding: 2px 10px;
    border-radius: 999px;
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    font-weight: 700;
  }
  .status.closed {
    background: var(--color-surface-muted);
    color: var(--color-text-muted);
  }
  .description {
    margin: 0;
    overflow-wrap: anywhere;
  }
  .money-line {
    display: flex;
    flex-wrap: wrap;
    align-items: baseline;
    gap: 8px;
  }
  .money-line .label {
    font-weight: 700;
    color: var(--color-text-muted);
  }
  .money-line strong {
    font-size: 1.4rem;
  }
  .money-line strong.over {
    color: var(--color-danger);
  }
  .small {
    font-size: 0.85rem;
  }
  .off-budget {
    font-size: 0.85rem;
    font-weight: 600;
    color: var(--color-on-warning-soft, var(--color-text));
    background: var(--color-warning-soft);
    padding: 8px 12px;
    border-radius: var(--radius-md);
  }
  .add-task {
    display: flex;
    gap: 8px;
  }
  .add-task input {
    flex: 1;
    min-width: 0;
  }
  .tasks,
  .expenses {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .tasks li {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 6px 6px 4px;
    border-radius: var(--radius-md);
    background: var(--color-surface);
  }
  .tasks li.shadow {
    opacity: 0.4;
  }
  .tasks input[type='checkbox'] {
    width: 22px;
    height: 22px;
    flex-shrink: 0;
    accent-color: var(--color-primary);
  }
  /* Les boutons centrent leur contenu par défaut : ici, texte aligné à gauche sur toute la largeur. */
  .task {
    flex: 1;
    min-width: 0;
    display: grid;
    grid-template-columns: minmax(0, 1fr);
    justify-content: stretch;
    justify-items: start;
    gap: 2px;
    padding: 6px 4px;
    min-height: 40px;
    background: transparent;
    box-shadow: none;
    color: var(--color-text);
    text-align: left;
  }
  .task .title {
    font-weight: 700;
    overflow-wrap: anywhere;
  }
  .done .task .title {
    text-decoration: line-through;
    color: var(--color-text-muted);
  }
  .task-meta {
    font-size: 0.8rem;
    color: var(--color-text-muted);
  }
  .tasks .ghost {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
  }
  .expenses li {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .expenses .text {
    flex: 1;
    display: grid;
    gap: 2px;
    min-width: 0;
  }
  .amount {
    color: var(--color-danger);
    font-weight: 800;
    white-space: nowrap;
  }
  .remove {
    justify-self: center;
  }
</style>
