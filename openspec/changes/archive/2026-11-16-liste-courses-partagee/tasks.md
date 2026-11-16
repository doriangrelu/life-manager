## 1. Socle du domaine shopping

- [x] 1.1 [infra] Schéma `shopping` et compte `shopping_app` dans `infra/postgres/init`, `docker-compose.yml`, `.env.example` et `db/test-init.sql` ; vérifié en recréant la base de dev
- [x] 1.2 [shopping/infrastructure/out] Datasource et Flyway `shopping`, migration `V1` (household_member, shopping_item) ; `shopping` ajouté aux domaines d'ArchUnit et des tests d'intégration ; vérifié par `./mvnw verify`
- [x] 1.3 [shopping/domain] Value objects (`ShoppingItemId`, `HouseholdId`, `MemberId`, `ItemLabel`) et agrégat `ShoppingItem` (ajout, cochage, décochage idempotents) ; vérifié par des tests unitaires

## 2. Membres et accès

- [x] 2.1 [shopping/application + infrastructure] Port et table `household_member`, use case `RegisterHouseholdMember`, écouteur des événements du foyer, service `ShoppingAccess` ; vérifié par le test d'intégration de 3.2

## 3. Liste

- [x] 3.1 [shopping/infrastructure/out] Repository, mapper et adaptateur des articles (tri, suppression des cochés) ; vérifié par le test de 3.2
- [x] 3.2 [shopping/application + infrastructure/in/web] `ListShoppingItems`, `AddShoppingItem`, `CheckShoppingItem`, `UncheckShoppingItem`, `RemoveShoppingItem`, `ClearCheckedItems` et routes `/shopping-items` ; vérifié par un test d'intégration (ordre, partage, cochage idempotent, vidage, 400 et 404)

## 4. PWA

- [x] 4.1 [frontend] Client d'API courses, types, tri local avec tests Vitest ; vérifié par `bun run check` et `bun run test`
- [x] 4.2 [frontend] Onglet « Courses » : ajout, cochage optimiste, suppression, vidage des cochés ; vérifié dans le navigateur en affichage mobile
- [x] 4.3 [frontend] Rafraîchissement toutes les 5 s quand l'écran est visible et au retour de visibilité ; vérifié avec deux comptes

## 5. Intégration

- [x] 5.1 [doc] `docs/architecture.md` (domaine shopping) à jour ; parcours à deux comptes vérifié de bout en bout
