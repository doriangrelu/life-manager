<script lang="ts">
  // Grand champ de montant en euros, clavier numérique sur mobile (la conversion en centimes se fait à l'envoi).
  let {
    value = $bindable(''),
    field = $bindable(),
    label = 'Montant',
  }: { value: string; field?: HTMLInputElement; label?: string } = $props()
</script>

<label class="amount-input">
  {label}
  <span class="row">
    <input bind:this={field} bind:value inputmode="decimal" placeholder="0,00" autocomplete="off" />
    <span class="currency" aria-hidden="true">€</span>
  </span>
</label>

<style>
  .amount-input {
    justify-items: center;
  }
  .row {
    display: flex;
    align-items: baseline;
    gap: 6px;
  }
  input {
    width: 200px;
    min-height: 60px;
    border: none;
    background: transparent;
    text-align: right;
    font-size: 2.6rem;
    font-weight: 800;
    letter-spacing: -0.02em;
    padding: 0;
  }
  .currency {
    font-size: 1.9rem;
    font-weight: 800;
    color: var(--color-text-muted);
  }
</style>
