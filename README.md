# Life Manager

Application open source et auto-hébergée pour gérer le quotidien d'un foyer : budget, véhicules, liste de courses partagée.

- Vision produit : [docs/vision.md](docs/vision.md)
- Architecture backend : [docs/architecture.md](docs/architecture.md)
- Changements en cours et specs : [openspec/](openspec/)
- Mise en production sur le VPS : [docs/deploiement.md](docs/deploiement.md) ; déployer une nouvelle version pas à pas : [docs/modop-deploiement.md](docs/modop-deploiement.md)

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

### Lancer l'application

```bash
./infra/keycloak/create-dev-users.sh   # une fois : comptes alice@example.org et bob@example.org (mot de passe dev-password)
cd backend && ./mvnw mn:run             # API sur http://localhost:8080
cd frontend && bun install && bun run dev   # PWA sur http://localhost:5173
```

| Commande | Rôle |
|---|---|
| `./mvnw verify` (backend) | tests, Checkstyle, règles d'architecture ArchUnit |
| `./mvnw package -Dpackaging=native-image -Dmicronaut.aot.enabled=true` | binaire natif GraalVM |
| `bun run check` / `bun run build` (frontend) | vérification des types / build de la PWA |

### Services

| Service | URL | Accès de dev |
|---|---|---|
| Keycloak (admin) | http://localhost:8180 | `admin` / `admin` |
| Realm de l'application | http://localhost:8180/realms/life-manager | comptes créés par l'admin (pas d'inscription libre) |
| PostgreSQL | `localhost:5432`, base `lifemanager` | un compte par domaine : `identity_app`, `household_app` |

Le realm est défini dans [infra/keycloak/realm-life-manager.json](infra/keycloak/realm-life-manager.json) et importé au démarrage de Keycloak s'il n'existe pas encore.

**Repartir de zéro** (après un changement du realm, d'un script d'init PostgreSQL, ou quand un nouveau domaine dépend d'événements que les anciennes données n'ont jamais produits) : `docker compose down -v && docker compose up -d`, puis relancer `./infra/keycloak/create-dev-users.sh`. Toutes les données de dev sont effacées.

> Les secrets présents dans ce dépôt (mots de passe, secret du client `life-manager-api`) sont des valeurs **de développement uniquement**.
