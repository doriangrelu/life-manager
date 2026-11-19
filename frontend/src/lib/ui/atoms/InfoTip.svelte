<script lang="ts">
  import { Info } from '@lucide/svelte'

  // Icône « i » qui explique un champ dans une bulle : au survol ou au clavier sur ordinateur,
  // au toucher sur téléphone (un second toucher, ou un toucher ailleurs, la referme).
  // La bulle se place sous la ligne du libellé : le parent direct doit être en « position: relative ».
  let { text, label = 'Explication' }: { text: string; label?: string } = $props()

  const id = $props.id()
  let open = $state(false)
  let root: HTMLSpanElement | undefined = $state()

  // Ouverte : un clic ou un toucher en dehors la referme.
  $effect(() => {
    if (!open) {
      return
    }
    const closeOutside = (event: PointerEvent) => {
      if (root && !root.contains(event.target as Node)) {
        open = false
      }
    }
    document.addEventListener('pointerdown', closeOutside)
    return () => document.removeEventListener('pointerdown', closeOutside)
  })

  function onkeydown(event: KeyboardEvent) {
    if (event.key === 'Escape' && open) {
      // Échap ferme la bulle, pas la fenêtre qui la contient.
      event.preventDefault()
      event.stopPropagation()
      open = false
    }
  }

  // Le survol ne vaut que pour une souris : au doigt, c'est le toucher (clic) qui ouvre et ferme.
  const hover = (event: PointerEvent, shown: boolean) => {
    if (event.pointerType === 'mouse') {
      open = shown
    }
  }
</script>

<span class="tip" role="presentation" bind:this={root} onpointerenter={(e) => hover(e, true)} onpointerleave={(e) => hover(e, false)}>
  <button
    type="button"
    class="icon"
    aria-label={label}
    aria-expanded={open}
    aria-describedby={open ? id : undefined}
    onclick={() => (open = !open)}
    onfocus={() => (open = true)}
    onblur={() => (open = false)}
    {onkeydown}
  >
    <Info size={16} strokeWidth={2.4} />
  </button>
  {#if open}
    <span class="bubble" role="tooltip" {id}>{text}</span>
  {/if}
</span>

<style>
  .tip {
    display: inline-flex;
  }
  /* Petite icône, mais zone de toucher confortable. */
  .icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    min-height: 0;
    margin: -6px 0;
    padding: 0;
    border-radius: 50%;
    background: transparent;
    color: var(--color-primary-text);
    box-shadow: none;
  }
  @media (hover: hover) {
    .icon:hover {
      filter: none;
      background: var(--color-primary-soft);
    }
  }
  .bubble {
    position: absolute;
    z-index: 5;
    top: calc(100% + 6px);
    left: 0;
    right: 0;
    padding: 10px 12px;
    border-radius: var(--radius-md);
    background: var(--color-text);
    color: var(--color-surface);
    font-size: 0.82rem;
    font-weight: 500;
    line-height: 1.4;
    text-align: left;
    box-shadow: var(--shadow-sm);
  }
</style>
