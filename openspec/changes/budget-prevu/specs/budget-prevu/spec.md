## ADDED Requirements

### Requirement: Budget prévu par catégorie

Le système SHALL permettre à un membre de fixer, pour une catégorie de dépenses de son foyer, un montant prévu en centimes d'euro (0,01 € à 1 000 000 €), soit à partir d'un mois donné, soit pour ce mois seulement, et de le retirer de la même façon.

#### Scenario: Tous les mois

- **WHEN** un membre fixe 400 € (40000 centimes) pour « Courses » à partir de novembre 2026
- **THEN** le prévu de « Courses » est de 400 € en novembre 2026, en décembre 2026 et les mois suivants

#### Scenario: Ce mois seulement

- **WHEN** « Courses » a 400 € prévus à partir de novembre 2026 et qu'un membre fixe 600 € pour décembre 2026 seulement
- **THEN** le prévu est de 600 € en décembre 2026 et reste de 400 € en novembre 2026 et en janvier 2027

#### Scenario: Les mois passés ne changent pas

- **WHEN** « Courses » a 400 € prévus à partir de novembre 2026 et qu'un membre fixe 450 € à partir de janvier 2027
- **THEN** le prévu reste de 400 € en novembre et décembre 2026, et passe à 450 € à partir de janvier 2027

#### Scenario: Retirer le prévu

- **WHEN** un membre retire le prévu de « Courses » à partir de mars 2027
- **THEN** « Courses » n'a plus de prévu à partir de mars 2027, et garde celui des mois précédents

#### Scenario: Valeur refusée

- **WHEN** un membre fixe un montant nul, négatif ou supérieur à 1 000 000 €, ou vise une catégorie de rentrées ou archivée
- **THEN** la demande est refusée avec une erreur `400 Bad Request`

#### Scenario: Catégorie d'un autre foyer

- **WHEN** un membre vise une catégorie inconnue ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Prévu dans la vue du mois

Le système SHALL renvoyer, avec la vue d'un mois, le prévu applicable de chaque catégorie qui en a un, ainsi que le total des dépenses du mois nées d'une dépense récurrente.

#### Scenario: Mois avec un prévu

- **WHEN** un membre consulte novembre 2026 alors que « Courses » a 400 € prévus
- **THEN** la vue contient le prévu de 400 € pour « Courses », même sans dépense dans cette catégorie

#### Scenario: Aucun prévu

- **WHEN** un membre consulte un mois sans aucun prévu
- **THEN** la vue ne contient aucun prévu et le reste de la réponse est inchangé

### Requirement: Suivi prévu et réel dans la PWA

La PWA SHALL afficher, pour chaque catégorie ayant un prévu, le dépensé rapporté au prévu avec une barre de progression : couleur normale sous 80 %, orange de 80 % à 100 %, rouge au-delà, en comptant à part les dépenses récurrentes encore à venir dans le mois. Sans prévu, l'affichage reste celui d'avant.

#### Scenario: Seuil d'alerte

- **WHEN** « Loisirs » a 150 € prévus et 129 € dépensés
- **THEN** sa barre est orange et une pastille indique 86 %

#### Scenario: Dépassement

- **WHEN** « Restaurants » a 100 € prévus et 134 € dépensés
- **THEN** sa barre est rouge et une pastille indique « + 34 € »

#### Scenario: Dépenses à venir

- **WHEN** « Logement » a 900 € prévus, 92 € dépensés et 760 € de dépenses récurrentes à venir ce mois
- **THEN** la barre montre les 92 € puis, en plus clair, les 760 € à venir

#### Scenario: Régler depuis le Budget

- **WHEN** un membre touche une catégorie dans « Par catégorie »
- **THEN** une fenêtre lui propose le montant prévu, le choix « Tous les mois » (par défaut) ou « Ce mois seulement », et de retirer le prévu

### Requirement: Reste à vivre du mois

La PWA SHALL afficher dans le bandeau du mois, quand le foyer suit ses rentrées, le reste à vivre (rentrées prévues moins dépenses récurrentes du mois, payées et à venir) et ce qu'il en reste une fois les dépenses courantes déduites.

#### Scenario: Mois en cours

- **WHEN** le mois compte 3 200 € de rentrées prévues, 1 750 € de dépenses récurrentes (payées et à venir) et 838 € de dépenses courantes
- **THEN** le bandeau affiche un reste à vivre de 1 450 €, dont il reste 612 €

#### Scenario: Suivi des rentrées désactivé

- **WHEN** le foyer ne suit pas ses rentrées
- **THEN** le bandeau n'affiche pas de reste à vivre
