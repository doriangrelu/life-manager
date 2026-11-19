## ADDED Requirements

### Requirement: Historique des rappels faits

Le système SHALL garder une ligne d'historique à chaque « C'est fait » sur un rappel saisi : titre du rappel, date prévue, date à laquelle c'est fait et membre qui l'a fait. Il SHALL la montrer à tous les membres du foyer, de la plus récente à la plus ancienne, par pages de 20. Supprimer le rappel ne supprime pas son historique.

#### Scenario: C'est fait

- **WHEN** un membre touche « C'est fait » sur « Ramonage » prévu le 23 novembre
- **THEN** l'historique du foyer contient « Ramonage », prévu le 23 novembre, fait aujourd'hui par ce membre

#### Scenario: Rappel supprimé ensuite

- **WHEN** un membre supprime un rappel qui a déjà des lignes d'historique
- **THEN** ces lignes restent dans l'historique, avec leur titre

#### Scenario: Autre foyer

- **WHEN** un membre demande l'historique d'un foyer dont il ne fait pas partie
- **THEN** l'API répond `404 Not Found`

### Requirement: Retirer une ligne d'historique

Le système SHALL permettre à tout membre du foyer de retirer une ligne de l'historique, après confirmation dans la PWA.

#### Scenario: Retrait

- **WHEN** un membre retire une ligne de l'historique de son foyer
- **THEN** la ligne disparaît pour tout le foyer

#### Scenario: Ligne inconnue ou d'un autre foyer

- **WHEN** un membre vise une ligne inconnue ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Durée de l'historique

Le système SHALL effacer chaque jour les lignes d'historique faites il y a plus d'un an, et toutes celles d'un foyer supprimé.

#### Scenario: Ligne ancienne

- **WHEN** une ligne a été faite il y a plus d'un an
- **THEN** elle n'apparaît plus dans l'historique après le passage quotidien
