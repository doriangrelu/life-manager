# Life Manager

Application open source et auto-hébergée pour gérer le quotidien d'un foyer : budget, véhicules, liste de courses partagée.

- Vision produit : [docs/vision.md](docs/vision.md)
- Architecture backend : [docs/architecture.md](docs/architecture.md)
- Changements en cours et specs : [openspec/](openspec/)

## Environnement de développement

### Prérequis

- Docker (avec Docker Compose)
- VS Code et l'extension **Dev Containers** (recommandé)

### Démarrer

1. Ouvrir le dépôt dans VS Code, puis **« Reopen in Container »**.
   - PostgreSQL et Keycloak sont lancés automatiquement sur la machine hôte (`docker compose up -d`).
   - Le conteneur fournit Java 25 (GraalVM, avec `native-image`), Maven (via `./mvnw`) et Bun.
2. Sans devcontainer : `docker compose up -d` à la racine, puis utiliser son propre JDK 25 et Bun.

Pour surcharger les mots de passe de dev : copier `.env.example` en `.env`.

### Services

| Service | URL | Accès de dev |
|---|---|---|
| Keycloak (admin) | http://localhost:8180 | `admin` / `admin` |
| Realm de l'application | http://localhost:8180/realms/life-manager | comptes créés par l'admin (pas d'inscription libre) |
| PostgreSQL | `localhost:5432`, base `lifemanager` | un compte par domaine : `identity_app`, `household_app` |

Le realm est défini dans [infra/keycloak/realm-life-manager.json](infra/keycloak/realm-life-manager.json) et importé au démarrage de Keycloak s'il n'existe pas encore. Pour repartir de zéro : `docker compose down -v`.

> Les secrets présents dans ce dépôt (mots de passe, secret du client `life-manager-api`) sont des valeurs **de développement uniquement**.
