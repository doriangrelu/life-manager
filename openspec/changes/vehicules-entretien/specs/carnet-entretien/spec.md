## Purpose

Garder l'historique des opérations d'entretien de chaque véhicule du foyer (date, type, kilométrage, coût, note), consultable par tous ses membres.

## ADDED Requirements

### Requirement: Ajout d'une opération d'entretien

Le système SHALL permettre à un membre d'ajouter une opération au carnet d'un véhicule de son foyer, avec : une date qui n'est pas dans le futur, un type parmi révision, vidange, contrôle technique, pneus, réparation et autre, et facultativement un kilométrage (0 à 2 000 000 km), un coût en centimes d'euro (0 à 100 000 €) et une note de 140 caractères au plus.

#### Scenario: Révision

- **WHEN** un membre ajoute une révision du 2026-10-12 à 45 000 km pour 189,90 € (18990 centimes)
- **THEN** l'opération est enregistrée avec ces valeurs

#### Scenario: Date future

- **WHEN** un membre ajoute une opération datée de demain
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Valeur hors bornes

- **WHEN** un membre ajoute une opération avec un kilométrage négatif, un coût négatif ou un type inconnu
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Véhicule d'un autre foyer

- **WHEN** un membre ajoute une opération à un véhicule inconnu ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Consultation du carnet

Le système SHALL permettre à un membre de consulter le carnet d'un véhicule de son foyer, l'opération la plus récente en premier.

#### Scenario: Historique

- **WHEN** un membre consulte le carnet d'un véhicule ayant une opération de 2025 et une de 2026
- **THEN** il obtient les deux opérations, celle de 2026 en premier

### Requirement: Suppression d'une opération

Le système SHALL permettre à un membre de supprimer définitivement une opération du carnet d'un véhicule de son foyer.

#### Scenario: Erreur de saisie

- **WHEN** un membre supprime une opération du carnet
- **THEN** elle n'apparaît plus dans le carnet

#### Scenario: Opération inconnue

- **WHEN** un membre supprime une opération inconnue ou d'un autre véhicule
- **THEN** l'API répond `404 Not Found`
