<script lang="ts">
  import { addCategory, archiveCategory, reactivateCategory, renameCategory } from '../../api/budgetApi'
  import type { Category } from '../../api/types'
  import { categoryStyle } from '../../categoryStyle'
  import TextDialog from '../../ui/TextDialog.svelte'

  // Gestion des catégories du foyer (dans une modale) : ajout, renommage, archivage, réactivation.
  let { householdId, categories = $bindable() }: { householdId: string; categories: Category[] } = $props()

  let newName = $state('')
  let error = $state<string | null>(null)
  let renaming = $state<Category | null>(null)
  let renameOpen = $state(false)

  // Remplace (ou ajoute) une catégorie dans la liste, triée par nom comme côté serveur.
  function upsert(category: Category) {
    const others = categories.filter((c) => c.id !== category.id)
    categories = [...others, category].sort((a, b) => a.name.localeCompare(b.name, 'fr'))
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
    if (category && name !== category.name) {
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
      <span class="name">{category.name}{category.archived ? ' (archivée)' : ''}</span>
      <button class="link" onclick={() => startRename(category)}>Renommer</button>
      <button class="link" onclick={() => toggle(category)}>{category.archived ? 'Réactiver' : 'Archiver'}</button>
    </li>
  {/each}
</ul>

<TextDialog
  bind:open={renameOpen}
  title="Renommer la catégorie"
  label="Nouveau nom"
  initial={renaming?.name ?? ''}
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
    gap: 10px;
    flex-wrap: wrap;
  }
  .list li.archived .name {
    color: var(--color-text-muted);
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
    min-width: 120px;
  }
</style>
