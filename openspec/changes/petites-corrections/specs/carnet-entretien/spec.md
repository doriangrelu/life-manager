## ADDED Requirements

### Requirement: Précision du type « Autre »

Le système SHALL permettre de préciser une opération de type « Autre » par un libellé facultatif de 40 caractères au plus, affiché dans le carnet à la place de « Autre ». Une précision n'est acceptée que pour le type « Autre ».

#### Scenario: Opération précisée

- **WHEN** un membre ajoute une opération de type « Autre » précisée « Essuie-glaces »
- **THEN** l'opération est enregistrée avec cette précision et le carnet l'affiche « Essuie-glaces »

#### Scenario: Sans précision

- **WHEN** un membre ajoute une opération de type « Autre » sans précision
- **THEN** l'opération est enregistrée et le carnet l'affiche « Autre »

#### Scenario: Précision sur un autre type

- **WHEN** un membre ajoute une révision avec une précision
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Précision trop longue

- **WHEN** un membre ajoute une opération « Autre » avec une précision de plus de 40 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`
