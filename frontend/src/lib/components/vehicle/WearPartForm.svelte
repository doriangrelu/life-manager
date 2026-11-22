<script lang="ts">
  import { tick, untrack } from 'svelte'
  import type { WearPart, WearPartInput, WearPartKind } from '../../api/types'
  import { today } from '../../month'
  import { WEAR_PART_KINDS, wearPartLabel } from '../../wearParts'
  import Field from '../../ui/molecules/Field.svelte'
  import { allValid, check, focusFirstInvalid, notAfter, optionalInteger, required } from '../../validation'

  // Pose d'une pièce d'usure (dans une modale) : nouvelle pièce suivie, remplacement, ou correction.
  // kinds : types proposés (ajout) ; part : pièce corrigée ; replacing : pièce remplacée (même type).
  let {
    kinds = [],
    part = null,
    replacing = null,
    currentKm = null,
    onsave,
  }: {
    kinds?: WearPartKind[]
    part?: WearPart | null
    replacing?: WearPart | null
    currentKm?: number | null
    onsave: (input: WearPartInput) => Promise<void>
  } = $props()

  const initial = untrack(() => ({ part, replacing, kinds, currentKm }))
  // Remplacement : date du jour et kilométrage actuel ; l'intervalle et la marque de l'ancienne pièce sont repris.
  const source = initial.part ?? initial.replacing
  let kind = $state<WearPartKind>(source?.kind ?? initial.kinds[0] ?? 'TIRES_FRONT')
  let fittedOn = $state(initial.part?.fittedOn ?? today())
  let fittedKm = $state(
    initial.part ? String(initial.part.fittedKm) : initial.currentKm !== null ? String(initial.currentKm) : '',
  )
  let label = $state(source?.label ?? '')
  let every = $state(source?.replaceAfterKm != null ? String(source.replaceAfterKm) : '')
  let busy = $state(false)
  let revealed = $state(false)
  let error = $state<string | null>(null)
  const hint = $derived(WEAR_PART_KINDS.find((k) => k.kind === kind)?.hint)
  const errors = $derived({
    fittedOn: check(fittedOn, required('Choisissez une date'), notAfter(today(), 'La date ne peut pas être dans le futur')),
    fittedKm: check(fittedKm, required('Indiquez le kilométrage à la pose'), optionalInteger('54300', 2_000_000)),
    every: check(every, optionalInteger('30000', 500_000)),
  })

  const toNumber = (text: string) => Number(text.replace(/\s/g, ''))

  async function submit(event: SubmitEvent) {
    event.preventDefault()
    if (!allValid(errors)) {
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
        kind,
        label: label.trim() || null,
        fittedOn,
        fittedKm: toNumber(fittedKm),
        replaceAfterKm: every.trim() && toNumber(every) > 0 ? toNumber(every) : null,
      })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  {#if !initial.part && !initial.replacing}
    <label>
      Pièce
      <select bind:value={kind}>
        {#each initial.kinds as option (option)}
          <option value={option}>{wearPartLabel(option)}</option>
        {/each}
      </select>
    </label>
    {#if hint}<p class="muted hint">{hint}</p>{/if}
  {/if}
  <div class="form-row">
    <Field label="Posée le" error={errors.fittedOn} reveal={revealed}>
      {#snippet children(id)}
        <input {id} type="date" bind:value={fittedOn} max={today()} />
      {/snippet}
    </Field>
    <Field label="Kilométrage à la pose" error={errors.fittedKm} reveal={revealed}>
      {#snippet children(id)}
        <input {id} bind:value={fittedKm} inputmode="numeric" placeholder="54300" autocomplete="off" />
      {/snippet}
    </Field>
  </div>
  <div class="form-row">
    <label>
      Marque ou modèle
      <input bind:value={label} maxlength="60" placeholder="Facultatif" autocomplete="off" />
    </label>
    <Field
      label="À changer tous les (km)"
      error={errors.every}
      reveal={revealed}
      info="Facultatif. La pièce est signalée « à prévoir » à 90 % de cette distance, puis « à changer »."
    >
      {#snippet children(id)}
        <input {id} bind:value={every} inputmode="numeric" placeholder="Facultatif" autocomplete="off" />
      {/snippet}
    </Field>
  </div>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>
    {initial.part ? 'Enregistrer' : initial.replacing ? 'Monter la nouvelle pièce' : 'Suivre cette pièce'}
  </button>
</form>

<style>
  .hint {
    margin: -8px 0 0;
    font-size: 0.85rem;
  }
</style>
