<script lang="ts">
  import { tick, untrack } from 'svelte'
  import type { Member, ProjectTask, TaskInput } from '../../api/types'
  import Field from '../../ui/molecules/Field.svelte'
  import Segmented from '../../ui/molecules/Segmented.svelte'
  import { allValid, check, focusFirstInvalid, maxLength, required } from '../../validation'

  // Modification d'une tâche (dans une modale) : titre, qui s'en charge, date.
  let {
    task,
    members,
    onsave,
  }: { task: ProjectTask; members: Member[]; onsave: (input: TaskInput) => Promise<void> } = $props()

  const initial = untrack(() => task)
  let title = $state(initial.title)
  let assignee = $state(initial.assigneeId ?? '')
  let dueOn = $state(initial.dueOn ?? '')
  let busy = $state(false)
  let error = $state<string | null>(null)
  let revealed = $state(false)

  const assignees = $derived([{ value: '', label: 'Personne' }, ...members.map((m) => ({ value: m.memberId, label: m.displayName }))])
  const errors = $derived({ title: check(title, required('Donnez un titre à la tâche'), maxLength(120)) })

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
      await onsave({ title: title.trim(), assigneeId: assignee || null, dueOn: dueOn || null })
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      busy = false
    }
  }
</script>

<form class="stack" onsubmit={submit}>
  <Field label="Tâche" error={errors.title} reveal={revealed}>
    {#snippet children(id)}
      <input {id} bind:value={title} maxlength="120" data-autofocus />
    {/snippet}
  </Field>
  <Segmented label="Qui s'en charge" options={assignees} bind:value={assignee} />
  <Field
    label="Date (facultative)"
    info="Une tâche datée apparaît dans les rappels du foyer 7 jours avant, jusqu'à ce qu'elle soit cochée."
  >
    {#snippet children(id)}
      <input {id} type="date" bind:value={dueOn} />
    {/snippet}
  </Field>
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  <button class="large" disabled={busy} aria-busy={busy}>Enregistrer</button>
</form>
