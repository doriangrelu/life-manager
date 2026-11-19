<script lang="ts">
  // Choix exclusif parmi quelques options (payé par, pour qui…), présenté en boutons accolés.
  // hideLabel : titre lu par les lecteurs d'écran seulement (les options se suffisent à elles-mêmes).
  // info : explication du choix, affichée par une icône « i » à côté du titre.
  import InfoTip from '../atoms/InfoTip.svelte'

  let {
    label,
    options,
    value = $bindable(),
    hideLabel = false,
    info,
  }: {
    label: string
    options: { value: string; label: string }[]
    value: string
    hideLabel?: boolean
    info?: string
  } = $props()
</script>

<div class="segmented">
  {#if !hideLabel}
    <span class="caption">
      {label}
      {#if info}<InfoTip text={info} label="À quoi sert « {label} »" />{/if}
    </span>
  {/if}
  <div class="options" role="group" aria-label={label}>
    {#each options as option (option.value)}
      <button
        type="button"
        class:selected={value === option.value}
        aria-pressed={value === option.value}
        onclick={() => (value = option.value)}>{option.label}</button
      >
    {/each}
  </div>
</div>

<style>
  .segmented {
    display: grid;
    gap: 6px;
    min-width: 0;
  }
  .caption {
    /* La bulle de l'icône « i » se place sous cette ligne. */
    position: relative;
    display: flex;
    align-items: center;
    gap: 2px;
    font-size: 0.85rem;
    font-weight: 700;
    color: var(--color-text-muted);
  }
  .options {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    padding: 4px;
    border-radius: var(--radius-md);
    background: var(--color-surface-muted);
  }
  .options button {
    flex: 1 1 auto;
    min-height: 40px;
    padding: 0 10px;
    border-radius: 10px;
    background: transparent;
    color: var(--color-text-muted);
    font-weight: 600;
  }
  .options button.selected {
    background: var(--color-surface);
    color: var(--color-text);
    font-weight: 800;
    box-shadow: var(--shadow-sm);
  }
</style>
