# depenses Specification

## Purpose
Enregistrer les dépenses d'un foyer en quelques secondes, puis pouvoir les corriger ou les supprimer, en sachant qui a payé et pour qui.

## Requirements

### Requirement: Saisie d'une dépense

Le système SHALL permettre à un membre d'un foyer d'enregistrer une dépense de ce foyer avec un montant en euros (strictement positif, au centime près, au plus 1 000 000 €), une catégorie, une date (pas dans le futur), un payeur, un bénéficiaire et un libellé facultatif d'au plus 140 caractères.

#### Scenario: Dépense valide

- **WHEN** un membre enregistre 42,50 € en « Courses », datés d'aujourd'hui, payés par lui pour le foyer en commun
- **THEN** la dépense est enregistrée avec ces informations et l'auteur de la saisie

#### Scenario: Montant invalide

- **WHEN** un membre saisit un montant nul, négatif ou supérieur à 1 000 000 €
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

#### Scenario: Date dans le futur

- **WHEN** un membre saisit une dépense datée de demain
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Payeur et bénéficiaire

Le système SHALL exiger que le payeur soit un membre du foyer, et que le bénéficiaire soit soit un membre du foyer, soit « commun » (le foyer dans son ensemble).

#### Scenario: Dépense pour un membre précis

- **WHEN** un membre enregistre une dépense payée par Alice pour Bob, tous deux membres du foyer
- **THEN** la dépense est enregistrée avec Alice comme payeuse et Bob comme bénéficiaire

#### Scenario: Payeur étranger au foyer

- **WHEN** un membre indique comme payeur ou bénéficiaire une personne qui n'est pas membre du foyer
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Catégorie utilisable

Le système SHALL n'accepter, pour une nouvelle dépense ou une dépense modifiée, qu'une catégorie active appartenant au même foyer.

#### Scenario: Catégorie archivée

- **WHEN** un membre enregistre une dépense dans une catégorie archivée de son foyer
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

#### Scenario: Catégorie d'un autre foyer

- **WHEN** un membre enregistre une dépense avec une catégorie appartenant à un autre foyer
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Modification d'une dépense

Le système SHALL permettre à tout membre du foyer de modifier une dépense du foyer (montant, catégorie, date, payeur, bénéficiaire, libellé), avec les mêmes règles qu'à la saisie.

#### Scenario: Correction d'un montant

- **WHEN** un membre corrige le montant d'une dépense de 42,50 € à 24,50 €
- **THEN** la dépense vaut désormais 24,50 € et ses autres informations sont inchangées

#### Scenario: Modification invalide

- **WHEN** un membre modifie une dépense avec un montant négatif
- **THEN** la modification est refusée avec une erreur `400 Bad Request` et la dépense reste inchangée

### Requirement: Suppression d'une dépense

Le système SHALL permettre à tout membre du foyer de supprimer définitivement une dépense du foyer.

#### Scenario: Suppression

- **WHEN** un membre supprime une dépense
- **THEN** elle n'apparaît plus nulle part, y compris dans les totaux du mois

#### Scenario: Dépense inconnue

- **WHEN** un membre modifie ou supprime une dépense qui n'existe pas dans son foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Cloisonnement des dépenses

Le système SHALL réserver la consultation, la saisie, la modification et la suppression des dépenses d'un foyer à ses membres, et SHALL traiter tout autre accès comme visant une ressource inexistante.

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre d'un foyer tente de lire ou d'enregistrer une dépense de ce foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Saisie rapide dans la PWA

La PWA SHALL proposer un formulaire de saisie utilisable d'une main sur mobile, pré-rempli avec : payé par l'utilisateur connecté, pour « commun », date du jour. Le montant SHALL ouvrir le clavier numérique et accepter la virgule comme séparateur décimal.

#### Scenario: Saisie minimale

- **WHEN** un membre tape un montant, choisit une catégorie et valide
- **THEN** la dépense est enregistrée avec les valeurs pré-remplies, une confirmation s'affiche et le formulaire est prêt pour une nouvelle saisie

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
