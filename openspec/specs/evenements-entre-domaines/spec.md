# evenements-entre-domaines Specification

## Purpose
Garantir qu'un changement validé dans un domaine est connu des autres domaines qui en dépendent, même après un arrêt brutal ou une erreur passagère : événements enregistrés avec le changement, distribués au moins une fois et dans l'ordre, à des destinataires qui acceptent les doublons.

## Requirements

### Requirement: Événement enregistré avec le changement

Le système SHALL enregistrer chaque événement de contrat dans la même transaction que le changement métier qui le produit : si le changement est validé, l'événement est conservé ; s'il est annulé, l'événement n'existe pas.

#### Scenario: Changement annulé

- **WHEN** l'enregistrement d'une échéance payée échoue et est annulé
- **THEN** aucun événement de paiement n'est conservé ni distribué

### Requirement: Distribution garantie après un arrêt

Le système SHALL distribuer tout événement enregistré au moins une fois, y compris après un arrêt brutal du serveur ou une erreur d'un destinataire, dans l'ordre d'enregistrement pour un même domaine producteur. Un événement en échec SHALL être retenté, puis mis de côté et signalé après 10 échecs.

#### Scenario: Arrêt avant la distribution

- **WHEN** une échéance est payée puis le serveur s'arrête avant que le budget ne soit prévenu
- **THEN** au redémarrage la dépense apparaît dans le budget

#### Scenario: Destinataire momentanément en erreur

- **WHEN** un destinataire échoue sur un événement puis réussit à l'essai suivant
- **THEN** l'événement est traité une fois et les événements suivants du même domaine arrivent après lui

### Requirement: Destinataires idempotents

Chaque destinataire d'un événement de contrat SHALL obtenir le même résultat s'il reçoit le même événement plusieurs fois.

#### Scenario: Événement reçu deux fois

- **WHEN** l'événement de création d'un foyer est distribué deux fois
- **THEN** le foyer a une seule fois ses catégories et ses sous-listes par défaut
