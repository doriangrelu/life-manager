<script lang="ts">
  import { Check } from '@lucide/svelte'
  import { tick, untrack } from 'svelte'
  import type { Project, ProjectColor, ProjectInput, ProjectStatus } from '../../api/types'
  import { centsToInput, parseAmountToCents } from '../../money'
  import { COLOR_NAMES, PROJECT_COLORS, STATUS_OPTIONS } from '../../projects'
  import Field from '../../ui/molecules/Field.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import InfoTip from '../../ui/atoms/InfoTip.svelte'
  import { allValid, check, focusFirstInvalid, maxLength, notBefore, optionalAmount, required } from '../../validation'

  // Création ou modification d'un projet (dans une modale) : nom, couleur, budget prévu, dates, état, option budget.
  let { project, onsave }: { project: Project | null; onsave: (input: ProjectInput) => Promise<void> } = $props()

  const initial = untrack(() => project)
  const COLORS = Object.keys(PROJECT_COLORS) as ProjectColor[]

  let name = $state(initial?.name ?? '')
  let color = $state<ProjectColor>(initial?.color ?? 'INDIGO')
  let planned = $state(initial?.plannedCents != null ? centsToInput(initial.plannedCents) : '')
  let startOn = $state(initial?.startOn ?? '')
  let targetOn = $state(initial?.targetOn ?? '')
  let status = $state<ProjectStatus>(initial?.status ?? 'ACTIVE')
  let countsInBudget = $state(initial?.countsInBudget ?? true)
  let description = $state(initial?.description ?? '')
  let busy = $state(false)
  let error = $state<string | null>(null)
  let revealed = $state(false)

  const errors = $derived({
    name: check(name, required('Donnez un nom au projet'), maxLength(60)),
    planned: check(planned, optionalAmount('3 000')),
    targetOn: check(targetOn, notBefore(startOn, 'La fin visée ne peut pas précéder le début')),
    description: check(description, maxLength(500)),
  })

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
        name: name.trim(),
        description: description.trim() || null,
        color,
        plannedCents: planned.trim() ? parseAmountToCents(planned) : null,
        startOn: startOn || null,
        targetOn: targetOn || null,
        status,
        countsInBudget,
      })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <Field label="Nom" error={errors.name} reveal={revealed}>
    {#snippet children(id)}
      <input {id} bind:value={name} maxlength="60" placeholder="Ex. : Salle de bain, Vacances d'été…" data-autofocus />
    {/snippet}
  </Field>

  <fieldset class="colors">
    <legend>Couleur</legend>
    <div class="swatches">
      {#each COLORS as option (option)}
        <button
          type="button"
          class="swatch"
          class:chosen={color === option}
          style:--swatch={PROJECT_COLORS[option]}
          aria-label={COLOR_NAMES[option]}
          aria-pressed={color === option}
          title={COLOR_NAMES[option]}
          onclick={() => (color = option)}
        >
          {#if color === option}<Check size={16} strokeWidth={3} />{/if}
        </button>
      {/each}
    </div>
  </fieldset>

  <Field
    label="Budget prévu (facultatif)"
    error={errors.planned}
    reveal={revealed}
    info="Ce que vous comptez dépenser en tout pour ce projet. La fiche compare les dépenses rattachées à ce montant."
  >
    {#snippet children(id)}
      <input {id} bind:value={planned} inputmode="decimal" placeholder="Ex. : 3 000" />
    {/snippet}
  </Field>

  <div class="form-row">
    <Field label="Début (facultatif)">
      {#snippet children(id)}
        <input {id} type="date" bind:value={startOn} />
      {/snippet}
    </Field>
    <Field label="Fin visée (facultative)" error={errors.targetOn} reveal={revealed}>
      {#snippet children(id)}
        <input {id} type="date" bind:value={targetOn} min={startOn || undefined} />
      {/snippet}
    </Field>
  </div>

  {#if initial}
    <Segmented label="État" options={STATUS_OPTIONS} bind:value={status} />
  {/if}

  <div class="budget-option">
    <label class="check">
      <input type="checkbox" bind:checked={countsInBudget} />
      Ses dépenses comptent dans le budget du mois
    </label>
    <InfoTip
      label="À quoi sert cette option"
      text="Décochée, les dépenses de ce projet sortent des totaux du mois, des catégories et du bilan, pour tous les mois (elles restent visibles, marquées « Budget à part »). Pratique pour un projet financé à part, comme des vacances payées avec une épargne."
    />
  </div>

  <Field label="Description (facultative)" error={errors.description} reveal={revealed}>
    {#snippet children(id)}
      <textarea {id} bind:value={description} rows="3" maxlength="500" placeholder="Ex. : refaire la douche et le carrelage"></textarea>
    {/snippet}
  </Field>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>{initial ? 'Enregistrer' : 'Créer le projet'}</button>
</form>

<style>
  .colors {
    border: none;
    padding: 0;
    margin: 0;
  }
  .colors legend {
    font-size: 0.85rem;
    font-weight: 700;
    color: var(--color-text-muted);
    margin-bottom: 8px;
  }
  .swatches {
    display: flex;
    flex-wrap: wrap;
    gap: 9px;
    padding: 4px;
  }
  .swatch {
    width: 32px;
    height: 32px;
    min-height: 32px;
    padding: 0;
    border-radius: 50%;
    background: var(--swatch);
    color: #fff;
    box-shadow: none;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .swatch.chosen {
    outline: 3px solid var(--swatch);
    outline-offset: 3px;
  }
  .budget-option {
    position: relative;
    display: flex;
    align-items: center;
    gap: 6px;
  }
  .check {
    display: flex;
    align-items: center;
    gap: 12px;
    min-height: 44px;
    font-size: 0.95rem;
    font-weight: 600;
    color: var(--color-text);
    cursor: pointer;
  }
  .check input {
    accent-color: var(--color-primary);
  }
  textarea {
    min-height: 80px;
    padding: 12px 14px;
    resize: vertical;
  }
</style>
