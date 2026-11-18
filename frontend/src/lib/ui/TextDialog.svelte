<script lang="ts">
  import { untrack } from 'svelte'
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

  // À chaque ouverture, le champ repart de la valeur proposée.
  $effect(() => {
    if (open) {
      value = untrack(() => initial)
    }
  })

  function submit(event: SubmitEvent) {
    event.preventDefault()
    const text = value.trim()
    if (text) {
      open = false
      onsubmit(text)
    }
  }
</script>

<Modal bind:open {title}>
  <form class="stack" onsubmit={submit}>
    <label>
      {label}
      <input bind:value {maxlength} required />
    </label>
    <button class="large" disabled={!value.trim()}>{submitLabel}</button>
  </form>
</Modal>
