<script lang="ts">
  import { FileText, Image, Paperclip, X } from '@lucide/svelte'
  import { attachmentContent, listAttachments, removeAttachment, uploadAttachment } from '../../api/vehicleApi'
  import type { Attachment, MaintenanceEntry } from '../../api/types'
  import { ACCEPTED_TYPES, formatSize, rejectionOf, remainingSlots } from '../../attachments'
  import { formatDay, kindLabel } from '../../vehicles'
  import ConfirmDialog from '../../ui/ConfirmDialog.svelte'
  import Modal from '../../ui/Modal.svelte'

  // Pièces jointes d'une opération du carnet : factures et photos, ajoutées, ouvertes ou supprimées.
  let {
    open = $bindable(false),
    householdId,
    vehicleId,
    entry,
    onchanged,
  }: {
    open: boolean
    householdId: string
    vehicleId: string
    entry: MaintenanceEntry
    onchanged: () => void
  } = $props()

  /** Un envoi en cours ou échoué, affiché sous la liste. */
  interface Upload {
    key: number
    name: string
    progress: number
    error: string | null
  }

  let attachments = $state<Attachment[]>([])
  let uploads = $state<Upload[]>([])
  let error = $state<string | null>(null)
  let removing = $state<Attachment | null>(null)
  let confirmingRemoval = $state(false)
  let picker: HTMLInputElement | undefined = $state()
  let nextKey = 0
  const slots = $derived(remainingSlots(attachments.length + uploads.filter((u) => !u.error).length))

  $effect(() => {
    if (open) {
      uploads = []
      load()
    }
  })

  async function load() {
    error = null
    try {
      attachments = await listAttachments(householdId, vehicleId, entry.id)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Un fichier par requête, les uns après les autres : la progression reste lisible sur mobile.
  async function picked() {
    const files = Array.from(picker?.files ?? []).slice(0, slots)
    if (picker) {
      picker.value = ''
    }
    for (const file of files) {
      await send(file)
    }
    onchanged()
  }

  async function send(file: File) {
    const upload: Upload = { key: nextKey++, name: file.name, progress: 0, error: rejectionOf(file) }
    uploads = [...uploads, upload]
    if (upload.error) {
      return
    }
    const update = (changes: Partial<Upload>) =>
      (uploads = uploads.map((u) => (u.key === upload.key ? { ...u, ...changes } : u)))
    try {
      const added = await uploadAttachment(householdId, vehicleId, entry.id, file, (progress) => update({ progress }))
      attachments = [...attachments, added]
      uploads = uploads.filter((u) => u.key !== upload.key)
    } catch (e) {
      update({ error: e instanceof Error ? e.message : String(e) })
    }
  }

  // Le fichier s'ouvre dans un nouvel onglet ; l'onglet est ouvert tout de suite (sinon le navigateur le bloque).
  async function show(attachment: Attachment) {
    const tab = window.open('', '_blank')
    try {
      const blob = await attachmentContent(householdId, vehicleId, entry.id, attachment.id)
      const url = URL.createObjectURL(blob)
      if (tab) {
        tab.location.href = url
      } else {
        const link = Object.assign(document.createElement('a'), { href: url, download: attachment.fileName })
        link.click()
      }
      setTimeout(() => URL.revokeObjectURL(url), 60_000)
    } catch (e) {
      tab?.close()
      error = e instanceof Error ? e.message : String(e)
    }
  }

  function askRemove(attachment: Attachment) {
    removing = attachment
    confirmingRemoval = true
  }

  async function remove() {
    const attachment = removing
    if (!attachment) {
      return
    }
    try {
      await removeAttachment(householdId, vehicleId, entry.id, attachment.id)
      attachments = attachments.filter((a) => a.id !== attachment.id)
      onchanged()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }
</script>

<Modal bind:open title="Pièces jointes">
  <p class="muted">{kindLabel(entry.kind)} du {formatDay(entry.performedOn)}</p>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  {#if attachments.length === 0 && uploads.length === 0}
    <p class="muted">Aucun fichier : ajoutez une facture ou une photo (PDF, JPEG, PNG, WebP, 10 Mo au plus).</p>
  {/if}

  {#if attachments.length > 0}
    <ul class="files" aria-label="Fichiers joints">
      {#each attachments as attachment (attachment.id)}
        <li>
          <button class="open" onclick={() => show(attachment)}>
            <span class="bubble" aria-hidden="true">
              {#if attachment.mediaType === 'application/pdf'}<FileText size={20} />{:else}<Image size={20} />{/if}
            </span>
            <span class="text">
              <span class="name">{attachment.fileName}</span>
              <span class="muted size">{formatSize(attachment.sizeBytes)}</span>
            </span>
          </button>
          <button class="icon ghost" aria-label="Supprimer {attachment.fileName}" onclick={() => askRemove(attachment)}>
            <X size={18} />
          </button>
        </li>
      {/each}
    </ul>
  {/if}

  {#if uploads.length > 0}
    <ul class="files" aria-label="Envois">
      {#each uploads as upload (upload.key)}
        <li class="upload">
          <span class="text">
            <span class="name">{upload.name}</span>
            {#if upload.error}
              <span class="error">{upload.error}</span>
            {:else}
              <progress value={upload.progress} max="1" aria-label="Envoi de {upload.name}"></progress>
            {/if}
          </span>
        </li>
      {/each}
    </ul>
  {/if}

  <input
    bind:this={picker}
    class="visually-hidden"
    type="file"
    multiple
    accept={ACCEPTED_TYPES}
    aria-label="Choisir des fichiers"
    onchange={picked}
  />

  {#snippet footer()}
    <button class="large" disabled={slots === 0} onclick={() => picker?.click()}>
      <Paperclip size={18} />
      {slots === 0 ? '10 fichiers au plus' : 'Ajouter des fichiers'}
    </button>
  {/snippet}
</Modal>

<ConfirmDialog
  bind:open={confirmingRemoval}
  title="Supprimer « {removing?.fileName} » ?"
  message="Le fichier sera définitivement supprimé."
  onconfirm={remove}
/>

<style>
  .files {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 6px;
  }
  .files li {
    display: flex;
    align-items: center;
    gap: 4px;
    padding-right: 4px;
    border-radius: var(--radius-md);
    background: var(--color-surface-muted);
  }
  .files li.upload {
    padding: 10px 14px;
  }
  .open {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 10px;
    background: transparent;
    box-shadow: none;
    color: var(--color-text);
    text-align: left;
    font-weight: 600;
  }
  .bubble {
    width: 38px;
    height: 38px;
    border-radius: var(--radius-md);
    background: var(--color-primary-soft);
    color: var(--color-on-primary-soft);
    display: grid;
    place-items: center;
    flex-shrink: 0;
  }
  .text {
    flex: 1;
    min-width: 0;
    display: grid;
    gap: 4px;
  }
  .name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .size {
    font-size: 0.85rem;
    font-weight: 500;
  }
  progress {
    width: 100%;
    height: 8px;
    accent-color: var(--color-primary);
  }
  .files li > :global(.icon) {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
  }
</style>
