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

export interface HouseholdDetails {
  id: string
  name: string
  members: Member[]
  activeInvitation: Invitation | null
}
