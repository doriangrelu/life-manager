<script lang="ts">
  import { untrack } from 'svelte'
  import type { Vehicle, VehicleInput } from '../../api/types'

  // Fiche d'un véhicule à créer ou modifier : nom, immatriculation et prochaines échéances.
  let {
    vehicle,
    onsave,
    oncancel,
  }: { vehicle: Vehicle | null; onsave: (input: VehicleInput) => Promise<void>; oncancel: () => void } = $props()

  const initial = untrack(() => vehicle)

  let name = $state(initial?.name ?? '')
  let licensePlate = $state(initial?.licensePlate ?? '')
  let technicalInspectionDue = $state(initial?.technicalInspectionDue ?? '')
  let serviceDue = $state(initial?.serviceDue ?? '')
  let busy = $state(false)

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    busy = true
    try {
      await onsave({
        name: name.trim(),
        licensePlate: licensePlate.trim() || null,
        technicalInspectionDue: technicalInspectionDue || null,
        serviceDue: serviceDue || null,
      })
    } finally {
      busy = false
    }
  }
</script>

<form class="card stack" onsubmit={submit}>
  <h2>{initial ? 'Modifier le véhicule' : 'Nouveau véhicule'}</h2>
  <div class="row">
    <label>
      Nom
      <input bind:value={name} maxlength="40" placeholder="Clio" required />
    </label>
    <label>
      Immatriculation (facultatif)
      <input bind:value={licensePlate} maxlength="12" placeholder="AB-123-CD" autocapitalize="characters" />
    </label>
  </div>
  <div class="row">
    <label>
      Prochain contrôle technique
      <input type="date" bind:value={technicalInspectionDue} />
    </label>
    <label>
      Prochaine révision
      <input type="date" bind:value={serviceDue} />
    </label>
  </div>
  <div class="actions">
    <button disabled={busy || !name.trim()}>Enregistrer</button>
    <button type="button" class="link" onclick={oncancel}>Annuler</button>
  </div>
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
  .row input {
    width: 100%;
  }
  @media (max-width: 480px) {
    .row {
      grid-template-columns: 1fr;
    }
  }
  .actions {
    display: flex;
    align-items: center;
    gap: 16px;
  }
</style>
