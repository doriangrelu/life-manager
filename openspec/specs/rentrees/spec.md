# rentrees Specification

## Purpose
Suivre l'argent qui rentre dans le foyer (salaires, primes, aides…), saisi comme les dépenses ou arrivé seul depuis une rentrée récurrente, quand le foyer a activé le suivi des rentrées.

## Requirements

### Requirement: Rentrées d'argent

Le système SHALL permettre à un membre d'enregistrer une rentrée d'argent pour son foyer : montant strictement positif en centimes, date qui n'est pas dans le futur, catégorie de rentrée du foyer, libellé optionnel, et bénéficiaire (un membre du foyer, ou le foyer entier). Un membre SHALL pouvoir modifier et supprimer une rentrée de son foyer.

#### Scenario: Salaire

- **WHEN** un membre enregistre 2 450 € dans la catégorie « Salaire », reçus par lui le 2026-11-28
- **THEN** la rentrée apparaît dans le mois de novembre 2026, au nom de ce membre

#### Scenario: Montant invalide

- **WHEN** un membre enregistre une rentrée de 0 €
- **THEN** l'enregistrement est refusé avec une erreur `400 Bad Request`

### Requirement: Catégories de rentrées

Le système SHALL gérer les catégories de rentrées comme celles des dépenses (ajout, renommage, archivage), séparément d'elles : une rentrée n'utilise qu'une catégorie de rentrée et une dépense qu'une catégorie de dépense. Le système SHALL proposer au départ les catégories Salaire, Prime, Remboursement, Aides et Autre, une seule fois par foyer.

#### Scenario: Catégories suggérées

- **WHEN** un foyer commence à suivre ses rentrées
- **THEN** ses catégories de rentrées sont Salaire, Prime, Remboursement, Aides et Autre, et ses catégories de dépenses sont inchangées

#### Scenario: Mauvais sens

- **WHEN** un membre enregistre une rentrée dans la catégorie de dépense « Courses »
- **THEN** l'enregistrement est refusé avec une erreur `400 Bad Request`

### Requirement: Rentrées récurrentes

Le système SHALL permettre de déclarer une rentrée récurrente (ex. salaire mensuel) avec les mêmes règles que les charges récurrentes : à chaque échéance, une rentrée à montant fixe SHALL être enregistrée seule, et une rentrée à montant variable SHALL attendre la confirmation de son montant. Une même échéance SHALL ne produire qu'une seule rentrée.

#### Scenario: Salaire fixe

- **WHEN** un salaire récurrent de 2 450 € a son échéance le 28 du mois
- **THEN** le 28, une rentrée de 2 450 € est enregistrée sans action du membre

#### Scenario: Prime variable

- **WHEN** une prime récurrente à montant variable arrive à échéance
- **THEN** elle attend la confirmation de son montant, avec un rappel, et ne compte dans le réalisé qu'une fois confirmée

### Requirement: Gestion des catégories de rentrées dans la PWA

La PWA SHALL permettre, quand l'option « Rentrées et bilan » est active, de gérer les catégories de rentrées depuis la fenêtre « Catégories » du Budget, comme celles des dépenses : ajout, renommage, archivage et réactivation. Sans l'option, la fenêtre ne montre que les catégories de dépenses.

#### Scenario: Renommer une catégorie de rentrées

- **WHEN** un membre ouvre « Catégories », choisit « Rentrées » et renomme « Aides » en « CAF »
- **THEN** la catégorie est renommée et proposée sous ce nom à la saisie d'une rentrée

#### Scenario: Archiver une catégorie de rentrées

- **WHEN** un membre archive la catégorie de rentrées « Prime »
- **THEN** elle n'est plus proposée à la saisie, et les rentrées déjà classées dedans restent affichées

#### Scenario: Option désactivée

- **WHEN** l'option « Rentrées et bilan » est désactivée et qu'un membre ouvre « Catégories »
- **THEN** il ne voit que les catégories de dépenses, sans choix Dépenses / Rentrées
