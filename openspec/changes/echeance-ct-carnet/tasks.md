## 1. Backend

- [x] 1.1 [vehicle/domain] `MaintenanceKind.validity()` (2 ans, 1 an), value object `NextDeadline` (calcul automatique, date précise postérieure), `Deadlines.with…` ; vérifié par des tests unitaires (29 février compris)
- [x] 1.2 [vehicle/application] `RecordMaintenance` : champ `nextDueOn`, mise à jour du véhicule dans la même transaction sauf opération plus récente du même type, publication de `VehicleUpdated` ; vérifié par des tests d'intégration reprenant les six scénarios de la spec (rappel compris)
- [x] 1.3 [vehicle/infrastructure] Champ facultatif dans `MaintenanceRequest`, requête `existsLaterOfKind` ; vérifié par `./mvnw verify`

## 2. PWA

- [ ] 2.1 [frontend] `MaintenanceForm` : choix « Automatique / Date précise » pour un contrôle technique ou une révision, aperçu de la date calculée ; fonction de calcul testée par Vitest
- [ ] 2.2 [frontend] Fiche du véhicule rechargée après l'ajout (nouvelle échéance visible) ; vérifié dans le navigateur, mobile et desktop

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
