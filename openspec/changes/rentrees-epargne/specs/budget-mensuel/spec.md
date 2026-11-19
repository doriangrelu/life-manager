## ADDED Requirements

### Requirement: Solde du mois

La vue mensuelle SHALL présenter, en plus des dépenses, le total des rentrées du mois, le total épargné du mois et le solde du mois, égal aux rentrées moins les dépenses moins l'épargne, ainsi que la liste des rentrées et des épargnes du mois.

#### Scenario: Mois dans le vert

- **WHEN** un foyer a 3 000 € de rentrées, 2 100 € de dépenses et 300 € d'épargne en novembre 2026
- **THEN** la vue de novembre 2026 affiche un solde de 600 €

#### Scenario: Mois dans le rouge

- **WHEN** les dépenses et l'épargne d'un mois dépassent ses rentrées
- **THEN** le solde est négatif et la PWA le met en évidence
