<script lang="ts">
  import { Archive, ArchiveRestore, Pencil } from '@lucide/svelte'
  import { displayName } from '../../text'
  import { addCategory, archiveCategory, reactivateCategory, renameCategory } from '../../api/budgetApi'
  import type { Category } from '../../api/types'
  import { sortCategories } from '../../categories'
  import { categoryStyle } from '../../categoryStyle'
  import TextDialog from '../../ui/molecules/TextDialog.svelte'

  // Gestion des catégories du foyer (dans une modale) : ajout, renommage, archivage, réactivation.
  let { householdId, categories = $bindable() }: { householdId: string; categories: Category[] } = $props()

  let newName = $state('')
  let error = $state<string | null>(null)
  let renaming = $state<Category | null>(null)
  let renameOpen = $state(false)

  // Remplace (ou ajoute) une catégorie dans la liste, triée comme côté serveur (« Autre » en dernier).
  function upsert(category: Category) {
    categories = sortCategories([...categories.filter((c) => c.id !== category.id), category])
  }

  async function run(action: () => Promise<Category>): Promise<boolean> {
    error = null
    try {
      upsert(await action())
      return true
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
      return false
    }
  }

  async function add(event: SubmitEvent) {
    event.preventDefault()
    if (await run(() => addCategory(householdId, newName))) {
      newName = ''
    }
  }

  function startRename(category: Category) {
    renaming = category
    renameOpen = true
  }

  function rename(name: string) {
    const category = renaming
    // Le nom affiché a une majuscule, l'enregistré non : on compare sans tenir compte de la casse.
    if (category && name.trim().toLowerCase() !== category.name) {
      run(() => renameCategory(householdId, category.id, name))
    }
  }

  function toggle(category: Category) {
    run(() => (category.archived ? reactivateCategory : archiveCategory)(householdId, category.id))
  }
</script>

<form class="add" onsubmit={add}>
  <label class="visually-hidden" for="new-category">Nouvelle catégorie</label>
  <input id="new-category" bind:value={newName} maxlength="40" placeholder="Nouvelle catégorie" required />
  <button disabled={!newName.trim()}>Ajouter</button>
</form>

{#if error}
  <p class="error" role="alert">{error}</p>
{/if}

<ul class="list">
  {#each categories as category (category.id)}
    {@const style = categoryStyle(category.name)}
    <li class:archived={category.archived}>
      <span class="bubble" style:background={style.soft} aria-hidden="true">
        <style.icon size={18} color={style.strong} />
      </span>
      <span class="name">
        {displayName(category.name)}
        {#if category.archived}<span class="tag">Archivée</span>{/if}
      </span>
      <button class="icon ghost" aria-label="Renommer {displayName(category.name)}" title="Renommer" onclick={() => startRename(category)}>
        <Pencil size={17} />
      </button>
      <button
        class="icon ghost"
        aria-label="{category.archived ? 'Réactiver' : 'Archiver'} {displayName(category.name)}"
        title={category.archived ? 'Réactiver' : 'Archiver'}
        onclick={() => toggle(category)}
      >
        {#if category.archived}<ArchiveRestore size={17} />{:else}<Archive size={17} />{/if}
      </button>
    </li>
  {/each}
</ul>

<TextDialog
  bind:open={renameOpen}
  title="Renommer la catégorie"
  label="Nouveau nom"
  initial={renaming ? displayName(renaming.name) : ''}
  maxlength={40}
  onsubmit={rename}
/>

<style>
  .add {
    display: flex;
    gap: 8px;
  }
  .list {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 4px;
  }
  .list li {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 4px 4px 6px;
    border-radius: var(--radius-md);
    transition: background-color 0.15s ease;
  }
  @media (hover: hover) {
    .list li:hover {
      background: var(--color-surface-muted);
    }
  }
  .list li.archived .name,
  .list li.archived .bubble {
    opacity: 0.55;
  }
  .list .ghost {
    box-shadow: none;
    background: transparent;
    color: var(--color-text-muted);
  }
  .tag {
    margin-left: 8px;
    padding: 2px 8px;
    border-radius: 999px;
    background: var(--color-surface-muted);
    color: var(--color-text-muted);
    font-size: 0.75rem;
    font-weight: 700;
  }
  .bubble {
    width: 34px;
    height: 34px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .name {
    flex: 1;
    font-weight: 700;
    min-width: 0;
    margin-left: 6px;
  }
</style>
