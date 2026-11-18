<script lang="ts">
  import { untrack } from 'svelte'
  import type { Vehicle, VehicleInput } from '../../api/types'

  // Véhicule à créer ou modifier (dans une modale) : nom, immatriculation et prochaines échéances.
  let { vehicle, onsave }: { vehicle: Vehicle | null; onsave: (input: VehicleInput) => Promise<void> } = $props()

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

<form class="stack" onsubmit={submit}>
  <div class="form-row">
    <label>
      Nom
      <input bind:value={name} maxlength="40" placeholder="Clio" required />
    </label>
    <label>
      Immatriculation
      <input bind:value={licensePlate} maxlength="12" placeholder="Facultatif" autocapitalize="characters" />
    </label>
  </div>
  <div class="form-row">
    <label>
      Prochain contrôle technique
      <input type="date" bind:value={technicalInspectionDue} />
    </label>
    <label>
      Prochaine révision
      <input type="date" bind:value={serviceDue} />
    </label>
  </div>
  <button class="large" disabled={busy || !name.trim()}>Enregistrer</button>
</form>
