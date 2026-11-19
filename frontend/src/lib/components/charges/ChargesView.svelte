<script lang="ts">
  import { ChevronRight } from '@lucide/svelte'
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import { onMount } from 'svelte'
  import { addCharge, confirmOccurrence, listCharges, skipOccurrence } from '../../api/billsApi'
  import { listCategories } from '../../api/budgetApi'
  import { getHousehold } from '../../api/householdApi'
  import type { Category, Charge, ChargeInput, Member, Occurrence, UserProfile } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import { frequencyLabel, monthlyEstimateCents, sortCharges, statusLabel } from '../../charges'
  import { formatDay } from '../../dates'
  import { formatCents } from '../../money'
  import { listMove, ms, rise } from '../../motion'
  import { navigate, route } from '../../route'
  import { displayName } from '../../text'
  import Fab from '../../ui/molecules/Fab.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import ChargeDetail from './ChargeDetail.svelte'
  import ChargeForm from './ChargeForm.svelte'
  import ConfirmAmountDialog from './ConfirmAmountDialog.svelte'

  // Onglet Charges : abonnements et factures qui reviennent, leurs échéances et ce qui attend une confirmation.
  let { householdId, profile }: { householdId: string; profile: UserProfile } = $props()

  let charges = $state<Charge[]>([])
  let categories = $state<Category[]>([])
  let members = $state<Member[]>([])
  let loaded = $state(false)
  let error = $state<string | null>(null)
  let adding = $state(false)
  let confirming = $state<{ charge: Charge; occurrence: Occurrence } | null>(null)
  let confirmOpen = $state(false)

  const sorted = $derived(sortCharges(charges))
  const toConfirm = $derived(sorted.filter((c) => c.next?.status === 'TO_CONFIRM'))
  const others = $derived(sorted.filter((c) => c.next?.status !== 'TO_CONFIRM'))
  const active = $derived(charges.filter((c) => c.active))
  const openedId = $derived($route.chargeId ?? null)
  const opened = $derived(charges.find((c) => c.id === openedId) ?? null)

  onMount(() =>
    run(async () => {
      const [loadedCharges, household, loadedCategories] = await Promise.all([
        listCharges(householdId),
        getHousehold(householdId),
        listCategories(householdId),
      ])
      charges = loadedCharges
      members = household.members
      categories = loadedCategories
      loaded = true
    }),
  )

  // Charge inconnue (adresse d'un autre foyer…) : retour à la liste.
  $effect(() => {
    if (loaded && openedId !== null && !opened) {
      navigate({ section: 'charges' }, { replace: true })
    }
  })

  async function reload() {
    charges = await listCharges(householdId)
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  async function add(input: ChargeInput) {
    await addCharge(householdId, input)
    adding = false
    await reload()
  }

  function askConfirm(charge: Charge) {
    if (charge.next) {
      confirming = { charge, occurrence: charge.next }
      confirmOpen = true
    }
  }

  async function confirm(amountCents: number) {
    const target = confirming
    if (target) {
      await confirmOccurrence(householdId, target.charge.id, target.occurrence.id, amountCents)
      await reload()
    }
  }

  function skip(charge: Charge) {
    const next = charge.next
    if (next) {
      run(async () => {
        await skipOccurrence(householdId, charge.id, next.id)
        await reload()
      })
    }
  }

  function categoryName(charge: Charge): string {
    return categories.find((c) => c.id === charge.budgetCategoryId)?.name ?? 'autre'
  }
</script>

{#if opened}
  <div class="stack" in:rise>
    <ChargeDetail
      {householdId}
      charge={opened}
      {categories}
      {members}
      currentUserId={profile.userId}
      onchanged={() => run(reload)}
      onclose={() => navigate({ section: 'charges' })}
    />
  </div>
{:else}
  <div class="stack" in:rise>
    <h1>Charges</h1>

    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}

    {#if !loaded && !error}
      <Skeleton variant="hero" />
      <Skeleton variant="cards" count={3} />
    {:else if loaded}
      <section class="summary">
        <span class="caption">Charges par mois (estimation)</span>
        <strong class="total amount">{formatCents(monthlyEstimateCents(charges))}</strong>
        <span class="caption">
          {active.length === 0
            ? 'Aucune charge pour l’instant'
            : `${active.length} charge${active.length > 1 ? 's' : ''} en cours`}
        </span>
      </section>

      {#if toConfirm.length > 0}
        <section class="stack">
          <h2>À confirmer</h2>
          {#each toConfirm as charge (charge.id)}
            {@const next = charge.next}
            {#if next}
              <article class="card pending" in:rise out:fade={{ duration: ms(150) }}>
                <div class="pending-text">
                  <strong>{displayName(charge.name)}</strong>
                  <span class="muted">
                    Échéance du {formatDay(next.dueOn)} · prévu {formatCents(next.expectedCents)}
                  </span>
                </div>
                <div class="pending-actions">
                  <button onclick={() => askConfirm(charge)}>Confirmer</button>
                  <button class="soft" onclick={() => skip(charge)}>Sauter</button>
                </div>
              </article>
            {/if}
          {/each}
        </section>
      {/if}

      {#if others.length > 0}
        <ul class="charges">
          {#each others as charge (charge.id)}
            {@const style = categoryStyle(categoryName(charge))}
            <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>
              <button class="charge" class:ended={!charge.active} onclick={() => navigate({ section: 'charges', chargeId: charge.id })}>
                <span class="bubble" style:background={style.soft} aria-hidden="true">
                  <style.icon size={20} color={style.strong} />
                </span>
                <span class="text">
                  <strong>{displayName(charge.name)}</strong>
                  <span class="muted">
                    {#if !charge.active}
                      Résiliée
                    {:else if charge.next}
                      {frequencyLabel(charge.frequency)} · {statusLabel(charge.next.status).toLowerCase()} le {formatDay(charge.next.dueOn)}
                    {:else}
                      {frequencyLabel(charge.frequency)}
                    {/if}
                  </span>
                </span>
                <span class="amount">
                  {charge.amountKind === 'VARIABLE' ? '≈ ' : ''}{formatCents(charge.amountCents)}
                </span>
                <ChevronRight size={18} />
              </button>
            </li>
          {/each}
        </ul>
      {:else if toConfirm.length === 0}
        <div class="empty card">
          <p class="muted">
            Ajoutez ce qui revient chaque mois ou chaque année : internet, électricité, assurances, abonnements…
            Les dépenses arriveront toutes seules dans le budget.
          </p>
          <button class="soft" onclick={() => (adding = true)}>Ajouter une charge</button>
        </div>
      {/if}
    {/if}

    <Fab label="Ajouter une charge" onclick={() => (adding = true)} />
  </div>
{/if}

<Modal bind:open={adding} title="Nouvelle charge">
  <ChargeForm charge={null} {categories} {members} currentUserId={profile.userId} onsave={add} />
</Modal>

{#if confirming}
  <ConfirmAmountDialog
    bind:open={confirmOpen}
    chargeName={displayName(confirming.charge.name)}
    occurrence={confirming.occurrence}
    onconfirm={confirm}
  />
{/if}

<style>
  .summary {
    display: grid;
    gap: 4px;
    padding: 20px;
    border-radius: var(--radius-xl);
    background: var(--color-primary);
    color: var(--color-on-primary);
  }
  .summary .caption {
    font-size: 0.85rem;
    font-weight: 600;
    opacity: 0.85;
  }
  .total {
    font-size: 2.2rem;
    letter-spacing: -0.02em;
  }
  .pending {
    display: grid;
    gap: 12px;
    border: 2px solid var(--color-warning);
    background: var(--color-warning-soft);
  }
  .pending-text {
    display: grid;
    gap: 2px;
  }
  .pending-text .muted {
    color: var(--color-on-warning-soft);
  }
  .pending-actions {
    display: flex;
    gap: 8px;
  }
  .pending-actions button {
    flex: 1;
  }
  .charges {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .charge {
    width: 100%;
    justify-content: flex-start;
    gap: 12px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
    color: var(--color-text);
    text-align: left;
  }
  .charge.ended {
    opacity: 0.6;
  }
  @media (hover: hover) {
    .charge:hover {
      filter: none;
      box-shadow:
        var(--shadow-sm),
        0 0 0 2px var(--color-primary-soft);
    }
  }
  .bubble {
    width: 44px;
    height: 44px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .text {
    flex: 1;
    display: grid;
    gap: 2px;
    min-width: 0;
  }
  .text .muted {
    font-size: 0.85rem;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .empty {
    display: grid;
    justify-items: start;
    gap: 12px;
  }
</style>
