# rappels Specification

## Purpose
Prévenir les membres d'un foyer des échéances de leurs véhicules et de leurs charges qui sont passées ou qui approchent, dans l'application et, s'ils le souhaitent, par une notification de leur appareil.

## Requirements

### Requirement: Rappels d'échéances

Le système SHALL fournir à un membre la liste des rappels de son foyer : chaque échéance de contrôle technique ou de révision d'un véhicule du foyer qui est passée (« en retard ») ou qui tombe dans les 30 jours (« bientôt »), avec le véhicule concerné, la date et le nombre de jours restants (négatif en cas de retard). Les rappels sont triés de la date la plus ancienne à la plus lointaine.

#### Scenario: Échéances proches et passées

- **WHEN** le 2026-11-16, un foyer a une Clio dont la révision était due le 2026-10-01 et le contrôle technique est dû le 2026-12-01, et un vélo dont la révision est due le 2027-06-01
- **THEN** le membre obtient deux rappels : la révision de la Clio en retard de 46 jours, puis le contrôle technique de la Clio dans 15 jours

#### Scenario: Aucune échéance proche

- **WHEN** aucune échéance du foyer n'est passée ni ne tombe dans les 30 jours
- **THEN** la liste des rappels est vide

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer demande ses rappels
- **THEN** l'API répond `404 Not Found`

### Requirement: Suivi des changements de véhicules

Le système SHALL tenir les rappels à jour quand un véhicule est ajouté, modifié ou supprimé, sans action supplémentaire des membres.

#### Scenario: Échéance repoussée

- **WHEN** un membre remplace une échéance de contrôle technique en retard par une date dans deux ans
- **THEN** le rappel de ce contrôle technique disparaît

#### Scenario: Véhicule supprimé

- **WHEN** un membre supprime un véhicule qui avait des rappels
- **THEN** ses rappels disparaissent

### Requirement: Affichage dans la PWA

La PWA SHALL afficher les rappels du foyer sélectionné dans un bandeau visible quel que soit l'onglet ouvert, et rien lorsqu'il n'y a aucun rappel.

#### Scenario: Ouverture de l'application

- **WHEN** un membre ouvre l'application alors que son foyer a un rappel « en retard »
- **THEN** le bandeau indique le véhicule, l'échéance et le retard

### Requirement: Notifications de l'appareil

La PWA SHALL proposer d'activer les notifications de l'appareil. Une fois activées, elle SHALL afficher une notification système pour chaque rappel à l'ouverture ou au retour sur l'application, au plus une fois par jour et par rappel sur un même appareil.

#### Scenario: Activation

- **WHEN** un membre active les notifications et que son foyer a un rappel
- **THEN** une notification système présente ce rappel

#### Scenario: Pas de répétition

- **WHEN** le membre rouvre l'application le même jour
- **THEN** aucune nouvelle notification n'est affichée pour ce rappel

### Requirement: Rappels des charges récurrentes

Le système SHALL ajouter aux rappels du foyer la prochaine échéance non payée de chaque charge active quand elle tombe dans les 7 jours, ainsi que toute échéance « à confirmer » tant qu'elle n'est pas confirmée ou sautée, avec le nom de la charge, la date et le nombre de jours restants. Ces rappels SHALL apparaître dans le même bandeau et les mêmes notifications que les rappels de véhicules.

#### Scenario: Échéance proche

- **WHEN** le 2026-12-01, la charge « Internet » a son échéance le 2026-12-05
- **THEN** le foyer a un rappel « Internet » dans 4 jours

#### Scenario: Échéance confirmée

- **WHEN** un membre confirme l'échéance à confirmer d'« Électricité »
- **THEN** son rappel disparaît

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
