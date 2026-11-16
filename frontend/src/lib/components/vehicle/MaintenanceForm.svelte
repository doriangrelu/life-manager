<script lang="ts">
  import type { MaintenanceInput, MaintenanceKind } from '../../api/types'
  import { parseAmountToCents } from '../../money'
  import { today } from '../../month'
  import { MAINTENANCE_KINDS } from '../../vehicles'

  // Ajout rapide d'une opération au carnet : date et type pré-remplis, le reste facultatif.
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
    if (!input) {
      return
    }
    busy = true
    if (await onsave(input)) {
      mileage = ''
      cost = ''
      note = ''
    }
    busy = false
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

<form class="card stack" onsubmit={submit}>
  <h2>Ajouter au carnet</h2>
  <div class="row">
    <label>
      Date
      <input type="date" bind:value={performedOn} max={today()} required />
    </label>
    <label>
      Type
      <select bind:value={kind}>
        {#each MAINTENANCE_KINDS as option (option.kind)}
          <option value={option.kind}>{option.label}</option>
        {/each}
      </select>
    </label>
    <label>
      Kilométrage (facultatif)
      <input bind:value={mileage} inputmode="numeric" placeholder="45000" autocomplete="off" />
    </label>
    <label>
      Coût en € (facultatif)
      <input bind:value={cost} inputmode="decimal" placeholder="0,00" autocomplete="off" />
    </label>
  </div>
  <label>
    Note (facultatif)
    <input bind:value={note} maxlength="140" placeholder="Garage, pièces changées…" />
  </label>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button disabled={busy}>Ajouter</button>
</form>

<style>
  .row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  .row label {
    min-width: 0;
  }
</style>
