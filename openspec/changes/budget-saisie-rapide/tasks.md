## 1. Événements de contrat du foyer

- [x] 1.1 [household/contract + infrastructure/out/event] `HouseholdCreatedV1` et `MemberJoinedV1` publiés après commit par un relais ; vérifié par un test d'intégration qui crée un foyer, fait rejoindre un membre et reçoit les deux événements

## 2. Socle du domaine budget

- [x] 2.1 [infra] Schéma `budget` et compte `budget_app` dans `infra/postgres/init`, schéma ajouté à `db/test-init.sql` ; vérifié en recréant la base de dev (`docker compose down -v && docker compose up -d`) puis `\dn`
- [x] 2.2 [budget/infrastructure/out] Datasource et Flyway `budget`, migration `V1` (household_member, category, expense) ; ajout de `budget` à la liste des domaines d'ArchUnit ; vérifié par `./mvnw verify`
- [x] 2.3 [budget/domain] Value objects `HouseholdId`, `MemberId`, `CategoryId`, `CategoryName`, `ExpenseId`, `Money`, `ExpenseDate`, `ExpenseLabel`, `Beneficiary` ; vérifié par des tests unitaires des cas limites (0 €, 1 000 000 €, 41 caractères, date de demain)

## 3. Membres du foyer côté budget

- [x] 3.1 [budget/application + infrastructure] Port et table `household_member`, use case `RegisterHouseholdMember`, écouteur des événements du foyer, service `BudgetAccess` ; vérifié par un test d'intégration : un membre qui rejoint un foyer y a accès, un étranger reçoit 404

## 4. Catégories

- [x] 4.1 [budget/domain] Agrégat `Category` (création, renommage, archivage, réactivation) ; vérifié par des tests unitaires
- [x] 4.2 [budget/infrastructure/out] Repository, mapper et adaptateur des catégories, index unique sur le nom ; vérifié par le test d'intégration de 4.4
- [x] 4.3 [budget/application] `CreateDefaultCategories` déclenché par `HouseholdCreatedV1`, `ListCategories`, `AddCategory` ; vérifié par un test d'intégration (8 catégories par défaut, doublon « courses » → 409, nom vide → 400)
- [x] 4.4 [budget] `RenameCategory`, `ArchiveCategory`, `ReactivateCategory` et routes `/expense-categories` ; vérifié par un test d'intégration couvrant chaque route et le 404 non-membre

## 5. Dépenses

- [x] 5.1 [budget/domain] Agrégat `Expense` (saisie, correction, suppression) et ses événements ; vérifié par des tests unitaires
- [x] 5.2 [budget/infrastructure/out] Repository, mapper et adaptateur des dépenses ; vérifié par le test d'intégration de 5.4
- [x] 5.3 [budget/application] Service `ExpenseRules`, use cases `RecordExpense`, `CorrectExpense`, `DeleteExpense` ; vérifié par des tests d'intégration (catégorie archivée ou d'un autre foyer → 400, payeur étranger → 400, modification invalide sans effet)
- [x] 5.4 [budget/infrastructure/in/web] Routes `/expenses` (POST, PUT, DELETE) avec validation des DTO ; vérifié par un test d'intégration de bout en bout (saisie, correction, suppression, 404 sur dépense inconnue ou foyer étranger)

## 6. Vue mensuelle

- [x] 6.1 [budget/application + infrastructure/out] Port `MonthlyBudgetReadModel` (requêtes d'agrégation SQL) et use case `GetMonthlyBudget` ; vérifié par un test d'intégration reprenant les exemples chiffrés des specs (total, catégories triées, payeurs, bénéficiaires, mois vide)
- [x] 6.2 [budget/infrastructure/in/web] Route `GET /budget/{yyyy-MM}` (mois invalide → 400) ; vérifié par un test d'intégration

## 7. PWA

- [ ] 7.1 [frontend] Client d'API budget, conversion montant saisi ↔ centimes avec tests unitaires (Vitest : « 12,5 », « 12,50 », « 0,1 », saisies invalides) ; vérifié par `bun run test` et `bun run check`
- [ ] 7.2 [frontend] Onglets « Budget » / « Foyer » dans le foyer sélectionné ; vérifié dans le navigateur
- [ ] 7.3 [frontend] Formulaire de saisie rapide (valeurs pré-remplies, clavier numérique, confirmation, remise à zéro) ; vérifié dans le navigateur en affichage mobile
- [ ] 7.4 [frontend] Écran du mois (totaux, répartitions avec noms des membres, liste, mois précédent / suivant) ; vérifié dans le navigateur
- [ ] 7.5 [frontend] Modification et suppression d'une dépense depuis la liste ; vérifié dans le navigateur
- [ ] 7.6 [frontend] Gestion des catégories (ajout, renommage, archivage, réactivation) ; vérifié dans le navigateur

## 8. Intégration

- [ ] 8.1 [ci] Tests Vitest ajoutés au job PWA de la CI ; vérifié par un run CI vert
- [ ] 8.2 [doc] `docs/architecture.md` (domaine budget dans le tableau des domaines) et README (réinitialisation de la base de dev) à jour ; parcours complet à deux comptes vérifié dans le navigateur (saisie par l'un, visible par l'autre)

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
