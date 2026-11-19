## ADDED Requirements

### Requirement: Rappels saisis

Le système SHALL permettre à un membre d'ajouter, modifier et supprimer des rappels pour son foyer, visibles par tous ses membres : un titre (1 à 60 caractères), une date, une note facultative (280 caractères au plus), une récurrence (aucune, chaque mois, chaque trimestre, chaque année) et un délai pour être prévenu (le jour même, 1 semaine avant, 1 mois avant).

#### Scenario: Ajout

- **WHEN** un membre ajoute « Ramonage », chaque année, le 23 novembre, prévenu 1 semaine avant
- **THEN** le rappel est enregistré et visible par tous les membres du foyer

#### Scenario: Saisie invalide

- **WHEN** un membre ajoute un rappel sans titre, avec un titre de plus de 60 caractères, ou avec une récurrence ou un délai inconnu
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Autre foyer

- **WHEN** un membre vise un rappel inconnu ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Rappel saisi dans les rappels du foyer

Le système SHALL ajouter aux rappels du foyer chaque rappel saisi dont la date est passée ou tombe dans son délai, avec son titre, sa date et le nombre de jours restants, dans le même bandeau et les mêmes notifications que les autres rappels.

#### Scenario: Dans le délai

- **WHEN** un rappel prévenu 1 semaine avant tombe dans 5 jours
- **THEN** il figure dans les rappels du foyer, à 5 jours

#### Scenario: Hors du délai

- **WHEN** un rappel prévenu 1 semaine avant tombe dans 20 jours
- **THEN** il ne figure pas encore dans les rappels du foyer

### Requirement: C'est fait

Le système SHALL permettre de marquer un rappel saisi comme fait : un rappel ponctuel est supprimé, un rappel récurrent passe à sa date suivante, après sa date actuelle et après aujourd'hui, en gardant son jour du mois de départ.

#### Scenario: Rappel récurrent en retard

- **WHEN** un rappel mensuel est en retard de 3 jours et qu'un membre le marque comme fait
- **THEN** sa nouvelle date est celle du mois suivant

#### Scenario: Fin de mois

- **WHEN** un rappel mensuel parti du 31 janvier est fait le 31 janvier, puis le 28 février
- **THEN** il tombe le 28 février, puis de nouveau le 31 mars
