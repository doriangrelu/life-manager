## ADDED Requirements

### Requirement: Rentrées d'argent

Le système SHALL permettre à un membre d'enregistrer une rentrée d'argent pour son foyer : montant strictement positif en centimes, date qui n'est pas dans le futur, nature (salaire, prime, remboursement, aide, autre), libellé optionnel, et bénéficiaire (un membre du foyer, ou le foyer entier). Un membre SHALL pouvoir modifier et supprimer une rentrée de son foyer.

#### Scenario: Salaire

- **WHEN** un membre enregistre un salaire de 2 450 € reçu par lui le 2026-11-28
- **THEN** la rentrée apparaît dans le mois de novembre 2026, au nom de ce membre

#### Scenario: Montant invalide

- **WHEN** un membre enregistre une rentrée de 0 €
- **THEN** l'enregistrement est refusé avec une erreur `400 Bad Request`

### Requirement: Rentrées récurrentes

Le système SHALL permettre de déclarer une rentrée récurrente (ex. salaire mensuel) avec les mêmes règles de calendrier que les charges récurrentes : à chaque échéance, une rentrée à montant fixe SHALL être enregistrée seule, et une rentrée à montant variable SHALL attendre la confirmation de son montant. Une même échéance SHALL ne produire qu'une seule rentrée.

#### Scenario: Salaire fixe

- **WHEN** un salaire récurrent de 2 450 € a son échéance le 28 du mois
- **THEN** le 28, une rentrée de 2 450 € est enregistrée sans action du membre
