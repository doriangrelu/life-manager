<script lang="ts">
  import { ChevronLeft, Paperclip, Pencil } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { flip } from 'svelte/animate'
  import {
    confirmOccurrence,
    invoiceContent,
    listInvoices,
    listOccurrences,
    removeInvoice,
    skipOccurrence,
    terminateCharge,
    updateCharge,
    uploadInvoice,
  } from '../../api/billsApi'
  import type { Category, Charge, ChargeInput, Member, Occurrence } from '../../api/types'
  import { attachmentCountLabel } from '../../attachments'
  import { frequencyLabel, occurrenceCents, statusLabel } from '../../charges'
  import { formatDay } from '../../dates'
  import { memberName } from '../../members'
  import { formatCents } from '../../money'
  import { listMove, rise } from '../../motion'
  import { displayName } from '../../text'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import AttachmentsDialog from '../AttachmentsDialog.svelte'
  import ChargeForm from './ChargeForm.svelte'
  import ConfirmAmountDialog from './ConfirmAmountDialog.svelte'

  // Fiche d'une charge : ce qu'elle coûte, ses échéances (confirmer, sauter, factures), modification, résiliation.
  let {
    householdId,
    charge,
    categories,
    members,
    currentUserId,
    onchanged,
    onclose,
  }: {
    householdId: string
    charge: Charge
    categories: Category[]
    members: Member[]
    currentUserId: string
    onchanged: () => void
    onclose: () => void
  } = $props()

  let occurrences = $state<Occurrence[]>([])
  let loaded = $state(false)
  let error = $state<string | null>(null)
  let editing = $state(false)
  let confirmingEnd = $state(false)
  let confirming = $state<Occurrence | null>(null)
  let confirmOpen = $state(false)
  let invoicesOf = $state<Occurrence | null>(null)
  let invoicesOpen = $state(false)

  const name = $derived(displayName(charge.name))
  const category = $derived(categories.find((c) => c.id === charge.budgetCategoryId)?.name ?? 'autre')
  const payer = $derived(memberName(members, charge.payerId))
  const beneficiary = $derived(memberName(members, charge.beneficiaryId))

  onMount(() => run(load))

  async function load() {
    occurrences = await listOccurrences(householdId, charge.id)
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

  /** Après un geste : les échéances de la fiche et la liste des charges. */
  async function refresh() {
    await load()
    onchanged()
  }

  async function save(input: ChargeInput) {
    await updateCharge(householdId, charge.id, input)
    editing = false
    await refresh()
  }

  function askConfirm(occurrence: Occurrence) {
    confirming = occurrence
    confirmOpen = true
  }

  async function confirm(amountCents: number) {
    const occurrence = confirming
    if (occurrence) {
      await confirmOccurrence(householdId, charge.id, occurrence.id, amountCents)
      await refresh()
    }
  }

  function skip(occurrence: Occurrence) {
    run(async () => {
      await skipOccurrence(householdId, charge.id, occurrence.id)
      await refresh()
    })
  }

  function terminate() {
    run(async () => {
      await terminateCharge(householdId, charge.id)
      await refresh()
    })
  }

  function showInvoices(occurrence: Occurrence) {
    invoicesOf = occurrence
    invoicesOpen = true
  }
</script>

<div class="header">
  <button class="ghost icon" aria-label="Retour aux charges" onclick={onclose}><ChevronLeft size={20} /></button>
  <h1>{name}</h1>
  {#if charge.active}
    <button class="ghost icon" aria-label="Modifier {name}" onclick={() => (editing = true)}><Pencil size={18} /></button>
  {/if}
</div>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

<section class="card facts">
  <div>
    <span class="caption">{charge.amountKind === 'FIXED' ? 'Montant' : 'Montant prévu'}</span>
    <strong class="amount">{formatCents(charge.amountCents)}</strong>
  </div>
  <div>
    <span class="caption">Fréquence</span>
    <strong>{frequencyLabel(charge.frequency)}</strong>
  </div>
  <div>
    <span class="caption">Catégorie</span>
    <strong>{displayName(category)}</strong>
  </div>
  <div>
    {#if charge.direction === 'INCOME'}
      <span class="caption">Reçu par</span>
      <strong>{payer}</strong>
    {:else}
      <span class="caption">Payé par · pour</span>
      <strong>{payer} · {beneficiary}</strong>
    {/if}
  </div>
  <p class="muted note">
    {#if !charge.active}
      Charge résiliée : plus aucune échéance ne sera créée.
    {:else if charge.amountKind === 'FIXED'}
      Chaque échéance est ajoutée seule au budget.
    {:else}
      Le montant de chaque échéance est à confirmer à réception de la facture.
    {/if}
  </p>
</section>

<section class="stack">
  <h2>Échéances</h2>
  {#if !loaded && !error}
    <Skeleton variant="cards" count={3} />
  {:else}
    <ul class="occurrences">
      {#each occurrences as occurrence (occurrence.id)}
        {@const open = occurrence.status === 'UPCOMING' || occurrence.status === 'TO_CONFIRM'}
        <li class="card occurrence" class:to-confirm={occurrence.status === 'TO_CONFIRM'} animate:flip={listMove} in:rise>
          <div class="line">
            <span class="date">{formatDay(occurrence.dueOn)}</span>
            <span class="badge {occurrence.status.toLowerCase()}">{statusLabel(occurrence.status)}</span>
            <span class="amount">{occurrence.status === 'SKIPPED' ? '—' : formatCents(occurrenceCents(occurrence))}</span>
          </div>
          <div class="actions">
            <button class="files" onclick={() => showInvoices(occurrence)}>
              <Paperclip size={15} />
              {occurrence.invoiceCount > 0 ? attachmentCountLabel(occurrence.invoiceCount) : 'Joindre la facture'}
            </button>
            {#if open}
              <button class="link" onclick={() => askConfirm(occurrence)}>
                {occurrence.status === 'TO_CONFIRM' ? 'Confirmer' : 'Payer maintenant'}
              </button>
              <button class="link" onclick={() => skip(occurrence)}>Sauter</button>
            {/if}
          </div>
        </li>
      {/each}
    </ul>
  {/if}
</section>

{#if charge.active}
  <button class="danger" onclick={() => (confirmingEnd = true)}>Résilier la charge</button>
{/if}

<Modal bind:open={editing} title="Modifier la charge">
  <ChargeForm {charge} {categories} {members} {currentUserId} onsave={save} />
</Modal>

<ConfirmDialog
  bind:open={confirmingEnd}
  title="Résilier « {name} » ?"
  message="Plus aucune échéance ne sera créée. Les échéances passées, leurs dépenses et leurs factures restent."
  confirmLabel="Résilier"
  onconfirm={terminate}
/>

{#if confirming}
  <ConfirmAmountDialog bind:open={confirmOpen} chargeName={name} occurrence={confirming} onconfirm={confirm} />
{/if}

{#if invoicesOf}
  {@const occurrenceId = invoicesOf.id}
  <AttachmentsDialog
    bind:open={invoicesOpen}
    title="Factures"
    subtitle="{name} · échéance du {formatDay(invoicesOf.dueOn)}"
    files={{
      list: () => listInvoices(householdId, charge.id, occurrenceId),
      upload: (file, onprogress) => uploadInvoice(householdId, charge.id, occurrenceId, file, onprogress),
      content: (id) => invoiceContent(householdId, charge.id, occurrenceId, id),
      remove: (id) => removeInvoice(householdId, charge.id, occurrenceId, id),
    }}
    onchanged={() => run(load)}
  />
{/if}

<style>
  .header {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .header h1 {
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .facts {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px;
  }
  .facts div {
    display: grid;
    gap: 2px;
  }
  .caption {
    font-size: 0.8rem;
    font-weight: 700;
    color: var(--color-text-muted);
  }
  .note {
    grid-column: 1 / -1;
    font-size: 0.85rem;
  }
  .occurrences {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .occurrence {
    display: grid;
    gap: 10px;
    padding: 14px;
  }
  .occurrence.to-confirm {
    border: 2px solid var(--color-warning);
  }
  .line {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .date {
    flex: 1;
    font-weight: 700;
  }
  .badge {
    padding: 2px 10px;
    border-radius: 999px;
    font-size: 0.75rem;
    font-weight: 800;
    background: var(--color-surface-muted);
    color: var(--color-text-muted);
  }
  .badge.paid {
    background: var(--color-success-soft);
    color: var(--color-on-success-soft);
  }
  .badge.to_confirm {
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
  }
  .badge.upcoming {
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
  }
  .actions {
    display: flex;
    align-items: center;
    gap: 4px;
    flex-wrap: wrap;
  }
  .files {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 36px;
    padding: 0 12px;
    margin-right: auto;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    font-size: 0.85rem;
    font-weight: 700;
    white-space: nowrap;
  }
  .actions .link {
    min-height: 36px;
    font-size: 0.9rem;
  }
</style>
