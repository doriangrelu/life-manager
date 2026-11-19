<script lang="ts">
  import { tick, untrack } from 'svelte'
  import type { Vehicle, VehicleInput } from '../../api/types'
  import Field from '../../ui/molecules/Field.svelte'
  import { check, focusFirstInvalid, required } from '../../validation'

  // Véhicule à créer ou modifier (dans une modale) : nom, immatriculation et prochaines échéances.
  let { vehicle, onsave }: { vehicle: Vehicle | null; onsave: (input: VehicleInput) => Promise<void> } = $props()

  const initial = untrack(() => vehicle)

  let name = $state(initial?.name ?? '')
  let licensePlate = $state(initial?.licensePlate ?? '')
  let technicalInspectionDue = $state(initial?.technicalInspectionDue ?? '')
  let serviceDue = $state(initial?.serviceDue ?? '')
  let busy = $state(false)
  let revealed = $state(false)
  let error = $state<string | null>(null)
  const nameError = $derived(check(name, required('Donnez un nom au véhicule')))

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    if (nameError) {
      const form = event.currentTarget as HTMLFormElement
      revealed = true
      await tick()
      focusFirstInvalid(form)
      return
    }
    busy = true
    error = null
    try {
      await onsave({
        name: name.trim(),
        licensePlate: licensePlate.trim().toUpperCase() || null,
        technicalInspectionDue: technicalInspectionDue || null,
        serviceDue: serviceDue || null,
      })
    } catch (e) {
      // Refus du serveur : montré dans la fenêtre (l'écran derrière est caché).
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <div class="form-row">
    <Field label="Nom" error={nameError} reveal={revealed}>
      {#snippet children(id)}
        <input {id} bind:value={name} maxlength="40" placeholder="Ex. : Clio" data-autofocus />
      {/snippet}
    </Field>
    <label>
      Immatriculation
      <input class="plate" bind:value={licensePlate} maxlength="12" placeholder="Facultatif" autocapitalize="characters" />
    </label>
  </div>
  <div class="form-row">
    <Field
      label="Prochain contrôle technique"
      info="Facultatif. Un rappel apparaît à l'approche de cette date. Elle se met à jour toute seule quand vous notez un contrôle technique dans le carnet."
    >
      {#snippet children(id)}
        <input {id} type="date" bind:value={technicalInspectionDue} />
      {/snippet}
    </Field>
    <Field
      label="Prochaine révision"
      info="Facultatif. Un rappel apparaît à l'approche de cette date. Elle se met à jour toute seule quand vous notez une révision dans le carnet."
    >
      {#snippet children(id)}
        <input {id} type="date" bind:value={serviceDue} />
      {/snippet}
    </Field>
  </div>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>Enregistrer</button>
</form>

<style>
  /* Plaque toujours affichée en majuscules pendant la saisie. */
  .plate:not(:placeholder-shown) {
    text-transform: uppercase;
    letter-spacing: 0.04em;
  }
</style>
