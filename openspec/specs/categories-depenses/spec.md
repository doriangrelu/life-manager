# categories-depenses Specification

## Purpose
Classer les dépenses d'un foyer dans des catégories : une liste prête à l'emploi à la création du foyer, que ses membres adaptent à leurs habitudes.

## Requirements

### Requirement: Catégories par défaut

Le système SHALL doter chaque nouveau foyer des catégories actives suivantes : Courses, Logement, Transport, Santé, Loisirs, Restaurants, Abonnements, Autre.

#### Scenario: Nouveau foyer

- **WHEN** un utilisateur crée un foyer puis consulte ses catégories de dépenses
- **THEN** il obtient les huit catégories par défaut, toutes actives

### Requirement: Liste des catégories

Le système SHALL permettre à un membre de lister les catégories de son foyer, triées par nom, en indiquant pour chacune si elle est archivée. La catégorie nommée « Autre », qui sert de dernier recours, SHALL toujours être placée en dernier.

#### Scenario: Consultation

- **WHEN** un membre consulte les catégories d'un foyer comportant une catégorie archivée
- **THEN** il obtient toutes les catégories, la catégorie archivée étant signalée comme telle

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte ses catégories
- **THEN** l'API répond `404 Not Found`

#### Scenario: « Autre » en dernier

- **WHEN** un membre consulte les catégories d'un foyer qui a « Abonnements », « Autre », « Courses » et « Transport »
- **THEN** il obtient « Abonnements », « Courses », « Transport », puis « Autre »

### Requirement: Ajout d'une catégorie

Le système SHALL permettre à un membre d'ajouter une catégorie à son foyer, avec un nom de 1 à 40 caractères, unique dans le foyer sans tenir compte des majuscules.

#### Scenario: Nouvelle catégorie

- **WHEN** un membre ajoute la catégorie « Animaux »
- **THEN** elle est créée, active, et utilisable pour les dépenses du foyer

#### Scenario: Nom déjà pris

- **WHEN** un membre ajoute « courses » alors que « Courses » existe dans le foyer
- **THEN** l'ajout est refusé avec une erreur `409 Conflict`

#### Scenario: Nom invalide

- **WHEN** un membre ajoute une catégorie au nom vide ou de plus de 40 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

### Requirement: Renommage d'une catégorie

Le système SHALL permettre à un membre de renommer une catégorie de son foyer, avec les mêmes règles de nom qu'à l'ajout ; les dépenses déjà classées dans cette catégorie y restent.

#### Scenario: Renommage

- **WHEN** un membre renomme « Loisirs » en « Sorties »
- **THEN** la catégorie s'appelle « Sorties » et ses dépenses existantes y sont toujours rattachées

### Requirement: Archivage d'une catégorie

Le système SHALL permettre à un membre d'archiver une catégorie de son foyer et de la réactiver. Une catégorie archivée n'est plus proposée pour de nouvelles dépenses, mais les dépenses existantes la conservent.

#### Scenario: Archivage

- **WHEN** un membre archive « Abonnements »
- **THEN** la catégorie est signalée archivée, n'est plus proposée à la saisie, et les dépenses existantes y restent rattachées

#### Scenario: Réactivation

- **WHEN** un membre réactive une catégorie archivée
- **THEN** elle est de nouveau utilisable pour les nouvelles dépenses
