<script lang="ts">
  import { X } from '@lucide/svelte'
  import type { Snippet } from 'svelte'

  // Fenêtre modale : panneau qui monte du bas sur mobile, fenêtre centrée sur grand écran.
  // Repose sur <dialog> du navigateur : le focus reste dedans, Échap ferme, et le focus revient à l'ouvreur.
  // Le contenu n'existe que pendant l'ouverture : un formulaire repart de zéro à chaque fois.
  let {
    open = $bindable(false),
    title,
    onclose,
    children,
    footer,
  }: {
    open: boolean
    title: string
    onclose?: () => void
    children: Snippet
    footer?: Snippet
  } = $props()

  const titleId = $props.id()
  let dialog: HTMLDialogElement | undefined = $state()

  $effect(() => {
    if (open && dialog && !dialog.open) {
      dialog.showModal()
      // Champ principal du formulaire (marqué data-autofocus) prêt à la saisie ; sinon le panneau lui-même,
      // pour ne pas ouvrir la fenêtre avec un cadre de focus sur le bouton Fermer.
      const target = dialog.querySelector<HTMLElement>('[data-autofocus]') ?? dialog.querySelector<HTMLElement>('.panel')
      target?.focus()
    } else if (!open && dialog?.open) {
      dialog.close()
    }
  })

  // Fermeture par Échap, par le bouton ou par le code : un seul point de sortie.
  function closed() {
    open = false
    onclose?.()
  }

  // Un clic sur le fond assombri (hors du panneau) ferme la fenêtre.
  function clickOutside(event: MouseEvent) {
    if (event.target === dialog) {
      open = false
    }
  }
</script>

<dialog bind:this={dialog} class="modal" aria-labelledby={titleId} onclose={closed} onclick={clickOutside}>
  {#if open}
    <div class="panel" tabindex="-1">
      <div class="handle" aria-hidden="true"></div>
      <header>
        <h2 id={titleId}>{title}</h2>
        <button class="icon ghost" aria-label="Fermer" onclick={() => (open = false)}>
          <X size={18} strokeWidth={2.4} />
        </button>
      </header>
      <div class="body">
        {@render children()}
      </div>
      {#if footer}
        <footer>{@render footer()}</footer>
      {/if}
    </div>
  {/if}
</dialog>

<style>
  .modal {
    border: none;
    padding: 0;
    background: var(--color-surface);
    color: var(--color-text);
    /* Mobile : collé en bas, toute la largeur. */
    margin: auto 0 0;
    width: 100%;
    max-width: 100%;
    max-height: 92dvh;
    border-radius: 28px 28px 0 0;
  }
  .modal::backdrop {
    background: var(--color-backdrop);
  }
  .panel {
    display: flex;
    flex-direction: column;
    max-height: 92dvh;
    padding: 10px 20px calc(20px + env(safe-area-inset-bottom));
    gap: 16px;
  }
  .panel:focus {
    outline: none;
  }
  .handle {
    align-self: center;
    width: 44px;
    height: 5px;
    border-radius: 3px;
    background: var(--color-border);
  }
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }
  header h2 {
    font-size: 1.25rem;
  }
  header button {
    background: var(--color-surface-muted);
    box-shadow: none;
  }
  .body {
    overflow-y: auto;
    display: grid;
    gap: 16px;
  }
  footer {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  footer :global(button) {
    flex: 1 1 140px;
  }

  @media (min-width: 900px) {
    .modal {
      margin: auto;
      width: min(520px, 92vw);
      border-radius: var(--radius-xl);
    }
    .handle {
      display: none;
    }
    .panel {
      padding: 20px 24px 24px;
      max-height: 86dvh;
    }
  }
</style>
