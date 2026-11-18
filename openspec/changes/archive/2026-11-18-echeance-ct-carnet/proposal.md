## Why

Retour de Dorian : quand il saisit un contrôle technique ou une révision dans le carnet, il doit ensuite modifier le véhicule à la main pour reporter la prochaine échéance. C'est un oubli facile, et ce sont justement ces dates qui alimentent les rappels.

## What Changes

- **Saisie d'un contrôle technique ou d'une révision** : le formulaire d'opération propose la prochaine échéance :
  - **Automatique** (choix par défaut) : date de l'opération + 2 ans pour un contrôle technique, + 1 an pour une révision, la date calculée étant affichée avant validation ;
  - **Date précise** : saisie libre (contre-visite, préconisation du garage…).
- **Mise à jour du véhicule** : à l'enregistrement, l'échéance correspondante du véhicule prend cette date, dans la même opération. Les rappels suivent comme pour une modification du véhicule.
- **Saisie d'une ancienne opération** : si le carnet contient déjà une opération du même type plus récente, l'échéance du véhicule n'est pas touchée (on complète l'historique sans reculer la date).

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `carnet-entretien` : un contrôle technique ou une révision saisi met à jour la prochaine échéance correspondante du véhicule.

## Impact

- Backend, domaine `vehicle` : champ facultatif `nextDueOn` dans la saisie d'une opération ; `RecordMaintenance` met à jour le `Vehicle` dans la même transaction et publie `VehicleUpdated` (donc `VehicleDeadlinesChangedV1` pour les rappels). Aucune migration.
- PWA : choix « Automatique / Date précise » dans `MaintenanceForm` pour ces deux types ; la fiche du véhicule affiche la nouvelle échéance après l'ajout.

## Hors périmètre

- Règle des 4 ans pour un véhicule neuf : elle ne concerne que le premier contrôle, saisi à la création du véhicule.
- Suppression d'une opération : l'échéance du véhicule n'est pas recalculée (modifiable à la main).
- Échéance au kilométrage (révision tous les 15 000 km…).
