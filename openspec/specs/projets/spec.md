# projets Specification

## Purpose
Mener les projets du foyer (travaux, vacances…) : un budget prévu face aux dépenses rattachées, un état, une option « budget à part », et une liste de tâches ordonnées dont les tâches datées sont rappelées.

## Requirements

### Requirement: Projets du foyer

Le système SHALL permettre à un membre d'ajouter, modifier et supprimer des projets pour son foyer, visibles par tous ses membres : un nom (1 à 60 caractères), une description facultative (500 caractères au plus), une couleur parmi une liste fixe, un budget prévu facultatif (en centimes, positif ou nul), une date de début et une date de fin visée facultatives (la fin ne précède pas le début), un état (en cours, terminé, abandonné) et une option « compte dans le budget du mois », cochée par défaut.

#### Scenario: Ajout

- **WHEN** un membre ajoute le projet « Salle de bain », couleur sarcelle, budget prévu 3 000 €
- **THEN** le projet est enregistré en cours, compté dans le budget, et visible par tous les membres du foyer

#### Scenario: Saisie invalide

- **WHEN** un membre ajoute un projet sans nom, avec une couleur inconnue, un budget négatif ou une fin visée avant le début
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Autre foyer

- **WHEN** un membre vise un projet inconnu ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

#### Scenario: Suppression

- **WHEN** un membre supprime un projet
- **THEN** le projet et ses tâches sont supprimés, et ses dépenses restent dans le budget sans projet

### Requirement: Tâches d'un projet

Le système SHALL permettre d'ajouter, modifier, ordonner, cocher, décocher et supprimer les tâches d'un projet : un titre (1 à 120 caractères), et en option un responsable (membre du foyer) et une date.

#### Scenario: Tâche avec responsable

- **WHEN** un membre ajoute « Choisir le carrelage », confiée à un autre membre du foyer, pour le 15 décembre
- **THEN** la tâche est enregistrée avec son responsable et sa date

#### Scenario: Responsable hors du foyer

- **WHEN** un membre confie une tâche à quelqu'un qui n'est pas membre du foyer
- **THEN** l'API répond `400 Bad Request`

#### Scenario: Départ du responsable

- **WHEN** le responsable d'une tâche quitte le foyer ou en est retiré
- **THEN** la tâche reste, sans responsable

#### Scenario: Avancement

- **WHEN** un projet a 7 tâches dont 3 faites
- **THEN** sa fiche et la liste des projets indiquent 3 / 7

### Requirement: Dépensé d'un projet

Le système SHALL montrer, pour chaque projet, la liste de ses dépenses (toutes dates) et leur total, face à son budget prévu s'il en a un.

#### Scenario: Total

- **WHEN** deux dépenses de 400 € et 250 € sont rattachées au projet « Salle de bain »
- **THEN** sa fiche affiche 650 € dépensés sur 3 000 € prévus
