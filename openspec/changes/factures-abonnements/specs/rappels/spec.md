## ADDED Requirements

### Requirement: Rappels des charges récurrentes

Le système SHALL ajouter aux rappels du foyer la prochaine échéance non payée de chaque charge active quand elle tombe dans les 7 jours, ainsi que toute échéance « à confirmer » tant qu'elle n'est pas confirmée ou sautée, avec le nom de la charge, la date et le nombre de jours restants. Ces rappels SHALL apparaître dans le même bandeau et les mêmes notifications que les rappels de véhicules.

#### Scenario: Échéance proche

- **WHEN** le 2026-12-01, la charge « Internet » a son échéance le 2026-12-05
- **THEN** le foyer a un rappel « Internet » dans 4 jours

#### Scenario: Échéance confirmée

- **WHEN** un membre confirme l'échéance à confirmer d'« Électricité »
- **THEN** son rappel disparaît
