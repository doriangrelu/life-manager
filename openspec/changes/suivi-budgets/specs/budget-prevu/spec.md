## ADDED Requirements

### Requirement: Total prévu et dépensé du mois

La PWA SHALL afficher, quand au moins une catégorie a un budget prévu pour le mois, une synthèse : la somme des dépenses des catégories prévues rapportée à la somme de leurs prévus, avec la même barre colorée que les catégories, et le rappel des dépenses faites dans les catégories sans prévu.

#### Scenario: Synthèse du mois

- **WHEN** « Courses » a 400 € prévus et 312 € dépensés, « Loisirs » 150 € prévus et 129 € dépensés, et « Santé » 25 € dépensés sans prévu
- **THEN** la synthèse indique 441 € dépensés sur 550 € prévus, en orange (80 %), et « + 25 € hors budget »

#### Scenario: Aucun prévu

- **WHEN** aucune catégorie n'a de budget prévu ce mois-là
- **THEN** la synthèse n'est pas affichée
