## Context

Domaine `vehicle` : agrégats `Vehicle` (échéances `Deadlines` : contrôle technique, révision) et `MaintenanceEntry` (carnet). Toute modification d'un véhicule publie `VehicleUpdated`, relayé après commit en `VehicleDeadlinesChangedV1`, que le domaine `reminder` écoute. La saisie d'une opération (`RecordMaintenance`) ne touche aujourd'hui que le carnet.

## Goals / Non-Goals

**Goals:**
- Un seul geste pour enregistrer un contrôle technique et reporter la prochaine échéance.
- La règle de calcul vit dans le domaine (le serveur fait foi) ; la PWA affiche seulement un aperçu.
- Ne jamais reculer l'échéance en saisissant un ancien contrôle.

**Non-Goals:**
- Gérer les cas réglementaires particuliers (contre-visite, collection) autrement que par la date précise.

## Decisions

### 1. Saisie

`MaintenanceInput` reçoit un champ facultatif `nextTechnicalInspectionDue` (date) :
- absent ou `null` → **automatique** : `performedOn + 2 ans` ;
- renseigné → **date précise**, qui doit être postérieure à `performedOn` (sinon `400`).
- Le champ n'est accepté que pour le type `TECHNICAL_INSPECTION` (sinon `400`), pour éviter une échéance posée par erreur sur une vidange.

Calcul dans un value object du domaine, `TechnicalInspectionRule.nextDue(performedOn)` (une constante `VALIDITY = Period.ofYears(2)`), testé unitairement (29 février compris : 2024-02-29 → 2026-02-28, comportement de `LocalDate.plus`).

### 2. Mise à jour du véhicule

`RecordMaintenance` (même transaction) : après avoir enregistré l'opération, si c'est un contrôle technique **et** qu'aucun autre contrôle technique du carnet n'est plus récent, il appelle `vehicle.update(details avec la nouvelle échéance, clock)`, sauvegarde et publie les événements du véhicule.
- Deux agrégats modifiés dans une transaction : choix assumé (même domaine, même base, et l'utilisateur attend la nouvelle échéance dès la réponse). Alternative écartée : événement `MaintenanceRecorded` traité après commit, plus découplé mais avec un délai visible et un cas d'échec de plus.
- Port `MaintenanceEntryRepository` : ajout de `existsLaterOfKind(vehicleId, kind, date)`.

### 3. API et PWA

- `POST …/maintenance` : champ `nextTechnicalInspectionDue` facultatif dans le corps ; la réponse reste l'opération. La PWA recharge ensuite le véhicule (ou reçoit l'échéance via la liste des véhicules) pour afficher la nouvelle date.
- `MaintenanceForm` : quand le type « Contrôle technique » est choisi, un `Segmented` « Automatique / Date précise » ; en automatique, texte « Prochain contrôle : 18/11/2028 » recalculé quand la date change ; en date précise, un champ date (min = lendemain de l'opération).

## Risks / Trade-offs

- [Règle des 2 ans propre à la France pour les voitures particulières] → la date précise couvre les autres cas ; la durée est une constante unique, facile à rendre configurable plus tard.
- [Saisie d'un ancien contrôle, alors qu'un plus récent existe] → échéance non modifiée, conformément à la spec. La PWA n'annonce rien de particulier : elle recharge le véhicule et affiche son échéance réelle.

## Migration Plan

Aucune migration de données. Déploiement simple ; retour arrière : version précédente (le champ ajouté est facultatif).

## Open Questions

- Faut-il le même mécanisme pour la **révision** (par exemple + 1 an, ou une date précise) ? Proposé hors périmètre pour l'instant ; facile à ajouter avec le même modèle.

## Décisions prises sans Dorian

1. **Automatique = + 2 ans**, par défaut — règle française pour une voiture particulière de plus de 4 ans. Revenir en arrière : changer la constante.
2. **Ancien contrôle saisi = échéance inchangée** — on complète l'historique sans reculer la date. Alternative : toujours écraser.
3. **Même transaction pour l'opération et le véhicule** (voir décision 2).
