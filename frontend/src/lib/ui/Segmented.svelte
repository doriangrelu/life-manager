<script lang="ts">
  // Choix exclusif parmi quelques options (payé par, pour qui…), présenté en boutons accolés.
  let {
    label,
    options,
    value = $bindable(),
  }: { label: string; options: { value: string; label: string }[]; value: string } = $props()
</script>

<div class="segmented">
  <span class="caption">{label}</span>
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
