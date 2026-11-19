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
