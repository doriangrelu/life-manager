## ADDED Requirements

### Requirement: Publication des échéances

Le système SHALL publier aux autres domaines, après validation de la transaction, un événement de contrat à chaque ajout ou modification d'un véhicule (`VehicleDeadlinesChangedV1` : véhicule, foyer, nom, échéances de contrôle technique et de révision) et à chaque suppression (`VehicleRemovedV1` : véhicule, foyer).

#### Scenario: Modification d'échéance

- **WHEN** un membre modifie l'échéance de révision d'un véhicule
- **THEN** un événement `VehicleDeadlinesChangedV1` portant la nouvelle échéance est publié après validation

#### Scenario: Échec de la modification

- **WHEN** la modification d'un véhicule est refusée (nom invalide)
- **THEN** aucun événement n'est publié
