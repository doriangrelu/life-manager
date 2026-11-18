## Purpose

Héberger Life Manager sur le socle partagé du VPS de façon sûre et réversible : accès uniquement en HTTPS, secrets hors du dépôt, données sauvegardées et restaurables, mises à jour maîtrisées, sans perturber les autres applications du serveur.

## ADDED Requirements

### Requirement: Accès en HTTPS uniquement

Le système SHALL être servi en production uniquement en HTTPS, avec un certificat valide renouvelé automatiquement ; une requête HTTP SHALL être redirigée vers HTTPS. Le backend, la PWA et PostgreSQL SHALL ne pas être accessibles depuis Internet autrement que par le reverse proxy.

#### Scenario: Accès en clair

- **WHEN** un navigateur ouvre l'application en `http://`
- **THEN** il est redirigé vers l'adresse `https://` équivalente

#### Scenario: Services non exposés

- **WHEN** on tente une connexion depuis Internet au port 5432 du VPS ou directement au port du backend
- **THEN** la connexion est refusée

### Requirement: Cohabitation avec le socle

Le déploiement de Life Manager SHALL réutiliser le reverse proxy, le PostgreSQL et le Keycloak déjà en service sur le VPS, et SHALL ne ni arrêter, ni redémarrer, ni modifier les données des autres applications.

#### Scenario: Premier déploiement

- **WHEN** Life Manager est installé puis déployé sur le VPS
- **THEN** les autres sites et Keycloak restent joignables pendant et après l'opération, et leurs bases, realms et routages sont inchangés

### Requirement: Secrets hors du dépôt

Les secrets de production de Life Manager (mots de passe des comptes PostgreSQL, secret du client `life-manager-api`) SHALL n'exister que sur le serveur, générés à l'installation, et jamais dans le dépôt ni dans les images.

#### Scenario: Installation

- **WHEN** le script d'installation est lancé sur le VPS
- **THEN** il génère des secrets aléatoires dans un fichier `.env` lisible seulement par son propriétaire, sans réutiliser les valeurs de développement

### Requirement: Sauvegardes

Le système SHALL sauvegarder chaque jour la base de Life Manager, son realm Keycloak et les fichiers des pièces jointes. Une procédure de restauration documentée SHALL permettre de reconstruire Life Manager à partir d'une sauvegarde.

#### Scenario: Sauvegarde quotidienne

- **WHEN** la sauvegarde nocturne du serveur s'exécute
- **THEN** l'archive produite contient la base `lifemanager`, le realm `life-manager` et les pièces jointes

#### Scenario: Restauration

- **WHEN** on restaure la dernière sauvegarde sur un serveur vierge en suivant la procédure
- **THEN** les comptes, foyers, dépenses, véhicules, pièces jointes, listes et rappels sont de nouveau accessibles

### Requirement: Mise à jour et retour arrière

Le système SHALL être mis à jour en déployant les images d'un tag de version publié, et SHALL pouvoir revenir au tag précédent avec la même commande. Les migrations Flyway SHALL s'exécuter au démarrage du backend.

#### Scenario: Nouvelle version

- **WHEN** Dorian lance le déploiement d'un tag publié
- **THEN** les nouvelles images sont tirées, les services redémarrés, et le contrôle de santé du backend répond `UP`

#### Scenario: Retour arrière

- **WHEN** la nouvelle version ne démarre pas
- **THEN** relancer le déploiement avec le tag précédent rétablit le service
