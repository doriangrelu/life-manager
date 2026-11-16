<script lang="ts">
  import { onMount } from 'svelte'
  import { listReminders } from '../api/reminderApi'
  import type { Reminder } from '../api/types'
  import { today } from '../month'
  import { askPermission, notificationState, notify, type NotificationState } from '../notifications'
  import { notifiedMemo, rememberNotified } from '../preferences'
  import { reminderDelay, reminderTitle, toNotify } from '../reminders'

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
  <aside class="card reminders" aria-label="Rappels">
    <h2>Rappels</h2>
    <ul>
      {#each reminders as reminder (reminder.deadlineId)}
        <li class={reminder.status === 'OVERDUE' ? 'overdue' : 'soon'}>
          <strong>{reminderTitle(reminder)}</strong> : {reminderDelay(reminder)}
        </li>
      {/each}
    </ul>
    {#if permission === 'default'}
      <button class="link" onclick={enable}>Activer les notifications sur cet appareil</button>
    {/if}
  </aside>
{/if}

<style>
  .reminders {
    display: grid;
    gap: 8px;
    margin-bottom: 16px;
    border-color: var(--warning);
  }
  ul {
    margin: 0;
    padding-left: 18px;
  }
  .overdue {
    color: var(--error);
  }
  .soon {
    color: var(--warning);
  }
  .link {
    justify-self: start;
  }
</style>
