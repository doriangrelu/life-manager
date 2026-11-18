<script lang="ts">
  import { untrack } from 'svelte'
  import type { Occurrence } from '../../api/types'
  import { formatDay } from '../../dates'
  import { centsToInput, parseAmountToCents } from '../../money'
  import AmountInput from '../../ui/AmountInput.svelte'
  import Modal from '../../ui/Modal.svelte'

  // Montant réel d'une échéance (facture reçue) : prérempli avec le montant prévu.
  let {
    open = $bindable(false),
    chargeName,
    occurrence,
    onconfirm,
  }: {
    open: boolean
    chargeName: string
    occurrence: Occurrence
    onconfirm: (amountCents: number) => Promise<void>
  } = $props()

  let amount = $state(untrack(() => centsToInput(occurrence.expectedCents)))
  let busy = $state(false)
  let error = $state<string | null>(null)

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    const cents = parseAmountToCents(amount)
    if (cents === null) {
      error = 'Montant invalide (exemple : 92,40)'
      return
    }
    busy = true
    error = null
    try {
      await onconfirm(cents)
      open = false
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<Modal bind:open title="Montant de la facture">
  <form class="stack" onsubmit={submit}>
    <p class="muted">{chargeName} · échéance du {formatDay(occurrence.dueOn)}</p>
    <AmountInput bind:value={amount} label="Montant payé" />
    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}
    <button class="large" disabled={busy} aria-busy={busy}>Confirmer et ajouter au budget</button>
  </form>
</Modal>
