## ADDED Requirements

### Requirement: Suivi des rentrées

Le propriétaire d'un foyer SHALL pouvoir activer ou désactiver le suivi des rentrées (bilan), désactivé par défaut et identique pour tous les membres. Tant qu'il est désactivé, la PWA SHALL se comporter exactement comme avant ce réglage. Désactiver le suivi SHALL ne supprimer aucune donnée.

#### Scenario: Par défaut

- **WHEN** un foyer existant ou nouveau n'a jamais touché ce réglage
- **THEN** le suivi des rentrées est désactivé et aucun écran ne change

#### Scenario: Activation

- **WHEN** le propriétaire active le suivi des rentrées
- **THEN** chaque membre voit les rentrées dans le Budget, les rentrées fixes dans les Charges et l'écran Bilan

#### Scenario: Membre non propriétaire

- **WHEN** un membre qui n'est pas propriétaire tente de changer le réglage
- **THEN** l'API répond `403 Forbidden`
