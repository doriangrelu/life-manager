## 1. Infrastructure de développement

- [ ] 1.1 Créer `docker-compose.yml` avec PostgreSQL (bases `lifemanager` et `keycloak`) et Keycloak en mode dev
- [ ] 1.2 Configurer le realm `life-manager` : client public `life-manager-pwa` (PKCE, lightweight access token), client confidentiel `life-manager-api` (introspection), auto-inscription, mappers `email`/`name` ajoutés à l'introspection
- [ ] 1.3 Exporter le realm dans `infra/keycloak/realm-life-manager.json`, importé au démarrage ; documenter le lancement dans le `README.md`

## 2. Squelette backend Micronaut

- [ ] 2.1 Générer le projet `backend/` (Maven wrapper, Java 21, Micronaut 4) avec security, http-client, data-jdbc, flyway, postgres, serde, management (`/health`)
- [ ] 2.2 Implémenter le `TokenValidator` par introspection Keycloak (client confidentiel, contrôle `active` + audience, 401 si inactif, 503 si Keycloak injoignable) ; aucune dépendance `security-jwt`
- [ ] 2.3 Ajouter le cache d'introspection (30 s, clé = SHA-256 du jeton) + tests
- [ ] 2.4 Mettre en place Testcontainers (PostgreSQL) et un faux endpoint d'introspection pour les tests d'intégration
- [ ] 2.5 Vérifier que `./mvnw package -Dpackaging=native-image` produit un binaire qui démarre et répond sur `/health`

## 3. Comptes utilisateurs

- [ ] 3.1 Migration Flyway `V1` : tables `user_account`, `household`, `household_member` (n-n), `household_invitation`
- [ ] 3.2 Entité et repository `UserAccount` ; provisionnement JIT depuis les claims d'introspection
- [ ] 3.3 Endpoint `GET /api/whoami` (profil + foyers) + tests (sans jeton → 401, jeton inactif → 401, premier appel crée le profil, appel suivant met à jour)

## 4. Foyers

- [ ] 4.1 Entités et repositories `Household`, `HouseholdMember` ; composant `HouseholdAccess` (vérification d'appartenance, 404 sinon) + tests
- [ ] 4.2 `POST /api/households` (validation du nom, plafond de 10) et `GET /api/households` + tests
- [ ] 4.3 `GET /api/households/{householdId}` (nom, membres, code actif) + tests, dont non-membre → 404
- [ ] 4.4 Génération de code d'invitation (`SecureRandom`, alphabet sans ambiguïté, 7 jours, expiration du code précédent) et `POST /api/households/{householdId}/invitations` + tests
- [ ] 4.5 `POST /api/households/join` avec consommation transactionnelle du code, erreurs 400/409 + tests (code expiré, déjà utilisé, déjà membre, plafond atteint)
- [ ] 4.6 Limitation du nombre de tentatives sur `/join` par utilisateur + test
- [ ] 4.7 Test de cloisonnement : un utilisateur membre du seul foyer A ne voit rien du foyer B

## 5. PWA minimale

- [ ] 5.1 Initialiser `frontend/` (Vite, TypeScript, Svelte, `vite-plugin-pwa` avec manifest et icônes) et le proxy `/api` vers le backend
- [ ] 5.2 Intégrer `oidc-client-ts` : connexion (Authorization Code + PKCE), callback, renouvellement, déconnexion ; client HTTP ajoutant le jeton sans jamais le décoder
- [ ] 5.3 Chargement de `whoami` au démarrage, sélecteur de foyer, mémorisation du dernier foyer choisi sur l'appareil
- [ ] 5.4 Écran « Créer ou rejoindre un foyer » (formulaires création et code, affichage des erreurs)
- [ ] 5.5 Écran du foyer (nom, membres, génération et copie du code d'invitation)
- [ ] 5.6 Vérification manuelle du parcours complet à deux comptes et deux foyers, sur desktop et en affichage mobile

## 6. Intégration continue

- [ ] 6.1 Workflow GitHub Actions : tests backend (JVM), build natif, build de la PWA
