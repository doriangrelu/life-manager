<script lang="ts">
  import { CircleCheck, X } from '@lucide/svelte'
  import { getHousehold } from '../../api/householdApi'
  import { HISTORY_PAGE_SIZE, listReminderHistory, removeReminderDone } from '../../api/reminderApi'
  import type { Member, ReminderDone } from '../../api/types'
  import { formatDay } from '../../dates'
  import { memberName } from '../../members'
  import { displayName } from '../../text'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'

  // Historique des rappels faits : du plus récent au plus ancien, par pages, chaque ligne retirable.
  // refreshKey change après un « c'est fait » : la première page est rechargée.
  let { householdId, refreshKey }: { householdId: string; refreshKey: number } = $props()

  let lines = $state<ReminderDone[]>([])
  let members = $state<Member[]>([])
  let hasMore = $state(false)
  let loadingMore = $state(false)
  let error = $state<string | null>(null)
  let removing = $state<ReminderDone | null>(null)
  let confirmOpen = $state(false)

  $effect(() => {
    void refreshKey
    run(reload)
  })

  async function reload() {
    const [page, household] = await Promise.all([listReminderHistory(householdId), getHousehold(householdId)])
    members = household.members
    lines = page
    hasMore = page.length === HISTORY_PAGE_SIZE
  }

  async function more() {
    loadingMore = true
    await run(async () => {
      const page = await listReminderHistory(householdId, lines.length)
      lines = [...lines, ...page]
      hasMore = page.length === HISTORY_PAGE_SIZE
    })
    loadingMore = false
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  function askRemove(line: ReminderDone) {
    removing = line
    confirmOpen = true
  }

  function remove() {
    const target = removing
    if (target) {
      run(async () => {
        await removeReminderDone(householdId, target.id)
        lines = lines.filter((line) => line.id !== target.id)
      })
    }
  }
</script>

<section class="stack">
  <h2>Historique</h2>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if lines.length === 0}
    <p class="muted">Les rappels marqués « C'est fait » apparaîtront ici.</p>
  {:else}
    <ul class="card history">
      {#each lines as line (line.id)}
        <li>
          <span class="mark" aria-hidden="true"><CircleCheck size={18} /></span>
          <span class="text">
            <strong>{displayName(line.title)}</strong>
            <span class="when">
              Fait le {formatDay(line.doneAt)} par {memberName(members, line.doneBy)} · prévu le {formatDay(line.dueOn)}
            </span>
          </span>
          <button
            class="icon ghost"
            aria-label="Retirer {line.title} de l'historique"
            title="Retirer de l'historique"
            onclick={() => askRemove(line)}
          >
            <X size={17} />
          </button>
        </li>
      {/each}
    </ul>
    {#if hasMore}
      <button class="soft more" disabled={loadingMore} aria-busy={loadingMore} onclick={more}>Voir plus</button>
    {/if}
  {/if}
</section>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Retirer cette ligne ?"
  message="« {removing ? displayName(removing.title) : ''} » disparaît de l'historique pour tout le foyer."
  confirmLabel="Retirer"
  onconfirm={remove}
/>

<style>
  .history {
    list-style: none;
    margin: 0;
    padding: 4px 16px;
    display: grid;
  }
  .history li {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 0;
  }
  .history li + li {
    border-top: 1px solid var(--color-border);
  }
  .mark {
    color: var(--color-success);
    display: flex;
    flex-shrink: 0;
  }
  .text {
    display: grid;
    gap: 2px;
    min-width: 0;
    flex: 1;
  }
  .text strong {
    overflow-wrap: anywhere;
  }
  .when {
    font-size: 0.85rem;
    color: var(--color-text-muted);
  }
  .history .ghost {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
    flex-shrink: 0;
  }
  .more {
    justify-self: center;
  }
</style>
