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

/** Saisie ou correction d'une dépense. beneficiary = null pour une dépense commune. */
export interface ExpenseInput {
  amountCents: number
  categoryId: string
  spentOn: string
  paidBy: string
  beneficiary: string | null
  label: string | null
}

export interface Expense extends ExpenseInput {
  id: string
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
}

export interface MaintenanceEntry extends MaintenanceInput {
  id: string
}

export interface HouseholdDetails {
  id: string
  name: string
  members: Member[]
  activeInvitation: Invitation | null
}
