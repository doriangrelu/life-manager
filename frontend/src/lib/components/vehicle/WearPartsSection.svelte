<script lang="ts">
  import { Pencil, RefreshCw } from '@lucide/svelte'
  import { correctWearPart, deleteWearPart, fitWearPart, listWearParts } from '../../api/vehicleApi'
  import type { Vehicle, WearPart, WearPartInput } from '../../api/types'
  import { formatDay } from '../../vehicles'
  import { freeKinds, km, splitWearParts, wearPartLabel, wearRatio, wearStatus } from '../../wearParts'
  import ConfirmDialog from '../../ui/molecules/ConfirmDialog.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import ProgressBar from '../../ui/atoms/ProgressBar.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import WearPartForm from './WearPartForm.svelte'

  // Pièces d'usure du véhicule : pièces montées (km parcourus, état), changement, correction et historique.
  // onmileage : une pose a pu relever le kilométrage du véhicule, la fiche le relit.
  let { householdId, vehicle, onmileage }: { householdId: string; vehicle: Vehicle; onmileage: () => void } =
    $props()

  type Dialog = { mode: 'add' } | { mode: 'change'; part: WearPart } | { mode: 'edit'; part: WearPart }

  let parts = $state<WearPart[]>([])
  let loaded = $state(false)
  let error = $state<string | null>(null)
  let dialog = $state<Dialog | null>(null)
  let dialogOpen = $state(false)
  let confirmingDelete = $state(false)
  const split = $derived(splitWearParts(parts))
  const available = $derived(freeKinds(parts))

  const STATUS_TEXT = { ok: null, soon: 'À prévoir', due: 'À changer' } as const
  const STATUS_COLOR = { ok: 'var(--color-success)', soon: 'var(--color-warning)', due: 'var(--color-danger)' }

  // Rechargé à l'ouverture et quand le kilométrage du véhicule change (carnet, pose).
  $effect(() => {
    void vehicle.mileageKm
    load()
  })

  async function load() {
    try {
      parts = await listWearParts(householdId, vehicle.id)
      error = null
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    } finally {
      loaded = true
    }
  }

  function open(next: Dialog) {
    dialog = next
    dialogOpen = true
  }

  // Un refus remonte au formulaire, qui l'affiche dans sa fenêtre.
  async function save(input: WearPartInput) {
    const current = dialog
    if (current?.mode === 'edit') {
      await correctWearPart(householdId, vehicle.id, current.part.id, input)
    } else {
      await fitWearPart(householdId, vehicle.id, input)
    }
    dialogOpen = false
    if (input.fittedKm > (vehicle.mileageKm ?? -1)) {
      onmileage() // le kilométrage change : la liste se recharge avec lui
    } else {
      await load()
    }
  }

  async function remove() {
    if (dialog?.mode !== 'edit') {
      return
    }
    const id = dialog.part.id
    dialogOpen = false
    try {
      await deleteWearPart(householdId, vehicle.id, id)
      parts = parts.filter((p) => p.id !== id)
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  const title = $derived(
    dialog?.mode === 'add'
      ? 'Suivre une pièce'
      : dialog?.mode === 'change'
        ? `Changer : ${wearPartLabel(dialog.part.kind).toLowerCase()}`
        : dialog
          ? wearPartLabel(dialog.part.kind)
          : '',
  )
</script>

<section class="stack">
  <div class="section-title">
    <h2>Pièces d'usure</h2>
    {#if available.length > 0}
      <button class="soft" onclick={() => open({ mode: 'add' })}>+ Pièce</button>
    {/if}
  </div>
  {#if vehicle.mileageKm !== null || vehicle.tireSize}
    <p class="facts muted">
      {#if vehicle.mileageKm !== null}<span>Compteur : <strong>{km(vehicle.mileageKm)}</strong></span>{/if}
      {#if vehicle.tireSize}<span>Pneus : <strong>{vehicle.tireSize}</strong></span>{/if}
    </p>
  {/if}
  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}
  {#if !loaded}
    <Skeleton variant="cards" count={1} />
  {:else if split.mounted.length === 0}
    <p class="muted">Suivez vos pneus, plaquettes ou disques pour voir les kilomètres parcourus depuis leur pose.</p>
  {:else}
    <ul class="parts">
      {#each split.mounted as part (part.id)}
        {@const status = wearStatus(part)}
        {@const ratio = wearRatio(part)}
        <li class="part">
          <div class="head">
            <strong>{wearPartLabel(part.kind)}</strong>
            {#if status && STATUS_TEXT[status]}
              <span class="status {status}">{STATUS_TEXT[status]}</span>
            {/if}
          </div>
          <span class="muted">
            {part.label ? `${part.label} · ` : ''}posée le {formatDay(part.fittedOn)} à {km(part.fittedKm)}
          </span>
          <span class="done">
            {part.kmDone !== null ? `${km(part.kmDone)} parcourus` : 'Kilométrage actuel inconnu'}
            {#if part.replaceAfterKm !== null}<span class="muted"> sur {km(part.replaceAfterKm)}</span>{/if}
          </span>
          {#if ratio !== null && status}
            <ProgressBar {ratio} color={STATUS_COLOR[status]} />
          {/if}
          <div class="actions">
            <button class="soft small" onclick={() => open({ mode: 'change', part })}>
              <RefreshCw size={15} /> Changer
            </button>
            <button class="ghost icon" aria-label="Corriger {wearPartLabel(part.kind)}" onclick={() => open({ mode: 'edit', part })}>
              <Pencil size={16} />
            </button>
          </div>
        </li>
      {/each}
    </ul>
  {/if}
  {#if split.history.length > 0}
    <details class="history">
      <summary>Historique ({split.history.length})</summary>
      <ul>
        {#each split.history as part (part.id)}
          <li>
            <span>
              <strong>{wearPartLabel(part.kind)}</strong>{part.label ? ` · ${part.label}` : ''}
            </span>
            <span class="muted">
              {formatDay(part.fittedOn)} → {formatDay(part.removedOn ?? '')} · {km(part.kmDone ?? 0)}
            </span>
          </li>
        {/each}
      </ul>
    </details>
  {/if}
</section>

<Modal bind:open={dialogOpen} {title}>
  {#if dialog}
    {#key dialog}
      <WearPartForm
        kinds={available}
        part={dialog.mode === 'edit' ? dialog.part : null}
        replacing={dialog.mode === 'change' ? dialog.part : null}
        currentKm={vehicle.mileageKm}
        onsave={save}
      />
    {/key}
    {#if dialog.mode === 'edit'}
      <button class="link danger-link" onclick={() => (confirmingDelete = true)}>Supprimer cette pièce</button>
    {/if}
  {/if}
</Modal>

<ConfirmDialog
  bind:open={confirmingDelete}
  title="Supprimer la pièce ?"
  message="Elle disparaît du suivi (à utiliser pour une erreur de saisie ; pour un remplacement, utilisez « Changer »)."
  onconfirm={remove}
/>

<style>
  .facts {
    display: flex;
    flex-wrap: wrap;
    gap: 4px 16px;
    margin: 0;
  }
  .facts strong {
    color: var(--color-text);
  }
  .parts {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .part {
    display: grid;
    gap: 6px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
    font-size: 0.9rem;
  }
  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  }
  .head strong {
    font-size: 1rem;
  }
  .done {
    font-weight: 700;
  }
  .status {
    padding: 2px 10px;
    border-radius: 999px;
    font-size: 0.78rem;
    font-weight: 800;
    white-space: nowrap;
  }
  .status.soon {
    background: var(--color-warning-soft);
    color: var(--color-on-warning-soft);
  }
  .status.due {
    background: var(--color-danger-soft);
    color: var(--color-danger);
  }
  .actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 2px;
  }
  .small {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    min-height: 36px;
    padding: 0 12px;
    font-size: 0.85rem;
  }
  .history {
    padding: 10px 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
    font-size: 0.9rem;
  }
  .history summary {
    cursor: pointer;
    font-weight: 700;
    min-height: 32px;
    display: flex;
    align-items: center;
  }
  .history ul {
    list-style: none;
    margin: 8px 0 0;
    padding: 0;
    display: grid;
    gap: 8px;
  }
  .history li {
    display: grid;
    gap: 2px;
  }
  .danger-link {
    justify-self: center;
    margin-top: 12px;
    color: var(--color-danger);
  }
</style>
