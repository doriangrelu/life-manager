import { apiRequest } from './http'
import type { Project, ProjectExpenses, ProjectInput, ProjectSheet, ProjectTask, TaskInput } from './types'

const projects = (householdId: string) => `/households/${householdId}/projects`
const tasks = (householdId: string, projectId: string) => `${projects(householdId)}/${projectId}/tasks`

/** Projets du foyer, les plus récents d'abord, avec l'avancement de leurs tâches. */
export function listProjects(householdId: string): Promise<Project[]> {
  return apiRequest('GET', projects(householdId))
}

/** Fiche d'un projet : le projet et ses tâches dans l'ordre. */
export function getProject(householdId: string, projectId: string): Promise<ProjectSheet> {
  return apiRequest('GET', `${projects(householdId)}/${projectId}`)
}

export function addProject(householdId: string, input: ProjectInput): Promise<Project> {
  return apiRequest('POST', projects(householdId), input)
}

export function updateProject(householdId: string, projectId: string, input: ProjectInput): Promise<Project> {
  return apiRequest('PUT', `${projects(householdId)}/${projectId}`, input)
}

/** Supprime le projet et ses tâches ; ses dépenses restent dans le budget, sans projet. */
export function deleteProject(householdId: string, projectId: string): Promise<void> {
  return apiRequest('DELETE', `${projects(householdId)}/${projectId}`)
}

/** Dépenses rattachées au projet (toutes dates) et leur total. */
export function projectExpenses(householdId: string, projectId: string): Promise<ProjectExpenses> {
  return apiRequest('GET', `${projects(householdId)}/${projectId}/expenses`)
}

export function addTask(householdId: string, projectId: string, input: TaskInput): Promise<ProjectTask> {
  return apiRequest('POST', tasks(householdId, projectId), input)
}

export function updateTask(householdId: string, projectId: string, taskId: string, input: TaskInput): Promise<ProjectTask> {
  return apiRequest('PUT', `${tasks(householdId, projectId)}/${taskId}`, input)
}

export function deleteTask(householdId: string, projectId: string, taskId: string): Promise<void> {
  return apiRequest('DELETE', `${tasks(householdId, projectId)}/${taskId}`)
}

/** Coche (fait) ou décoche une tâche. */
export function markTask(householdId: string, projectId: string, taskId: string, done: boolean): Promise<void> {
  return apiRequest(done ? 'POST' : 'DELETE', `${tasks(householdId, projectId)}/${taskId}/done`)
}

/** Nouvel ordre des tâches (toutes, chacune une fois). */
export function reorderTasks(householdId: string, projectId: string, ids: string[]): Promise<void> {
  return apiRequest('PUT', `${tasks(householdId, projectId)}/order`, { ids })
}
