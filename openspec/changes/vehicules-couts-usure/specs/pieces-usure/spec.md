## ADDED Requirements

### Requirement: Kilométrage actuel et taille des pneus

Le système SHALL garder sur la fiche d'un véhicule un kilométrage actuel et une taille de pneus facultatifs ; une opération du carnet qui porte un kilométrage plus élevé met à jour le kilométrage actuel.

#### Scenario: Relevé par le carnet

- **WHEN** le véhicule est à 52 000 km et un membre ajoute une vidange à 54 300 km
- **THEN** le kilométrage actuel passe à 54 300 km

#### Scenario: Ancienne opération

- **WHEN** un membre ajoute une opération à 40 000 km
- **THEN** le kilométrage actuel ne change pas

### Requirement: Pièces d'usure suivies

Le système SHALL permettre de suivre les pièces d'usure d'un véhicule (pneus, plaquettes et disques avant et arrière, batterie, courroie de distribution), avec la date et le kilométrage de pose, un libellé et un intervalle de remplacement facultatifs, et SHALL montrer les kilomètres parcourus depuis la pose.

#### Scenario: Pneus montés

- **WHEN** des pneus avant posés à 40 000 km équipent un véhicule à 54 300 km
- **THEN** la fiche montre 14 300 km parcourus

#### Scenario: Changement

- **WHEN** un membre monte de nouveaux pneus avant à 60 000 km
- **THEN** les anciens passent dans l'historique avec 20 000 km faits et les nouveaux sont montés

#### Scenario: À changer

- **WHEN** des plaquettes à changer tous les 30 000 km ont fait 28 000 km
- **THEN** la PWA les signale « à prévoir », puis « à changer » à partir de 30 000 km

#### Scenario: Valeurs invalides

- **WHEN** un membre saisit un kilométrage de pose négatif, une date future ou un type inconnu
- **THEN** l'API répond `400 Bad Request`

#### Scenario: Véhicule d'un autre foyer

- **WHEN** un utilisateur consulte les pièces d'un véhicule d'un foyer dont il n'est pas membre
- **THEN** l'API répond `404 Not Found`
