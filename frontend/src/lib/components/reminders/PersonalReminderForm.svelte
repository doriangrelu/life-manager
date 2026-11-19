<script lang="ts">
  import { tick, untrack } from 'svelte'
  import type { NoticePeriod, PersonalReminder, PersonalReminderInput, Recurrence } from '../../api/types'
  import { today } from '../../month'
  import { NOTICES, RECURRENCES } from '../../personalReminders'
  import Field from '../../ui/molecules/Field.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import { allValid, check, focusFirstInvalid, maxLength, required } from '../../validation'

  // Saisie ou modification d'un rappel (dans une modale) : titre, date, récurrence, délai, note.
  let {
    reminder,
    onsave,
  }: { reminder: PersonalReminder | null; onsave: (input: PersonalReminderInput) => Promise<void> } = $props()

  const initial = untrack(() => reminder)
  let title = $state(initial?.title ?? '')
  let dueOn = $state(initial?.dueOn ?? today())
  let recurrence = $state<Recurrence>(initial?.recurrence ?? 'NONE')
  let notice = $state<NoticePeriod>(initial?.notice ?? 'ONE_WEEK')
  let note = $state(initial?.note ?? '')
  let busy = $state(false)
  let error = $state<string | null>(null)
  let revealed = $state(false)

  const errors = $derived({
    title: check(title, required('Donnez un titre au rappel'), maxLength(60)),
    dueOn: check(dueOn, required('Choisissez une date')),
    note: check(note, maxLength(280)),
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
      await onsave({ title: title.trim(), dueOn, recurrence, notice, note: note.trim() || null })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <Field label="Titre" error={errors.title} reveal={revealed}>
    {#snippet children(id)}
      <input {id} bind:value={title} maxlength="60" placeholder="Ex. : Ramonage, Déclarer les impôts…" data-autofocus />
    {/snippet}
  </Field>
  <Field
    label={recurrence === 'NONE' ? 'Date' : 'Prochaine date'}
    error={errors.dueOn}
    reveal={revealed}
  >
    {#snippet children(id)}
      <input {id} type="date" bind:value={dueOn} />
    {/snippet}
  </Field>
  <Segmented
    label="Revient"
    options={RECURRENCES}
    bind:value={recurrence}
    info="Un rappel qui revient passe tout seul à sa date suivante quand vous touchez « C'est fait »."
  />
  <Segmented
    label="Prévenir"
    options={NOTICES}
    bind:value={notice}
    info="À partir de quand le rappel apparaît dans le bandeau et dans les notifications de l'appareil, avant sa date."
  />
  <Field label="Note (facultative)" error={errors.note} reveal={revealed}>
    {#snippet children(id)}
      <textarea {id} bind:value={note} rows="3" maxlength="280" placeholder="Ex. : appeler M. Martin, numéro de contrat…"></textarea>
    {/snippet}
  </Field>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>{initial ? 'Enregistrer' : 'Ajouter le rappel'}</button>
</form>

<style>
  textarea {
    min-height: 88px;
    padding: 12px 14px;
    resize: vertical;
  }
</style>
