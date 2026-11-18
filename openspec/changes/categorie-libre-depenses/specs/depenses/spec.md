## ADDED Requirements

### Requirement: Nouvelle catégorie à la saisie

Le système SHALL permettre de saisir ou de corriger une dépense en indiquant un nom de catégorie (1 à 40 caractères) à la place d'une catégorie existante. Si une catégorie du foyer porte déjà ce nom sans tenir compte des majuscules, elle est utilisée, et réactivée si elle était archivée ; sinon une catégorie active est créée. La catégorie et la dépense sont enregistrées ensemble, ou pas du tout.

#### Scenario: Catégorie créée à la volée

- **WHEN** un membre enregistre une dépense de 25 € avec le nom de catégorie « Cadeaux », que le foyer n'a pas
- **THEN** la catégorie « Cadeaux » est créée, active, et la dépense y est classée

#### Scenario: Nom d'une catégorie existante

- **WHEN** un membre enregistre une dépense avec le nom « courses » alors que « Courses » existe
- **THEN** la dépense est classée dans « Courses » et aucune catégorie n'est créée

#### Scenario: Catégorie archivée réutilisée

- **WHEN** un membre enregistre une dépense avec le nom d'une catégorie archivée du foyer
- **THEN** la catégorie est réactivée et la dépense y est classée

#### Scenario: Saisie refusée

- **WHEN** un membre enregistre une dépense avec un nouveau nom de catégorie mais un montant invalide
- **THEN** la saisie est refusée avec une erreur `400 Bad Request` et aucune catégorie n'est créée

#### Scenario: Deux façons d'indiquer la catégorie

- **WHEN** un membre indique à la fois une catégorie existante et un nom de catégorie, ou aucun des deux
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Précision de la catégorie « Autre » dans la PWA

La PWA SHALL afficher un champ « Préciser (facultatif) » quand la catégorie « Autre » est choisie. Laissé vide, la dépense est classée dans « Autre » ; rempli, elle est classée dans la catégorie de ce nom, créée si besoin, qui est ensuite proposée comme les autres catégories.

#### Scenario: Précision saisie

- **WHEN** un membre choisit « Autre », tape « Cadeaux » et valide
- **THEN** la dépense est classée dans « Cadeaux », et « Cadeaux » est proposée lors de la saisie suivante

#### Scenario: Sans précision

- **WHEN** un membre choisit « Autre » sans rien préciser et valide
- **THEN** la dépense est classée dans « Autre »
