## ADDED Requirements

### Requirement: Rentrées et résultat du mois

Pour un foyer qui suit ses rentrées, la vue mensuelle SHALL présenter, en plus des dépenses, le total des rentrées du mois, leur liste, et le résultat du mois (rentrées moins dépenses), positif ou négatif. Pour un foyer qui ne suit pas ses rentrées, la vue mensuelle SHALL rester inchangée.

#### Scenario: Mois dans le vert

- **WHEN** un foyer qui suit ses rentrées a 3 000 € de rentrées et 2 400 € de dépenses en novembre 2026
- **THEN** la vue de novembre 2026 affiche Dépensé 2 400 €, Rentré 3 000 € et un résultat de +600 €

#### Scenario: Suivi désactivé

- **WHEN** un foyer qui ne suit pas ses rentrées ouvre son budget
- **THEN** le bandeau et le bouton d'ajout sont ceux d'avant ce changement
