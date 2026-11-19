## ADDED Requirements

### Requirement: Évolution mois par mois

La PWA SHALL proposer dans le Bilan, pour une période de plusieurs mois, un onglet « Évolution » avec deux graphiques : les rentrées et les dépenses de chaque mois avec l'épargne cumulée depuis le début de la période, puis les dépenses de chaque mois face au prévu, pour toutes les catégories prévues ou pour une catégorie choisie. Toucher un mois affiche ses chiffres exacts ; les mois à venir ne comptent ni dans l'épargne ni dans la moyenne.

#### Scenario: Année en cours

- **WHEN** un membre ouvre l'onglet « Évolution » sur l'année en cours
- **THEN** il voit, pour chaque mois, les rentrées et les dépenses, et l'épargne cumulée s'arrête au mois courant

#### Scenario: Mois au-dessus du prévu

- **WHEN** « Courses » a 400 € prévus par mois et 450 € dépensés en octobre
- **THEN** la barre d'octobre est rouge et le graphique indique « 1 mois au-dessus du prévu : oct. »

#### Scenario: Un seul mois

- **WHEN** la période choisie est un mois
- **THEN** l'onglet « Évolution » n'est pas proposé
