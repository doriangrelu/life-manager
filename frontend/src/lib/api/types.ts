// Formes des réponses JSON de l'API (miroir des DTO du backend).

export interface UserProfile {
  userId: string
  email: string
  displayName: string
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
}

export interface Expense extends Omit<ExpenseInput, 'categoryId' | 'newCategoryName'> {
  id: string
  categoryId: string
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

export interface MonthlyBudget {
  month: string
  totalCents: number
  byCategory: CategoryTotal[]
  byPayer: MemberTotal[]
  byBeneficiary: MemberTotal[]
  expenses: Expense[]
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
  kind: 'TECHNICAL_INSPECTION' | 'SERVICE' | 'CHARGE' | 'CHARGE_TO_CONFIRM'
  subject: string
  dueOn: string
  status: 'OVERDUE' | 'DUE_SOON'
  daysLeft: number
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
  members: Member[]
  activeInvitation: Invitation | null
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
  payerId: string
  beneficiaryId: string | null
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
