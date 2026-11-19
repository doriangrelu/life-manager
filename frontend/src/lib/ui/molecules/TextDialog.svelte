<script lang="ts">
  import { untrack } from 'svelte'
  import { check, required } from '../../validation'
  import Field from './Field.svelte'
  import Modal from './Modal.svelte'

  // Saisie d'un texte court dans une modale (renommer…), à la place de prompt() du navigateur.
  let {
    open = $bindable(false),
    title,
    label,
    initial = '',
    maxlength,
    submitLabel = 'Enregistrer',
    onsubmit,
  }: {
    open: boolean
    title: string
    label: string
    initial?: string
    maxlength: number
    submitLabel?: string
    onsubmit: (value: string) => void
  } = $props()

  let value = $state(untrack(() => initial))
  let revealed = $state(false)
  const valueError = $derived(check(value, required('Ce champ ne peut pas rester vide')))

  // À chaque ouverture, le champ repart de la valeur proposée.
  $effect(() => {
    if (open) {
      value = untrack(() => initial)
      revealed = false
    }
  })

  function submit(event: SubmitEvent) {
    event.preventDefault()
    const text = value.trim()
    revealed = true
    if (text) {
      open = false
      onsubmit(text)
    }
  }
</script>

<Modal bind:open {title}>
  <form class="stack" onsubmit={submit}>
    <Field {label} error={valueError} reveal={revealed}>
      {#snippet children(id)}
        <input {id} bind:value {maxlength} data-autofocus />
      {/snippet}
    </Field>
    <button class="large">{submitLabel}</button>
  </form>
</Modal>
