<script lang="ts">
  import { addCategory, archiveCategory, reactivateCategory, renameCategory } from '../../api/budgetApi'
  import type { Category } from '../../api/types'

  // Gestion des catégories du foyer : ajout, renommage, archivage, réactivation.
  let {
    householdId,
    categories = $bindable(),
    onclose,
  }: { householdId: string; categories: Category[]; onclose: () => void } = $props()

  let newName = $state('')
  let error = $state<string | null>(null)

  // Remplace (ou ajoute) une catégorie dans la liste, triée par nom comme côté serveur.
  function upsert(category: Category) {
    const others = categories.filter((c) => c.id !== category.id)
    categories = [...others, category].sort((a, b) => a.name.localeCompare(b.name, 'fr'))
  }

  async function run(action: () => Promise<Category>) {
    error = null
    try {
      upsert(await action())
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  async function add(event: SubmitEvent) {
    event.preventDefault()
    await run(() => addCategory(householdId, newName))
    if (!error) {
      newName = ''
    }
  }

  function rename(category: Category) {
    const name = prompt('Nouveau nom', category.name)
    if (name && name.trim() !== category.name) {
      run(() => renameCategory(householdId, category.id, name))
    }
  }

  function toggle(category: Category) {
    run(() => (category.archived ? reactivateCategory : archiveCategory)(householdId, category.id))
  }
</script>

<section class="stack">
  <h1>Catégories</h1>

  <form class="add" onsubmit={add}>
    <input bind:value={newName} maxlength="40" placeholder="Nouvelle catégorie" required />
    <button disabled={!newName.trim()}>Ajouter</button>
  </form>

  {#if error}
    <p class="error" role="alert">{error}</p>
  {/if}

  <ul class="card list">
    {#each categories as category (category.id)}
      <li class:archived={category.archived}>
        <span>{category.name}{category.archived ? ' (archivée)' : ''}</span>
        <span class="actions">
          <button class="link" onclick={() => rename(category)}>Renommer</button>
          <button class="link" onclick={() => toggle(category)}>
            {category.archived ? 'Réactiver' : 'Archiver'}
          </button>
        </span>
      </li>
    {/each}
  </ul>

  <button class="link" onclick={onclose}>Retour au mois</button>
</section>

<style>
  .add {
    display: flex;
    gap: 8px;
  }
  .add input {
    flex: 1;
    min-width: 0;
  }
  .list {
    list-style: none;
    margin: 0;
    display: grid;
    gap: 10px;
  }
  .list li {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
  }
  .list li.archived {
    color: var(--muted);
  }
  .actions {
    display: flex;
    gap: 12px;
  }
</style>
