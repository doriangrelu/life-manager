## ADDED Requirements

### Requirement: Page de connexion aux couleurs de l'application

La page de connexion SHALL porter le nom « Life Manager », le logo et les couleurs de la PWA, en français par défaut, et SHALL suivre le mode clair ou sombre du système. Elle SHALL rester utilisable sur mobile.

#### Scenario: Ouverture de la page de connexion

- **WHEN** un membre non connecté ouvre l'application
- **THEN** la page de connexion affiche le logo, le titre « Life Manager » et des textes en français, avec le bouton principal de la couleur de l'application

#### Scenario: Mode sombre

- **WHEN** le système du membre est en mode sombre
- **THEN** la page de connexion s'affiche avec les couleurs sombres de l'application
