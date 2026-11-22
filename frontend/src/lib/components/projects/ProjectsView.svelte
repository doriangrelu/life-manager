<script lang="ts">
  import { ChevronRight, FolderKanban } from '@lucide/svelte'
  import { onMount } from 'svelte'
  import { flip } from 'svelte/animate'
  import { fade } from 'svelte/transition'
  import { addProject, listProjects } from '../../api/projectApi'
  import type { Project, ProjectInput } from '../../api/types'
  import { formatCents } from '../../money'
  import { listMove, ms, rise } from '../../motion'
  import { PROJECT_COLORS, splitProjects, statusLabel, tasksText } from '../../projects'
  import { navigate, route } from '../../route'
  import { displayName } from '../../text'
  import ProgressBar from '../../ui/atoms/ProgressBar.svelte'
  import Skeleton from '../../ui/atoms/Skeleton.svelte'
  import Fab from '../../ui/molecules/Fab.svelte'
  import Modal from '../../ui/molecules/Modal.svelte'
  import ProjectDetail from './ProjectDetail.svelte'
  import ProjectForm from './ProjectForm.svelte'

  // Écran Projets de l'espace Maison : projets en cours, puis terminés et abandonnés ; chaque fiche s'ouvre à part.
  let { householdId }: { householdId: string } = $props()

  let projects = $state<Project[]>([])
  let loaded = $state(false)
  let adding = $state(false)
  let showClosed = $state(false)
  let error = $state<string | null>(null)
  const openedId = $derived($route.projectId ?? null)
  const groups = $derived(splitProjects(projects))

  onMount(() => run(load))

  // Retour à la liste (bouton de la fiche ou Précédent) : liste rafraîchie.
  let wasOpen = false
  $effect(() => {
    const open = openedId !== null
    if (wasOpen && !open) {
      run(load)
    }
    wasOpen = open
  })

  async function load() {
    projects = await listProjects(householdId)
    loaded = true
  }

  async function run(action: () => Promise<void>) {
    error = null
    try {
      await action()
    } catch (e) {
      error = e instanceof Error ? e.message : String(e)
    }
  }

  // Un refus remonte au formulaire ; le projet créé s'ouvre directement.
  async function add(input: ProjectInput) {
    const created = await addProject(householdId, input)
    adding = false
    navigate({ section: 'projects', projectId: created.id })
  }

  const open = (projectId: string) => navigate({ section: 'projects', projectId })
  const close = (replace = false) => navigate({ section: 'projects' }, { replace })
</script>

{#snippet card(project: Project)}
  {@const tasks = tasksText(project)}
  <button class="project" onclick={() => open(project.id)}>
    <span class="bubble" style:--tint={PROJECT_COLORS[project.color]} aria-hidden="true"><FolderKanban size={21} /></span>
    <span class="text">
      <strong>{displayName(project.name)}</strong>
      <span class="meta">
        {#if project.status !== 'ACTIVE'}{statusLabel(project.status)} · {/if}{tasks ?? 'Aucune tâche'}{#if project.plannedCents !== null} · {formatCents(project.plannedCents)} prévus{/if}{#if !project.countsInBudget} · budget à part{/if}
      </span>
      {#if project.tasksTotal > 0}
        <ProgressBar ratio={project.tasksDone / project.tasksTotal} color={PROJECT_COLORS[project.color]} />
      {/if}
    </span>
    <ChevronRight size={20} />
  </button>
{/snippet}

{#if openedId}
  {#key openedId}
    <div class="stack" in:rise>
      <ProjectDetail {householdId} projectId={openedId} onclose={() => close()} onremoved={() => close(true)} />
    </div>
  {/key}
{:else}
  <div class="stack" in:rise>
    <h1>Projets</h1>
    {#if error}
      <p class="error" role="alert">{error}</p>
    {/if}

    {#if !loaded && !error}
      <Skeleton variant="cards" count={2} />
    {:else if loaded && projects.length === 0}
      <div class="card empty">
        <p class="muted">
          Travaux, vacances, achat d'une voiture… Un projet réunit son budget, les dépenses qui s'y rattachent et la liste
          de ce qu'il reste à faire.
        </p>
        <button class="soft" onclick={() => (adding = true)}>Créer un projet</button>
      </div>
    {/if}

    {#if groups.active.length > 0}
      <ul class="projects">
        {#each groups.active as project (project.id)}
          <li animate:flip={listMove} in:rise out:fade={{ duration: ms(150) }}>{@render card(project)}</li>
        {/each}
      </ul>
    {:else if loaded && projects.length > 0}
      <p class="muted">Aucun projet en cours.</p>
    {/if}

    {#if groups.closed.length > 0}
      <button class="link toggle" aria-expanded={showClosed} onclick={() => (showClosed = !showClosed)}>
        {showClosed ? 'Masquer' : 'Voir'} les projets terminés et abandonnés ({groups.closed.length})
      </button>
      {#if showClosed}
        <ul class="projects closed" transition:fade={{ duration: ms(150) }}>
          {#each groups.closed as project (project.id)}
            <li>{@render card(project)}</li>
          {/each}
        </ul>
      {/if}
    {/if}
  </div>
  <Fab label="Créer un projet" onclick={() => (adding = true)} />
{/if}

<Modal bind:open={adding} title="Nouveau projet">
  <ProjectForm project={null} onsave={add} />
</Modal>

<style>
  .projects {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    gap: 10px;
  }
  .closed {
    opacity: 0.85;
  }
  .project {
    width: 100%;
    justify-content: flex-start;
    gap: 12px;
    padding: 14px;
    border-radius: var(--radius-lg);
    background: var(--color-surface);
    color: var(--color-text);
    text-align: left;
  }
  @media (hover: hover) {
    .project:hover {
      filter: none;
      box-shadow: var(--shadow-sm), 0 0 0 2px var(--color-primary-soft);
    }
  }
  .bubble {
    width: 44px;
    height: 44px;
    border-radius: var(--radius-md);
    background: color-mix(in srgb, var(--tint) 18%, transparent);
    color: var(--tint);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .text {
    flex: 1;
    display: grid;
    gap: 6px;
    min-width: 0;
  }
  .text strong {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .meta {
    font-size: 0.82rem;
    font-weight: 500;
    color: var(--color-text-muted);
  }
  .toggle {
    justify-self: start;
  }
  .empty {
    display: grid;
    gap: 12px;
    justify-items: start;
  }
  .empty p {
    margin: 0;
  }
</style>
