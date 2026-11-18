<script lang="ts">
  import Modal from './Modal.svelte'

  // Confirmation avant une action définitive (suppression…), à la place de confirm() du navigateur.
  let {
    open = $bindable(false),
    title,
    message,
    confirmLabel = 'Supprimer',
    onconfirm,
  }: {
    open: boolean
    title: string
    message: string
    confirmLabel?: string
    onconfirm: () => void
  } = $props()

  function confirm() {
    open = false
    onconfirm()
  }
</script>

<Modal bind:open {title}>
  <p class="muted">{message}</p>
  {#snippet footer()}
    <button class="ghost" onclick={() => (open = false)}>Annuler</button>
    <button class="danger" onclick={confirm}>{confirmLabel}</button>
  {/snippet}
</Modal>
