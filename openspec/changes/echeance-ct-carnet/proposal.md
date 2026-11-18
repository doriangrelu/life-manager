## Why

Retour de Dorian : quand il saisit un contrôle technique dans le carnet, il doit ensuite aller modifier le véhicule à la main pour reporter la prochaine échéance. C'est un oubli facile, et c'est justement cette date qui alimente les rappels.

## What Changes

- **Saisie d'un contrôle technique** : le formulaire d'opération propose, quand le type « Contrôle technique » est choisi, le prochain contrôle :
  - **Automatique** (choix par défaut) : date du contrôle + 2 ans, la date calculée étant affichée avant validation ;
  - **Date précise** : saisie libre (contre-visite, véhicule de collection, règle particulière).
- **Mise à jour du véhicule** : à l'enregistrement, l'échéance « contrôle technique » du véhicule prend cette date, dans la même opération. Les rappels suivent comme pour une modification du véhicule.
- **Saisie d'un ancien contrôle** : si le carnet contient déjà un contrôle technique plus récent, l'échéance du véhicule n'est pas touchée (on complète l'historique sans reculer la date).

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `carnet-entretien` : un contrôle technique saisi met à jour l'échéance du prochain contrôle du véhicule.

## Impact

- Backend, domaine `vehicle` : champ facultatif `nextTechnicalInspectionDue` dans la saisie d'une opération ; `RecordMaintenance` met à jour le `Vehicle` dans la même transaction et publie `VehicleUpdated` (donc `VehicleDeadlinesChangedV1` pour les rappels). Aucune migration.
- PWA : choix « Automatique / Date précise » dans `MaintenanceForm` quand le type est « Contrôle technique » ; la fiche du véhicule affiche la nouvelle échéance après l'ajout.

## Hors périmètre

- Même mécanisme pour la révision (voir la question ouverte du design).
- Règle des 4 ans pour un véhicule neuf : elle ne concerne que le premier contrôle, saisi à la création du véhicule.
- Suppression d'un contrôle technique : l'échéance du véhicule n'est pas recalculée (modifiable à la main).
