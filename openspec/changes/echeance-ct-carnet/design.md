## Context

Domaine `vehicle` : agrégats `Vehicle` (échéances `Deadlines` : contrôle technique, révision) et `MaintenanceEntry` (carnet). Toute modification d'un véhicule publie `VehicleUpdated`, relayé après commit en `VehicleDeadlinesChangedV1`, que le domaine `reminder` écoute. La saisie d'une opération (`RecordMaintenance`) ne touchait que le carnet.

## Goals / Non-Goals

**Goals:**
- Un seul geste pour enregistrer un contrôle technique ou une révision et reporter la prochaine échéance.
- La règle de calcul vit dans le domaine (le serveur fait foi) ; la PWA affiche seulement un aperçu.
- Ne jamais reculer l'échéance en saisissant une ancienne opération.

**Non-Goals:**
- Cas réglementaires particuliers (contre-visite, collection) autrement que par la date précise.

## Decisions

### 1. Saisie

`MaintenanceInput` reçoit un champ facultatif `nextDueOn` (date) :
- type contrôle technique ou révision, champ absent → **automatique** : `performedOn + 2 ans` (contrôle technique) ou `+ 1 an` (révision) ;
- champ renseigné → **date précise**, postérieure à `performedOn` (sinon `400`) ;
- autres types : le champ doit être absent (sinon `400`), pour éviter une échéance posée par erreur sur une vidange.

Règle dans le domaine : `MaintenanceKind.validity()` renvoie `Optional<Period>` (2 ans, 1 an, vide pour les autres) et un value object `NextDeadline` calcule ou vérifie la date. Testé unitairement (29 février compris : 2024-02-29 + 2 ans → 2026-02-28, comportement de `LocalDate.plus`).

### 2. Mise à jour du véhicule

`RecordMaintenance` (même transaction) : après avoir enregistré l'opération, si son type porte une échéance **et** qu'aucune opération du même type n'est plus récente au carnet, il remplace l'échéance correspondante du véhicule (`Deadlines.withTechnicalInspection` / `withService`), appelle `vehicle.update(…)`, sauvegarde et publie les événements du véhicule.
- Deux agrégats modifiés dans une transaction : choix assumé (même domaine, même base, et l'utilisateur attend la nouvelle échéance dès la réponse). Alternative écartée : événement `MaintenanceRecorded` traité après commit, plus découplé mais avec un délai visible et un cas d'échec de plus.
- Port `MaintenanceEntryRepository` : ajout de `existsLaterOfKind(vehicleId, kind, date)`.

### 3. API et PWA

- `POST …/maintenance` : champ `nextDueOn` facultatif ; la réponse reste l'opération. La PWA recharge ensuite le véhicule pour afficher la nouvelle échéance.
- `MaintenanceForm` : pour un contrôle technique ou une révision, un `Segmented` « Automatique / Date précise » ; en automatique, texte « Prochain contrôle technique : 18/11/2028 » recalculé quand la date change ; en date précise, un champ date (min = lendemain de l'opération).

## Risks / Trade-offs

- [Règles propres à la France / au constructeur] → la date précise couvre les autres cas ; les durées sont deux constantes du domaine.
- [Saisie d'une ancienne opération alors qu'une plus récente existe] → échéance non modifiée. La PWA recharge le véhicule et affiche son échéance réelle.

## Migration Plan

Aucune migration de données. Retour arrière : version précédente (le champ ajouté est facultatif).

## Décisions

1. **Automatique = + 2 ans (contrôle technique), + 1 an (révision)**, validé par Dorian pour la révision.
2. **Ancienne opération saisie = échéance inchangée** — on complète l'historique sans reculer la date.
3. **Même transaction pour l'opération et le véhicule** (voir décision 2).
