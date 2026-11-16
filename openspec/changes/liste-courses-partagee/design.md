## Context

Le foyer publie `HouseholdCreatedV1` et `MemberJoinedV1` après commit ; budget et véhicule en tiennent chacun une copie locale. La liste de courses suit le même modèle (`docs/architecture.md`). La vision demande une synchro « en temps réel » entre deux personnes ; `docs/vision.md` laisse ouverte la question du hors-ligne.

## Goals / Non-Goals

**Goals:**
- Un domaine `shopping` autonome.
- Des gestes rapides : un appel par ajout ou par cochage.
- Une liste à jour chez l'autre membre en quelques secondes, sans infrastructure supplémentaire.

**Non-Goals:**
- Hors-ligne, WebSocket/SSE, plusieurs listes.

## Decisions

### 1. Domaine `shopping`, schéma `shopping`

Package de premier niveau `shopping`, datasource `shopping` (compte PostgreSQL `shopping_app`), migrations Flyway dans `db/migration/shopping`. Aucune clé étrangère vers un autre schéma.

### 2. Agrégat `ShoppingItem`

Un article = un agrégat : le seul invariant porte sur l'article lui-même (libellé valide, un seul foyer). Une « liste » agrégat unique obligerait à charger et verrouiller toute la liste à chaque cochage, et deux membres cochant en même temps se gêneraient.
- Value objects : `ShoppingItemId`, `HouseholdId`, `MemberId`, `ItemLabel` (1 à 60 caractères).
- État : `checkedAt` (instant, `null` si non coché), `addedBy`, `addedAt`.
- Méthodes : `add`, `check(clock)` et `uncheck()` (idempotents), suppression par le repository.
- Pas d'événement de domaine : aucun consommateur prévu (à ajouter si les rappels en ont besoin).

### 3. Membres du foyer

Comme budget et véhicule : `HouseholdEventsListener`, use case `RegisterHouseholdMember`, table `shopping.household_member` à insertion idempotente.

### 4. Services et use cases

- `ShoppingAccess` : appartenance au foyer (sinon `404`) et chargement d'un article du foyer (sinon `404`).
- Use cases : `ListShoppingItems`, `AddShoppingItem`, `CheckShoppingItem`, `UncheckShoppingItem`, `RemoveShoppingItem`, `ClearCheckedItems`, `RegisterHouseholdMember`.
- `ClearCheckedItems` supprime en une requête SQL (`DELETE … WHERE checked_at IS NOT NULL`) : aucune règle d'article n'est en jeu, charger chaque agrégat n'apporterait rien.

### 5. Base de données (schéma `shopping`)

- `household_member(id, household_id, member_id, UNIQUE (household_id, member_id))`
- `shopping_item(id, household_id, label VARCHAR(60), checked_at TIMESTAMPTZ NULL, added_by, added_at)` + index `(household_id)`

### 6. API (`/api/households/{householdId}/shopping-items`)

| Route | Use case |
|---|---|
| `GET` / `POST /shopping-items` | lister / ajouter |
| `POST /shopping-items/{id}/check` et `/uncheck` | cocher / décocher |
| `DELETE /shopping-items/{id}` | supprimer |
| `POST /shopping-items/clear-checked` | vider les cochés (`204`) |

Réponse d'un article : `id`, `label`, `checked` (booléen), `addedAt`.

### 7. PWA et synchronisation

Onglet « Courses ». Le composant recharge la liste toutes les 5 s (`setInterval`) tant que `document.visibilityState === 'visible'`, et au retour de visibilité (`visibilitychange`). Cochage optimiste : la case change tout de suite, la réponse de l'API fait foi au rechargement suivant. Le rafraîchissement ne remplace pas la liste pendant qu'une action de l'utilisateur est en cours, pour éviter un clignotement.

Coût : une requête légère toutes les 5 s par écran ouvert, soit quelques milliers par jour au plus pour un foyer : négligeable pour le VPS ; le cache d'introspection (30 s) évite de solliciter Keycloak à chaque appel.

## Risks / Trade-offs

- **Pas de temps réel strict** → jusqu'à 5 s de décalage ; acceptable pour deux personnes qui font les courses.
- **Pas de hors-ligne** → en magasin sans réseau, la liste affichée reste visible mais les actions échouent avec un message ; à traiter plus tard (file d'attente locale).
- **Conflit cochage / suppression simultanés** → le second geste reçoit `404` puis la liste se recharge ; acceptable.

## Migration Plan

Nouveau schéma et compte PostgreSQL dans le script d'init de dev et `db/test-init.sql` ; migration Flyway `V1` du schéma `shopping`. Base de dev à recréer.

## Décisions prises sans Dorian

1. **Synchronisation par rafraîchissement périodique (5 s) plutôt que SSE ou WebSocket** — alternatives : SSE (`EventSource`), WebSocket, notifications push. Raison : aucune infrastructure à ajouter ; `EventSource` ne sait pas envoyer l'en-tête `Authorization` exigé par nos jetons opaques (il faudrait un jeton dans l'URL ou un cookie) ; le volume de requêtes est négligeable pour un foyer. Revenir en arrière : ajouter un endpoint SSE alimenté par des événements de domaine `ShoppingItemChanged`, et remplacer le `setInterval` de la PWA par un `EventSource` ; l'API REST reste inchangée.
2. **Pas de mode hors-ligne** — alternative : file d'attente locale (IndexedDB) rejouée au retour du réseau. Raison : complexité (conflits, service worker) disproportionnée pour le MVP. Revenir en arrière : ajouter la file côté PWA ; les routes idempotentes (`check`, `uncheck`) s'y prêtent déjà.
3. **Une seule liste par foyer** — alternative : plusieurs listes nommées. Raison : besoin décrit dans la vision. Revenir en arrière : ajouter un agrégat `ShoppingList` et une colonne `list_id`.
4. **Libellé libre, quantité incluse dans le texte, doublons autorisés** — alternatives : quantité structurée, fusion des doublons. Raison : saisie la plus rapide possible. Revenir en arrière : ajouter un champ facultatif.
5. **Un agrégat par article** — alternative : agrégat « liste » contenant les articles. Raison : pas d'invariant commun à la liste ; évite les conflits de concurrence entre deux membres. Revenir en arrière : sans objet.
