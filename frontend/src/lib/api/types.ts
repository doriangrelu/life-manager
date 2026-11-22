// Formes des réponses JSON de l'API (miroir des DTO du backend).

export interface UserProfile {
  userId: string
  email: string
  displayName: string
  /** Vrai s'il a accepté la version en vigueur des conditions d'utilisation. */
  termsAccepted: boolean
  /** Version en vigueur des conditions (une date). */
  termsVersion: string
}

export interface HouseholdSummary {
  id: string
  name: string
}

export interface Member {
  memberId: string
  displayName: string
  joinedAt: string
}

export interface Invitation {
  code: string
  expiresAt: string
}

// ---- Budget : tous les montants sont en centimes d'euro.

export interface Category {
  id: string
  name: string
  archived: boolean
}

/**
 * Saisie ou correction d'une dépense. beneficiary = null pour une dépense commune.
 * La catégorie : soit categoryId (existante), soit newCategoryName (tapée librement, créée si besoin).
 */
export interface ExpenseInput {
  amountCents: number
  categoryId: string | null
  newCategoryName: string | null
  spentOn: string
  paidBy: string
  beneficiary: string | null
  label: string | null
  /** Projet en cours du foyer, ou null. */
  projectId?: string | null
}

export interface Expense extends Omit<ExpenseInput, 'categoryId' | 'newCategoryName'> {
  id: string
  categoryId: string
  projectId: string | null
  /** Son projet est réglé « hors budget » : elle ne compte pas dans les totaux du mois. */
  offBudget: boolean
}

// ---- Projets (travaux, vacances…) : budget prévu, tâches, dépenses rattachées.

export type ProjectColor = 'INDIGO' | 'TEAL' | 'GREEN' | 'AMBER' | 'ORANGE' | 'RED' | 'PINK' | 'SLATE'
export type ProjectStatus = 'ACTIVE' | 'DONE' | 'ABANDONED'

export interface ProjectInput {
  name: string
  description: string | null
  color: ProjectColor
  plannedCents: number | null
  startOn: string | null
  targetOn: string | null
  status: ProjectStatus
  /** Ses dépenses comptent dans le budget du mois. */
  countsInBudget: boolean
}

export interface Project extends ProjectInput {
  id: string
  tasksDone: number
  tasksTotal: number
}

export interface TaskInput {
  title: string
  assigneeId: string | null
  dueOn: string | null
}

export interface ProjectTask extends TaskInput {
  id: string
  done: boolean
}

export interface ProjectSheet {
  project: Project
  tasks: ProjectTask[]
}

export interface ProjectExpenses {
  totalCents: number
  expenses: Expense[]
}

export interface CategoryTotal {
  categoryId: string
  name: string
  totalCents: number
}

/** memberId = null pour les dépenses communes (côté bénéficiaires). */
export interface MemberTotal {
  memberId: string | null
  totalCents: number
}

/** Sens d'une catégorie, d'une charge récurrente : argent qui sort ou qui rentre. */
export type FlowDirection = 'EXPENSE' | 'INCOME'

/**
 * Saisie ou correction d'une rentrée d'argent. receivedBy = null pour le foyer entier.
 * La catégorie : soit categoryId (existante), soit newCategoryName (tapée librement, créée si besoin).
 */
export interface IncomeInput {
  amountCents: number
  categoryId: string | null
  newCategoryName: string | null
  receivedOn: string
  receivedBy: string | null
  label: string | null
}

export interface Income extends Omit<IncomeInput, 'categoryId' | 'newCategoryName'> {
  id: string
  categoryId: string
}

/** Vue d'un mois. totalCents = dépenses ; rentrées et résultat valent 0 / vides sans suivi des rentrées. */
export interface MonthlyBudget {
  month: string
  totalCents: number
  byCategory: CategoryTotal[]
  byPayer: MemberTotal[]
  byBeneficiary: MemberTotal[]
  expenses: Expense[]
  incomeCents: number
  /** Rentrées moins dépenses : négatif = mois dans le rouge. */
  resultCents: number
  incomeByCategory: CategoryTotal[]
  incomes: Income[]
  /** Budget prévu des catégories qui en ont un ce mois-là. */
  plans: CategoryPlan[]
  /** Part des dépenses née d'une dépense récurrente (charge payée) : sert au reste à vivre. */
  recurringExpenseCents: number
}

/** Portée d'un budget prévu : ce mois et les suivants, ou ce mois seulement. */
export type PlanScope = 'FROM_MONTH' | 'MONTH_ONLY'

export interface CategoryPlan {
  categoryId: string
  plannedCents: number
  scope: PlanScope
}

/** Réglage d'un budget prévu ; plannedCents = null retire le prévu. */
export interface CategoryPlanInput {
  month: string
  scope: PlanScope
  plannedCents: number | null
}

/** Bilan réalisé d'une période de mois entiers (from et to : « 2026-10 »). */
export interface BudgetReport {
  from: string
  to: string
  incomeCents: number
  expenseCents: number
  resultCents: number
  incomeByCategory: CategoryTotal[]
  /** memberId = null pour les rentrées du foyer entier. */
  incomeByReceiver: MemberTotal[]
  expenseByCategory: CategoryTotal[]
  /** Seuls les mois qui ont des mouvements. */
  months: { month: string; incomeCents: number; expenseCents: number; resultCents: number }[]
  /** Part des dépenses née d'une dépense récurrente : sert au reste à vivre. */
  recurringExpenseCents: number
  /** Budget prévu de chaque catégorie, mois par mois (pour le respect des budgets). */
  monthPlans: MonthAmount[]
  /** Dépenses de chaque catégorie, mois par mois. */
  monthExpenses: MonthAmount[]
}

/** Montant d'une catégorie pour un mois (« 2026-11 »), en centimes. */
export interface MonthAmount {
  month: string
  categoryId: string
  cents: number
}

// ---- Véhicules : dates au format « 2027-03-01 », coût en centimes d'euro, null = non renseigné.

export interface VehicleInput {
  name: string
  licensePlate: string | null
  technicalInspectionDue: string | null
  serviceDue: string | null
}

export interface Vehicle extends VehicleInput {
  id: string
}

export type MaintenanceKind = 'SERVICE' | 'OIL_CHANGE' | 'TECHNICAL_INSPECTION' | 'TIRES' | 'REPAIR' | 'OTHER'

export interface MaintenanceInput {
  performedOn: string
  kind: MaintenanceKind
  mileageKm: number | null
  costCents: number | null
  note: string | null
  /** Contrôle technique ou révision : prochaine échéance précise, null = calcul automatique par le serveur. */
  nextDueOn: string | null
  /** Type « Autre » seulement : précision libre (« Essuie-glaces »), sinon null. */
  otherLabel: string | null
}

export interface MaintenanceEntry extends Omit<MaintenanceInput, 'nextDueOn'> {
  id: string
  attachmentCount: number
}

/** Fichier joint à une opération du carnet ; mediaType est le type vérifié par le serveur. */
export interface Attachment {
  id: string
  fileName: string
  mediaType: string
  sizeBytes: number
  addedAt: string
}

// ---- Rappels : daysLeft négatif = en retard.

export interface Reminder {
  deadlineId: string
  /** PERSONAL : rappel saisi par un membre (deadlineId est alors son identifiant) ; TASK : tâche de projet datée. */
  kind: 'TECHNICAL_INSPECTION' | 'SERVICE' | 'CHARGE' | 'CHARGE_TO_CONFIRM' | 'PERSONAL' | 'TASK'
  subject: string
  dueOn: string
  status: 'OVERDUE' | 'DUE_SOON'
  daysLeft: number
}

export type Recurrence = 'NONE' | 'MONTHLY' | 'QUARTERLY' | 'YEARLY'
/** À partir de quand prévenir avant la date : le jour même, 1 semaine ou 1 mois avant. */
export type NoticePeriod = 'SAME_DAY' | 'ONE_WEEK' | 'ONE_MONTH'

/** Saisie d'un rappel ; note null = aucune. */
export interface PersonalReminderInput {
  title: string
  note: string | null
  dueOn: string
  recurrence: Recurrence
  notice: NoticePeriod
}

/** Rappel saisi par un membre, visible par tout le foyer. */
export interface PersonalReminder extends PersonalReminderInput {
  id: string
}

/** Ligne de l'historique : un « c'est fait » (titre recopié, date prévue, moment et membre). */
export interface ReminderDone {
  id: string
  title: string
  dueOn: string
  /** Instant ISO. */
  doneAt: string
  doneBy: string
}

// ---- Courses

export interface ShoppingItem {
  id: string
  label: string
  checked: boolean
  addedAt: string
}

/** Sous-liste de courses (« Courses », « Bricolage »…). */
export interface ShoppingList {
  id: string
  name: string
  /** Nombre d'articles non cochés. */
  pendingCount: number
}

/** Article déjà acheté dans une sous-liste. */
export interface PurchaseHistoryEntry {
  id: string
  label: string
  lastPurchasedAt: string
  purchaseCount: number
}

export interface HouseholdDetails {
  id: string
  name: string
  /** Propriétaire : lui seul renomme, invite, retire un membre, transmet ou supprime le foyer. */
  ownerId: string
  members: Member[]
  /** Code actif ; toujours null pour un membre qui n'est pas propriétaire. */
  activeInvitation: Invitation | null
  /** Suivi des rentrées et bilan activés (réglage du propriétaire). */
  incomeTracking: boolean
}

// ---- Charges récurrentes (abonnements, factures)

export type AmountKind = 'FIXED' | 'VARIABLE'
export type Frequency = 'MONTHLY' | 'BIMONTHLY' | 'QUARTERLY' | 'YEARLY'
export type OccurrenceStatus = 'UPCOMING' | 'TO_CONFIRM' | 'PAID' | 'SKIPPED'

/** Saisie d'une charge ; montant en centimes, bénéficiaire null = tout le foyer. */
export interface ChargeInput {
  name: string
  amountCents: number
  amountKind: AmountKind
  frequency: Frequency
  firstDueOn: string
  budgetCategoryId: string
  /** Pour une rentrée récurrente : le membre qui la reçoit. */
  payerId: string
  beneficiaryId: string | null
  /** Absent = dépense. */
  direction?: FlowDirection
  /** Dernière échéance (fin d'un crédit) ; absente ou null = sans fin. */
  lastDueOn?: string | null
  /** Présent = crédit : amountCents est le total, assurance comprise (0 = sans assurance). */
  loan?: { insuranceCents: number } | null
}

/** Une échéance d'une charge. */
export interface Occurrence {
  id: string
  chargeId: string
  dueOn: string
  expectedCents: number
  status: OccurrenceStatus
  paidCents: number | null
  paidOn: string | null
  invoiceCount: number
}

/** Une charge avec sa prochaine échéance ouverte (null s'il n'y en a pas). */
export interface Charge extends ChargeInput {
  id: string
  active: boolean
  next: Occurrence | null
}

/** Échéance encore attendue d'une période, à son montant prévu ; PLANNED = calculée, pas encore créée. */
export interface ForecastItem {
  occurrenceId: string | null
  chargeId: string
  name: string
  dueOn: string
  expectedCents: number
  status: 'UPCOMING' | 'TO_CONFIRM' | 'PLANNED'
  direction: FlowDirection
  budgetCategoryId: string
}
