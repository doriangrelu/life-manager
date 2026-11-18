## Context

Domaine `shopping` (voir `openspec/specs/liste-courses`). Aujourd'hui : un agrégat `ShoppingItem` (libellé, auteur, date de cochage) rattaché directement au foyer, une copie locale des membres alimentée par `HouseholdCreatedV1` / `MemberJoinedV1`, un vidage des cochés qui supprime les lignes. Schéma `shopping` : tables `household_member` et `shopping_item`.

## Goals / Non-Goals

**Goals:**
- Des agrégats petits : une sous-liste ne contient pas ses articles (pas d'invariant commun, et deux membres cochent en même temps).
- Un historique compact : une ligne par libellé et par sous-liste.
- Reprise des données existantes sans perte.

**Non-Goals:**
- Garder les anciennes routes `/shopping-items` (la PWA est le seul client, elle est mise à jour dans le même change).

## Decisions

### 1. Agrégats

- **`ShoppingList`** (nouveau) : `ShoppingListId`, `HouseholdId`, `ListName` (1 à 30 caractères). Méthodes : `create`, `rename`, `defaultsFor(household)` (Courses, Bricolage, Vêtements). L'unicité du nom dans le foyer est vérifiée par le use case et garantie par un index unique `(household_id, lower(name))`, comme pour les catégories du budget.
- **`ShoppingItem`** (existant) : gagne un `ShoppingListId`. Ses invariants ne changent pas.
- **`PurchaseHistoryEntry`** (nouveau) : `PurchaseHistoryEntryId`, `ShoppingListId`, `ItemLabel`, date du dernier achat, nombre d'achats (≥ 1). Méthode `recordPurchase(clock)` qui incrémente et met à jour la date. Unicité `(list_id, lower(label))` garantie par un index.

### 2. Vidage des cochés → historique

`ClearCheckedItems` (use case existant, modifié) : pour chaque article coché de la sous-liste, trouve l'entrée d'historique du même libellé (sans tenir compte des majuscules) ou la crée, appelle `recordPurchase`, puis supprime l'article ; une seule transaction. Le libellé conservé est celui de la première fois (on ne renomme pas une entrée existante).

### 3. Remise en liste

`RestockFromHistory` : reçoit une liste d'identifiants d'entrées d'historique, vérifie qu'elles appartiennent toutes à la sous-liste (sinon `404` et rien n'est ajouté), puis crée un `ShoppingItem` par entrée. Les doublons avec des articles déjà présents sont autorisés, comme pour un ajout manuel (règle existante).

### 4. Sous-listes par défaut et reprise

- Sur `HouseholdCreatedV1`, l'écouteur existant crée aussi les trois sous-listes (`CreateDefaultShoppingLists`, idempotent).
- Migration Flyway `V2` du schéma `shopping` : tables `shopping_list` et `purchase_history`, colonne `shopping_item.list_id` ; pour chaque foyer connu de `household_member`, création des trois sous-listes par défaut ; les articles existants sont rattachés à « Courses » de leur foyer ; puis `list_id` passe `NOT NULL`. Fonction SQL `gen_random_uuid()` (native dans PostgreSQL 13+).

### 5. Services applicatifs

- `ShoppingAccess` (existant) : membre du foyer.
- `ShoppingListAccess` (nouveau) : charge une sous-liste du foyer pour un membre, sinon `404` — partagé par tous les use cases qui ciblent une sous-liste.

### 6. API

| Route | Use case |
|---|---|
| `GET` / `POST /shopping-lists` | lister (avec nombre d'articles à acheter) / ajouter |
| `PUT` / `DELETE /shopping-lists/{listId}` | renommer / supprimer |
| `GET` / `POST /shopping-lists/{listId}/items` | consulter / ajouter un article |
| `POST /shopping-lists/{listId}/items/{itemId}/check` et `/uncheck`, `DELETE …/items/{itemId}` | inchangés sur le fond |
| `POST /shopping-lists/{listId}/clear-checked` | vider les cochés vers l'historique |
| `GET /shopping-lists/{listId}/history` | historique |
| `POST /shopping-lists/{listId}/history/restock` (corps : identifiants) | remise en liste |
| `DELETE /shopping-lists/{listId}/history/{entryId}` | oubli |

### 7. PWA

Onglet Courses : rangée de sous-listes en pastilles (nom + nombre d'articles à acheter), sous-liste courante mémorisée sur l'appareil, bouton « Historique » qui ouvre l'écran de sélection multiple. Le rafraîchissement toutes les 5 s s'applique à la sous-liste affichée et aux compteurs.

## Risks / Trade-offs

- [Historique qui grossit indéfiniment] → une ligne par libellé et par sous-liste seulement ; l'oubli manuel permet de faire le ménage. Pas de purge automatique.
- [Suppression d'une sous-liste par erreur] → confirmation dans la PWA ; pas de corbeille (cohérent avec le reste de l'application).
- [Changement cassant de l'API] → la PWA et le backend sont livrés ensemble ; aucun autre client.

## Migration Plan

Migration `V2` automatique au démarrage (Flyway). Retour arrière : restaurer la sauvegarde ; la migration n'est pas réversible automatiquement.
