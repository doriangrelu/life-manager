## ADDED Requirements

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
