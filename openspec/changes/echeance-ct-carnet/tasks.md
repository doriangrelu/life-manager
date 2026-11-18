## 1. Backend

- [ ] 1.1 [vehicle/domain] Règle `TechnicalInspectionRule` (+ 2 ans, date précise postérieure au contrôle) ; vérifié par des tests unitaires (29 février compris)
- [ ] 1.2 [vehicle/application] `RecordMaintenance` : champ `nextTechnicalInspectionDue`, mise à jour du véhicule dans la même transaction sauf contrôle plus récent, publication de `VehicleUpdated` ; vérifié par des tests d'intégration reprenant les cinq scénarios de la spec (rappel compris)
- [ ] 1.3 [vehicle/infrastructure] Champ facultatif dans `MaintenanceRequest`, requête `existsLaterOfKind` ; vérifié par `./mvnw verify`

## 2. PWA

- [ ] 2.1 [frontend] `MaintenanceForm` : choix « Automatique / Date précise » pour un contrôle technique, aperçu de la date calculée ; fonction de calcul testée par Vitest
- [ ] 2.2 [frontend] Fiche du véhicule rechargée après l'ajout (nouvelle échéance visible) ; vérifié dans le navigateur, mobile et desktop

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
