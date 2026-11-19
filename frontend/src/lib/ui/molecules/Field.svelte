<script lang="ts">
  import type { Snippet } from 'svelte'
  import InfoTip from '../atoms/InfoTip.svelte'

  // Champ de formulaire avec contrôle en direct : libellé, icône « i » facultative, le champ lui-même,
  // puis le motif de l'erreur en rouge. L'erreur ne s'affiche qu'une fois le champ quitté (ou le formulaire
  // envoyé : `reveal`), puis suit la saisie : on n'est pas repris pendant qu'on tape la première lettre.
  // Le champ reçoit son `id` par le snippet : `{#snippet children(id)}<input {id} … />{/snippet}`.
  // Sans `label` (champ qui porte déjà le sien, comme le montant), seuls le rouge et le message sont ajoutés.
  let {
    label,
    error = null,
    info,
    reveal = false,
    children,
  }: {
    label?: string
    /** Motif de l'erreur, ou null si la valeur est valide. */
    error?: string | null
    /** Explication du champ, affichée par l'icône « i ». */
    info?: string
    /** Vrai après une tentative d'envoi : toutes les erreurs se montrent. */
    reveal?: boolean
    children: Snippet<[string]>
  } = $props()

  const id = $props.id()
  let touched = $state(false)
  let wrapper: HTMLDivElement | undefined = $state()

  const shown = $derived((touched || reveal) && error ? error : null)

  // Le champ est annoncé invalide aux lecteurs d'écran, avec son message.
  $effect(() => {
    const control = wrapper?.querySelector<HTMLElement>('input, select, textarea')
    if (control) {
      control.setAttribute('aria-invalid', String(shown !== null))
      if (shown) {
        control.setAttribute('aria-describedby', `${id}-error`)
      } else {
        control.removeAttribute('aria-describedby')
      }
    }
  })

  // Quitter le champ (et non passer à son icône « i ») déclenche le contrôle.
  function onfocusout(event: FocusEvent) {
    if (!wrapper?.contains(event.relatedTarget as Node | null)) {
      touched = true
    }
  }
</script>

<div class="field" class:invalid={shown !== null} bind:this={wrapper} {onfocusout}>
  {#if label}
    <span class="head">
      <label for={id}>{label}</label>
      {#if info}<InfoTip text={info} label="À quoi sert « {label} »" />{/if}
    </span>
  {/if}
  {@render children(id)}
  {#if shown}
    <span class="message" id="{id}-error" role="alert">{shown}</span>
  {/if}
</div>

<style>
  .field {
    display: grid;
    gap: 6px;
    min-width: 0;
  }
  /* La bulle de l'icône « i » se place sous cette ligne. */
  .head {
    position: relative;
    display: flex;
    align-items: center;
    gap: 2px;
  }
  .message {
    color: var(--color-danger);
    font-size: 0.82rem;
    font-weight: 600;
  }
  .invalid label {
    color: var(--color-danger);
  }
  .invalid :global(input),
  .invalid :global(select),
  .invalid :global(textarea) {
    border-color: var(--color-danger);
    box-shadow: 0 0 0 1px var(--color-danger);
  }
</style>
