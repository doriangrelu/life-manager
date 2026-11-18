<script lang="ts">
  import type { MaintenanceInput, MaintenanceKind } from '../../api/types'
  import { parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { MAINTENANCE_KINDS } from '../../vehicles'
  import Chip from '../../ui/Chip.svelte'

  // Nouvelle opération du carnet (dans une modale) : date et type pré-remplis, le reste facultatif.
  let { onsave }: { onsave: (input: MaintenanceInput) => Promise<boolean> } = $props()

  let performedOn = $state(today())
  let kind = $state<MaintenanceKind>('SERVICE')
  let mileage = $state('')
  let cost = $state('')
  let note = $state('')
  let error = $state<string | null>(null)
  let busy = $state(false)

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
    return { performedOn, kind, mileageKm, costCents, note: note.trim() || null }
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
  <button class="large" disabled={busy}>Ajouter au carnet</button>
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
  .chips {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
</style>
