<script lang="ts">
  import type { MaintenanceInput, MaintenanceKind } from '../../api/types'
  import { parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import {
    automaticNextDue,
    dayAfter,
    formatDay,
    MAINTENANCE_KINDS,
    nextDueLabel,
    setsDeadline,
  } from '../../vehicles'
  import Chip from '../../ui/atoms/Chip.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'

  // Nouvelle opération du carnet (dans une modale) : date et type pré-remplis, le reste facultatif.
  // Un contrôle technique ou une révision fixe aussi la prochaine échéance du véhicule.
  let { onsave }: { onsave: (input: MaintenanceInput) => Promise<boolean> } = $props()

  let performedOn = $state(today())
  let kind = $state<MaintenanceKind>('SERVICE')
  let mileage = $state('')
  let cost = $state('')
  let note = $state('')
  let otherLabel = $state('') // précision tapée sous « Autre »
  let nextMode = $state<'auto' | 'precise'>('auto')
  let preciseNextDue = $state('')
  let error = $state<string | null>(null)
  let busy = $state(false)
  const withDeadline = $derived(setsDeadline(kind))
  const automatic = $derived(automaticNextDue(kind, performedOn))

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    const input = toInput()
    if (input) {
      busy = true
      await onsave(input)
      busy = false
    }
  }

  // Champs facultatifs : vide = non renseigné ; sinon la valeur doit être lisible.
  function toInput(): MaintenanceInput | null {
    error = null
    const mileageKm = mileage.trim() ? Number(mileage.replace(/\s/g, '')) : null
    if (mileageKm !== null && !Number.isInteger(mileageKm)) {
      error = 'Kilométrage invalide (exemple : 45000)'
      return null
    }
    const costCents = cost.trim() ? parseAmountToCents(cost) : null
    if (cost.trim() && costCents === null) {
      error = 'Coût invalide (exemple : 189,90)'
      return null
    }
    const precise = withDeadline && nextMode === 'precise'
    if (precise && !(preciseNextDue > performedOn)) {
      error = 'La prochaine échéance doit suivre la date de l\'opération'
      return null
    }
    const nextDueOn = precise ? preciseNextDue : null
    // La précision n'est envoyée que pour « Autre » (le serveur la refuse ailleurs).
    const label = kind === 'OTHER' ? otherLabel.trim() || null : null
    return { performedOn, kind, mileageKm, costCents, note: note.trim() || null, nextDueOn, otherLabel: label }
  }
</script>

<form class="stack" onsubmit={submit}>
  <fieldset class="kinds">
    <legend>Type</legend>
    <div class="chips">
      {#each MAINTENANCE_KINDS as option (option.kind)}
        <Chip selected={kind === option.kind} onclick={() => (kind = option.kind)}>{option.label}</Chip>
      {/each}
    </div>
    {#if kind === 'OTHER'}
      <label class="precision">
        Préciser (facultatif)
        <input bind:value={otherLabel} maxlength="40" placeholder="Essuie-glaces, batterie…" />
      </label>
    {/if}
  </fieldset>
  <div class="form-row">
    <label>
      Date
      <input type="date" bind:value={performedOn} max={today()} required />
    </label>
    <label>
      Kilométrage
      <input bind:value={mileage} inputmode="numeric" placeholder="Facultatif" autocomplete="off" />
    </label>
  </div>
  {#if withDeadline}
    <div class="next stack">
      <Segmented
        label={nextDueLabel(kind)}
        options={[
          { value: 'auto', label: 'Automatique' },
          { value: 'precise', label: 'Date précise' },
        ]}
        bind:value={nextMode}
      />
      {#if nextMode === 'auto'}
        <p class="muted" aria-live="polite">
          {automatic ? `${nextDueLabel(kind)} le ${formatDay(automatic)}` : 'Choisissez la date de l\'opération'}
          <span class="hint">({kind === 'SERVICE' ? '+ 1 an' : '+ 2 ans'})</span>
        </p>
      {:else}
        <label>
          <span>Date <span class="visually-hidden">de la {nextDueLabel(kind).toLowerCase()}</span></span>
          <input type="date" bind:value={preciseNextDue} min={performedOn ? dayAfter(performedOn) : undefined} required />
        </label>
      {/if}
    </div>
  {/if}
  <div class="form-row">
    <label>
      Coût en €
      <input bind:value={cost} inputmode="decimal" placeholder="Facultatif" autocomplete="off" />
    </label>
    <label>
      Note
      <input bind:value={note} maxlength="140" placeholder="Garage, pièces…" />
    </label>
  </div>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>Ajouter au carnet</button>
</form>

<style>
  .kinds {
    border: none;
    padding: 0;
    margin: 0;
  }
  .kinds legend {
    font-size: 0.85rem;
    font-weight: 700;
    color: var(--color-text-muted);
    margin-bottom: 8px;
  }
  /* Même présentation que la précision « Autre » des dépenses */
  .precision {
    margin-top: 12px;
  }
  .precision input {
    border: 2px solid var(--color-primary);
  }
  .next {
    padding: 12px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
  }
  .next p {
    margin: 0;
    color: var(--color-on-primary-soft);
    font-weight: 700;
  }
  .hint {
    font-weight: 500;
  }
  .chips {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
</style>
