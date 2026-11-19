## ADDED Requirements

### Requirement: Respect des budgets sur une période

Le système SHALL renvoyer avec le bilan, pour chaque mois de la période, le budget prévu de chaque catégorie qui en a un et les dépenses de chaque catégorie. La PWA SHALL en tirer un rapport « Budgets » : pour chaque catégorie prévue, le prévu cumulé des mois écoulés de la période, le dépensé, l'écart, et le nombre de mois où le budget a été tenu.

#### Scenario: Trimestre

- **WHEN** « Courses » a 400 € prévus par mois, et 380 €, 450 € et 390 € dépensés sur les trois mois d'un trimestre terminé
- **THEN** le rapport indique 1 220 € dépensés sur 1 200 € prévus, un dépassement de 20 €, et un budget tenu 2 mois sur 3

#### Scenario: Mois à venir

- **WHEN** la période contient des mois qui ne sont pas encore commencés
- **THEN** leur prévu n'est pas compté dans le rapport

#### Scenario: Aucun budget prévu

- **WHEN** aucune catégorie n'a de budget prévu sur la période
- **THEN** l'onglet « Budgets » n'est pas proposé
