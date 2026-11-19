## ADDED Requirements

### Requirement: Version affichée

La PWA SHALL afficher dans un pied de page discret sa version, et celle de l'API si elle est différente. L'API SHALL exposer sa version sans authentification, sans aucune autre information.

#### Scenario: Versions identiques

- **WHEN** la PWA et l'API déployées sont en v0.12.0
- **THEN** le pied de page affiche « v0.12.0 »

#### Scenario: Juste après une livraison

- **WHEN** le navigateur garde encore l'ancienne PWA alors que l'API est déjà en version suivante
- **THEN** le pied de page affiche les deux versions
