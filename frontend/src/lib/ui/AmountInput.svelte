<script lang="ts">
  // Grand champ de montant en euros, clavier numérique sur mobile (la conversion en centimes se fait à l'envoi).
  // Le champ prend la largeur de son contenu : montant et « € » restent centrés ensemble.
  let {
    value = $bindable(''),
    field = $bindable(),
    label = 'Montant',
  }: { value: string; field?: HTMLInputElement; label?: string } = $props()

  // Largeur en caractères : celle du montant tapé, ou du « 0,00 » affiché quand il est vide.
  const width = $derived(Math.max(value.length, 4) + 0.5)
</script>

<label class="amount-input">
  <span class="caption">{label}</span>
  <span class="row">
    <input
      bind:this={field}
      bind:value
      inputmode="decimal"
      data-autofocus
      placeholder="0,00"
      autocomplete="off"
      style:width="{width}ch"
    />
    <span class="currency" aria-hidden="true">€</span>
  </span>
</label>

<style>
  .amount-input {
    justify-items: center;
    text-align: center;
  }
  .row {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 6px;
    max-width: 100%;
    padding: 4px 16px;
    border-radius: var(--radius-lg);
    transition: background-color 0.15s ease;
  }
  .row:focus-within {
    background: var(--color-surface-muted);
  }
  input {
    min-width: 4ch;
    max-width: 10ch;
    min-height: 60px;
    border: none;
    background: transparent;
    text-align: center;
    font-size: 2.6rem;
    font-weight: 800;
    font-variant-numeric: tabular-nums;
    letter-spacing: -0.02em;
    padding: 0;
  }
  /* Le fond de la ligne montre déjà le focus. */
  input:focus-visible {
    outline: none;
  }
  .currency {
    font-size: 1.9rem;
    font-weight: 800;
    color: var(--color-text-muted);
  }
</style>
