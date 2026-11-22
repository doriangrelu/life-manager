## ADDED Requirements

### Requirement: Tâches datées dans les rappels

Le système SHALL ajouter aux rappels du foyer chaque tâche de projet non faite, d'un projet en cours, dont la date est passée ou tombe dans les 7 jours, avec son titre, le nom du projet, sa date et le nombre de jours restants.

#### Scenario: Tâche proche

- **WHEN** la tâche « Choisir le carrelage » du projet « Salle de bain » tombe dans 3 jours et n'est pas faite
- **THEN** elle figure dans les rappels du foyer, à 3 jours, avec le nom du projet

#### Scenario: Tâche faite

- **WHEN** un membre coche cette tâche
- **THEN** elle ne figure plus dans les rappels du foyer

#### Scenario: Projet terminé

- **WHEN** le projet passe à l'état terminé ou abandonné
- **THEN** ses tâches ne figurent plus dans les rappels du foyer
