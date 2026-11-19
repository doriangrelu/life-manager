<script lang="ts">
  import { Bell, Check, Pencil, Trash2 } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import {
    addPersonalReminder,
    completePersonalReminder,
    deletePersonalReminder,
    listPersonalReminders,
    listReminders,
    updatePersonalReminder,
  } from '../../api/reminderApi'
  import type { PersonalReminder, PersonalReminderInput, Reminder } from '../../api/types'
  import { formatDay } from '../../dates'
  import { today } from '../../month'
  import { daysUntil, recurrenceText, whenText } from '../../personalReminders'
  import { reminderTitle } from '../../reminders'
  import { displayName } from '../../text'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Fab from '../../ui/molecules/Fab.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import PersonalReminderForm from './PersonalReminderForm.svelte'

  // Écran Rappels de l'espace Maison : ce qui arrive bientôt (rappels saisis et automatiques),
  // puis tous les rappels saisis par les membres, à modifier, supprimer ou marquer « c'est fait ».
  let { householdId }: { householdId: string } = $props()

  let upcoming = $state<Reminder[]>([])
  let saved = $state<PersonalReminder[]>([])
  let loaded = $state(false)
  let error = $state<string | null>(null)
  let editorOpen = $state(false)
  let editing = $state<PersonalReminder | null>(null)
  let deleting = $state<PersonalReminder | null>(null)
  let confirmOpen = $state(false)

  onMount(() => run(load))

  async function load() {
    const [loadedUpcoming, loadedSaved] = await Promise.all([listReminders(householdId), listPersonalReminders(householdId)])
    upcoming = loadedUpcoming
    saved = loadedSaved
    loaded = true
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  function openEditor(reminder: PersonalReminder | null) {
    editing = reminder
    editorOpen = true
  }

  async function save(input: PersonalReminderInput) {
    if (editing) {
      await updatePersonalReminder(householdId, editing.id, input)
    } else {
      await addPersonalReminder(householdId, input)
    }
    editorOpen = false
    await load()
  }

  function done(id: string) {
    run(async () => {
      await completePersonalReminder(householdId, id)
      await load()
    })
  }

  function askDelete(reminder: PersonalReminder) {
    deleting = reminder
    confirmOpen = true
  }

  function remove() {
    const target = deleting
    if (target) {
      run(async () => {
        await deletePersonalReminder(householdId, target.id)
        await load()
      })
    }
  }

  const savedById = $derived(new Map(saved.map((r) => [r.id, r])))
</script>

<div class="stack">
  <h1>Rappels</h1>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if !loaded && !error}
    <Skeleton variant="rows" count={4} />
  {:else if loaded}
    <section class="card stack">
      <h2>Bientôt</h2>
      {#if upcoming.length === 0}
        <p class="muted">Rien à prévoir pour l'instant.</p>
      {:else}
        <ul class="rows">
          {#each upcoming as reminder (reminder.deadlineId)}
            {@const own = reminder.kind === 'PERSONAL' ? savedById.get(reminder.deadlineId) : undefined}
            <li class:late={reminder.status === 'OVERDUE'}>
              <span class="text">
                <strong>{reminderTitle(reminder)}</strong>
                <span class="when">{formatDay(reminder.dueOn)} · {whenText(reminder.daysLeft)}</span>
              </span>
              {#if own}
                <button class="soft done" onclick={() => done(own.id)}><Check size={16} /> C'est fait</button>
              {/if}
            </li>
          {/each}
        </ul>
      {/if}
    </section>

    <section class="stack">
      <h2>Rappels saisis</h2>
      {#if saved.length === 0}
        <div class="card empty">
          <p class="muted">
            Notez ce qu'il ne faut pas oublier : déclarer les impôts, ramoner la cheminée, renouveler la mutuelle…
            Le rappel apparaîtra dans le bandeau et dans les notifications à l'approche de sa date.
          </p>
          <button class="soft" onclick={() => openEditor(null)}>Ajouter un rappel</button>
        </div>
      {:else}
        <ul class="saved">
          {#each saved as reminder (reminder.id)}
            {@const days = daysUntil(reminder, today())}
            <li class="card">
              <span class="bubble" aria-hidden="true"><Bell size={18} /></span>
              <span class="text">
                <strong>{displayName(reminder.title)}</strong>
                <span class="when" class:late-text={days < 0}>
                  {formatDay(reminder.dueOn)} · {whenText(days)}{reminder.recurrence !== 'NONE' ? ` · ${recurrenceText(reminder.recurrence)}` : ''}
                </span>
                {#if reminder.note}<span class="note">{reminder.note}</span>{/if}
              </span>
              <span class="actions">
                <button class="icon ghost" aria-label="C'est fait : {reminder.title}" title="C'est fait" onclick={() => done(reminder.id)}>
                  <Check size={18} />
                </button>
                <button class="icon ghost" aria-label="Modifier {reminder.title}" title="Modifier" onclick={() => openEditor(reminder)}>
                  <Pencil size={17} />
                </button>
                <button class="icon ghost" aria-label="Supprimer {reminder.title}" title="Supprimer" onclick={() => askDelete(reminder)}>
                  <Trash2 size={17} />
                </button>
              </span>
            </li>
          {/each}
        </ul>
      {/if}
    </section>
  {/if}

  <Fab label="Ajouter un rappel" onclick={() => openEditor(null)} />
</div>

<Modal bind:open={editorOpen} title={editing ? 'Modifier le rappel' : 'Nouveau rappel'}>
  <PersonalReminderForm reminder={editing} onsave={save} />
</Modal>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Supprimer « {deleting ? displayName(deleting.title) : ''} » ?"
  message="Le rappel disparaît pour tout le foyer."
  confirmLabel="Supprimer"
  onconfirm={remove}
/>

<style>
  .rows,
  .saved {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .rows li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }
  .text {
    display: grid;
    gap: 2px;
    min-width: 0;
    flex: 1;
  }
  .when {
    font-size: 0.85rem;
    color: var(--color-text-muted);
  }
  .late .when,
  .late-text {
    color: var(--color-danger);
    font-weight: 700;
  }
  .done {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 36px;
    padding: 0 12px;
    font-size: 0.85rem;
    white-space: nowrap;
  }
  .saved li {
    display: flex;
    align-items: flex-start;
    gap: 12px;
  }
  .bubble {
    width: 38px;
    height: 38px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .note {
    font-size: 0.88rem;
    overflow-wrap: anywhere;
  }
  .actions {
    display: flex;
    gap: 2px;
    margin: -6px -6px 0 0;
  }
  .actions .ghost {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
  }
  .empty {
    display: grid;
    gap: 12px;
    justify-items: start;
  }
  .empty p {
    margin: 0;
  }
</style>
