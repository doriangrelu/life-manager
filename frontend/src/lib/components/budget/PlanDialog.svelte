<script lang="ts">
  import { untrack } from 'svelte'
  import type { CategoryPlanInput, PlanScope } from '../../api/types'
  import { centsToInput, formatCents, parseAmountToCents } from '../../money'
  import { monthLabel } from '../../month'
  import type { CategoryProgress } from '../../plan'
  import { displayName } from '../../text'
  import AmountInput from '../../ui/atoms/AmountInput.svelte'
  import Field from '../../ui/molecules/Field.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import { amount as amountRule, check } from '../../validation'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Réglage du budget prévu d'une catégorie : un montant, pour tous les mois (à partir du mois affiché)
  // ou pour ce mois seulement. « Retirer » enlève le prévu avec la même portée.
  let {
    open = $bindable(false),
    line,
    month,
    onsave,
  }: {
    open: boolean
    line: CategoryProgress | null
    month: string
    onsave: (categoryId: string, plan: CategoryPlanInput) => Promise<boolean>
  } = $props()

  let amount = $state('')
  let scope = $state<PlanScope>('FROM_MONTH')
  let error = $state<string | null>(null)
  let busy = $state(false)
  let revealed = $state(false)
  const amountError = $derived(check(amount, amountRule('400')))

  // À chaque ouverture : le prévu actuel de la catégorie, et « Tous les mois » par défaut.
  $effect(() => {
    if (open) {
      untrack(() => {
        amount = line?.plannedCents ? centsToInput(line.plannedCents) : ''
        scope = 'FROM_MONTH'
        error = null
        revealed = false
      })
    }
  })

  const explanation = $derived(
    scope === 'FROM_MONTH'
      ? `Valable à partir de ${monthLabel(month)}, jusqu'au prochain changement. Les mois précédents ne bougent pas.`
      : `Valable pour ${monthLabel(month)} seulement.`,
  )

  async function save(plannedCents: number | null) {
    if (line) {
      busy = true
      error = null
      try {
        const saved = await onsave(line.categoryId, { month, scope, plannedCents })
        open = !saved
      } catch (e) {
        // Refus du serveur : montré dans la fenêtre (l'écran derrière est caché).
        error = e instanceof Error ? e.message : String(e)
      } finally {
        busy = false
      }
    }
  }

  function submit(event: SubmitEvent) {
    event.preventDefault()
    const cents = parseAmountToCents(amount)
    if (cents === null) {
      revealed = true
      return
    }
    error = null
    save(cents)
  }
</script>

<Modal bind:open title="Budget prévu · {line ? displayName(line.name) : ''}">
  {#if line}
    <form class="stack" onsubmit={submit}>
      <Field error={amountError} reveal={revealed}>
        {#snippet children()}
          <AmountInput bind:value={amount} label="Montant prévu par mois" />
        {/snippet}
      </Field>
      <Segmented
        label="Pour quels mois"
        info="« Tous les mois » : le montant vaut à partir du mois affiché, jusqu'au prochain changement. « Ce mois seulement » : un ajustement ponctuel, sans toucher aux autres mois."
        options={[
          { value: 'FROM_MONTH', label: 'Tous les mois' },
          { value: 'MONTH_ONLY', label: 'Ce mois seulement' },
        ]}
        bind:value={scope}
      />
      <p class="muted explanation" aria-live="polite">{explanation}</p>
      <p class="muted spent">Dépensé ce mois : {formatCents(line.spentCents)}</p>
      {#if error}
        <p class="error" role="alert">{error}</p>
      {/if}
      <button class="large" disabled={busy} aria-busy={busy}>Enregistrer</button>
      {#if line.plannedCents !== null}
        <button type="button" class="link remove" disabled={busy} onclick={() => save(null)}>
          Retirer le budget prévu ({scope === 'FROM_MONTH' ? 'à partir de ce mois' : 'ce mois seulement'})
        </button>
      {/if}
    </form>
  {/if}
</Modal>

<style>
  .explanation,
  .spent {
    margin: 0;
    font-size: 0.85rem;
    text-align: center;
  }
  .explanation::first-letter {
    text-transform: uppercase;
  }
  .remove {
    justify-self: center;
    color: var(--color-danger);
  }
</style>
