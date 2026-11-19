# bilan Specification

## Purpose
Montrer, sur un mois, un trimestre, une année ou les 12 derniers mois, ce qui est rentré, dépensé et mis de côté, en réalisé et en prévu en fin de période.

## Requirements

### Requirement: Bilan par période

Le système SHALL présenter, pour une période (un mois, un trimestre, une année ou les 12 derniers mois), le **réalisé** : total des rentrées, total des dépenses et résultat (rentrées moins dépenses), les rentrées par catégorie et par personne, et les dépenses par catégorie. Un résultat positif SHALL être présenté comme l'épargne de la période.

#### Scenario: Trimestre dans le vert

- **WHEN** un foyer a 9 000 € de rentrées et 7 800 € de dépenses sur le 4e trimestre 2026
- **THEN** le bilan du trimestre affiche un résultat de +1 200 €, présenté comme mis de côté

#### Scenario: Mois dans le rouge

- **WHEN** les dépenses d'un mois dépassent ses rentrées
- **THEN** le résultat est négatif et mis en évidence

### Requirement: Prévu en fin de période

Le bilan SHALL présenter, à côté du réalisé, le **prévu en fin de période** : le réalisé augmenté des échéances récurrentes encore ouvertes de la période (rentrées et charges à venir ou à confirmer, à leur montant prévu).

#### Scenario: Salaire pas encore arrivé

- **WHEN** au 15 novembre le salaire récurrent de 2 450 € prévu le 28 n'est pas encore arrivé
- **THEN** le prévu de novembre compte ces 2 450 € et le réalisé ne les compte pas

### Requirement: Bilan dans la PWA

La PWA SHALL proposer le Bilan comme écran de l'espace Argent, uniquement pour un foyer qui suit ses rentrées, avec un choix de période (mois, trimestre, année, 12 derniers mois) et la navigation vers la période précédente ou suivante.

#### Scenario: Foyer qui ne suit pas ses rentrées

- **WHEN** un foyer n'a pas activé le suivi des rentrées
- **THEN** l'espace Argent ne propose pas l'écran Bilan

### Requirement: Reste à vivre de la période

Le système SHALL renvoyer avec le bilan le total des dépenses de la période nées d'une dépense récurrente, et la PWA SHALL afficher le reste à vivre de la période : rentrées prévues moins dépenses récurrentes (payées et à venir), et ce qu'il en reste une fois les dépenses courantes déduites.

#### Scenario: Trimestre en cours

- **WHEN** un membre consulte le bilan du trimestre en cours
- **THEN** il voit le reste à vivre du trimestre et ce qu'il en reste, calculés sur le réalisé et sur les échéances à venir de la période

#### Scenario: Période passée

- **WHEN** un membre consulte le bilan d'une période terminée
- **THEN** le reste à vivre est calculé sur le seul réalisé

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
