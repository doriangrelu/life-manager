## Why

Les domaines se préviennent par des événements de contrat publiés en mémoire **juste après** la validation de la transaction. Un arrêt brutal du serveur entre les deux perd l'événement sans bruit : échéance payée sans dépense au budget, véhicule modifié sans rappel à jour, membre inconnu d'un domaine. C'est la dette technique « événements sans outbox » de la feuille de route, à traiter maintenant que le lot 1 a ajouté des événements.

## What Changes

- **Boîte d'envoi (outbox)** dans chaque domaine qui publie (`identity`, `household`, `vehicle`, `bills`) : l'événement de contrat est enregistré dans une table du domaine **dans la même transaction** que le changement métier.
- **Facteur** : distribue les événements enregistrés aux autres domaines, aussitôt après la validation, puis toutes les 5 secondes et au démarrage pour ceux qui restent (plantage, erreur). Ordre d'enregistrement respecté dans un domaine.
- **Erreurs** : un événement refusé par un destinataire est retenté ; après 10 échecs il est mis de côté et signalé dans les journaux, sans bloquer les suivants.
- **Ménage** : les événements distribués sont supprimés après 7 jours.
- Les destinataires acceptent déjà un événement reçu deux fois (vérifié) ; c'est désormais une règle.

Rien de visible dans l'application.

## Capabilities

### New Capabilities

- `evenements-entre-domaines` : livraison garantie des événements de contrat, au moins une fois, dans l'ordre.

### Modified Capabilities

Aucune.

## Impact

- `kernel` : boîte d'envoi et facteur communs.
- `identity`, `household`, `vehicle`, `bills` : une table `outbox` par schéma (migration additive), relais d'événements branchés sur la boîte d'envoi.
- Aucun changement d'API ni de PWA ; aucune action manuelle sur le VPS.

## Hors périmètre

- Broker externe (Kafka, RabbitMQ…) : la boîte d'envoi en est le point de branchement futur.
- Écran d'administration des événements mis de côté (journaux uniquement).
- Plusieurs instances de l'API en parallèle (un seul serveur aujourd'hui).
