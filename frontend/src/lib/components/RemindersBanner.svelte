<script lang="ts">
  import { Bell } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { listReminders } from '../api/reminderApi'
  import type { Reminder } from '../api/types'
  import { today } from '../month'
  import { askPermission, notificationState, notify, type NotificationState } from '../notifications'
  import { notifiedMemo, rememberNotified } from '../preferences'
  import { reminderDelay, reminderTitle, remindersChanged, toNotify } from '../reminders'

  // Bandeau des rappels du foyer, visible sur tous les onglets ; notifie aussi l'appareil si autorisé.
  // refreshKey : change quand il faut recharger (ex. changement d'onglet après une modification de véhicule).
  let { householdId, refreshKey }: { householdId: string; refreshKey: string } = $props()

  let reminders = $state<Reminder[]>([])
  let permission = $state<NotificationState>(notificationState())

  onMount(() => {
    const onVisible = () => document.visibilityState === 'visible' && load()
    document.addEventListener('visibilitychange', onVisible)
    return () => document.removeEventListener('visibilitychange', onVisible)
  })

  $effect(() => {
    void refreshKey
    void $remindersChanged
    load()
  })

  async function load() {
    try {
      reminders = await listReminders(householdId)
      await notifyFresh()
    } catch {
      // Un rappel indisponible ne doit pas gêner le reste de l'écran.
    }
  }

  async function notifyFresh() {
    if (permission !== 'granted') {
      return
    }
    const { fresh, memo } = toNotify(reminders, notifiedMemo(), today())
    rememberNotified(memo)
    for (const reminder of fresh) {
      await notify(reminderTitle(reminder), reminderDelay(reminder), reminder.deadlineId)
    }
  }

  async function enable() {
    permission = await askPermission()
    await notifyFresh()
  }
</script>

{#if reminders.length > 0}
  <aside class="reminders" aria-label="Rappels">
    <span class="icon" aria-hidden="true"><Bell size={20} /></span>
    <div class="stack-tight">
      <ul>
        {#each reminders as reminder (reminder.deadlineId)}
          <li class={reminder.status === 'OVERDUE' ? 'overdue' : 'soon'}>
            <strong>{reminderTitle(reminder)}</strong> : {reminderDelay(reminder)}
          </li>
        {/each}
      </ul>
      {#if permission === 'default'}
        <button class="link" onclick={enable}>Activer les notifications sur cet appareil</button>
      {:else if permission === 'denied'}
        <p class="note">Notifications bloquées par le navigateur : les rappels restent affichés ici.</p>
      {/if}
    </div>
  </aside>
{/if}

<style>
  .reminders {
    display: flex;
    gap: 12px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
  }
  .icon {
    flex-shrink: 0;
    padding-top: 2px;
  }
  .stack-tight {
    display: grid;
    gap: 4px;
    min-width: 0;
  }
  ul {
    margin: 0;
    padding: 0;
    list-style: none;
    display: grid;
    gap: 4px;
  }
  .overdue strong {
    color: var(--color-danger);
  }
  .link {
    justify-self: start;
    min-height: 36px;
    padding: 0;
    color: var(--color-on-warning-soft);
    text-decoration: underline;
  }
  .note {
    margin: 0;
    font-size: 0.85rem;
  }
</style>
