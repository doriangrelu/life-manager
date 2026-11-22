// Navigation par espaces : les écrans (sections) sont regroupés par thème pour garder une barre du bas courte.
// Seule source de vérité : un nouvel écran s'ajoute dans la liste de son espace.
import { Bell, Car, ChartColumnBig, FolderKanban, House, ListChecks, ReceiptText, Users, Wallet } from '@lucide/svelte'
import type { Section } from './route'

export type SpaceId = 'money' | 'shopping' | 'home' | 'household'
type IconComponent = typeof Wallet

export interface SpaceEntry {
  section: Section
  label: string
  icon: IconComponent
  /** Écran proposé seulement si le foyer suit ses rentrées (réglage du propriétaire). */
  incomeTrackingOnly?: boolean
}

export interface Space {
  id: SpaceId
  label: string
  icon: IconComponent
  entries: SpaceEntry[]
}

export const SPACES: Space[] = [
  {
    id: 'money',
    label: 'Argent',
    icon: Wallet,
    entries: [
      { section: 'budget', label: 'Budget', icon: Wallet },
      { section: 'charges', label: 'Récurrents', icon: ReceiptText },
      { section: 'report', label: 'Bilan', icon: ChartColumnBig, incomeTrackingOnly: true },
    ],
  },
  {
    id: 'shopping',
    label: 'Courses',
    icon: ListChecks,
    entries: [{ section: 'shopping', label: 'Courses', icon: ListChecks }],
  },
  {
    id: 'home',
    label: 'Maison',
    icon: House,
    entries: [
      { section: 'vehicles', label: 'Véhicules', icon: Car },
      { section: 'reminders', label: 'Rappels', icon: Bell },
      { section: 'projects', label: 'Projets', icon: FolderKanban },
    ],
  },
  {
    id: 'household',
    label: 'Foyer',
    icon: Users,
    entries: [{ section: 'household', label: 'Foyer', icon: Users }],
  },
]

/** Espace qui contient un écran. */
export function spaceOf(section: Section): Space {
  return SPACES.find((space) => space.entries.some((entry) => entry.section === section)) ?? SPACES[0]
}

/** Écrans proposés dans un espace : sans suivi des rentrées, le Bilan est caché. */
export function visibleEntries(space: Space, incomeTracking: boolean): SpaceEntry[] {
  return space.entries.filter((entry) => incomeTracking || !entry.incomeTrackingOnly)
}

/**
 * Écran à ouvrir quand on touche un espace : son premier écran si on y est déjà (retour au début),
 * sinon le dernier consulté sur l'appareil, ou à défaut le premier.
 */
export function sectionToOpen(
  space: Space,
  current: Section,
  remembered: string | null,
  incomeTracking = true,
): Section {
  const entries = visibleEntries(space, incomeTracking)
  const first = entries[0].section
  if (spaceOf(current).id === space.id) {
    return first
  }
  return entries.find((entry) => entry.section === remembered)?.section ?? first
}
