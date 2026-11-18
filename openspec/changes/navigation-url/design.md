## Context

PWA Svelte 5 sans routeur : `HouseholdTabs` garde la section dans un `$state` (`'budget'` au départ), `BudgetView` le mois, `VehiclesView` le véhicule ouvert. La connexion OIDC (oidc-client-ts) revient sur `/` avec `?code=…&state=…`. Le foyer sélectionné et la sous-liste de courses sont déjà mémorisés dans `localStorage`.

## Goals / Non-Goals

**Goals:**
- F5 réaffiche le même écran ; Précédent / Suivant (et le bouton Retour d'Android) naviguent entre écrans.
- Aucune configuration serveur nécessaire (Vite en dev, Caddy en production).

**Non-Goals:**
- Liens partageables, modales dans l'adresse.

## Decisions

### 1. Adresses avec `#`

| Écran | Adresse |
|---|---|
| Budget, mois courant / un mois | `#/budget` · `#/budget/2026-10` |
| Courses | `#/courses` |
| Véhicules / un véhicule | `#/vehicules` · `#/vehicules/<id>` |
| Foyer | `#/foyer` |

Le `#` évite toute règle de réécriture côté serveur (le serveur ne voit jamais cette partie) et ne gêne pas le retour de connexion OIDC, qui utilise `?code=`. Alternative écartée : adresses « propres » (`/vehicules/…`) avec l'API History, qui demandent un renvoi vers `index.html` dans Caddy et une attention particulière au service worker. Une adresse inconnue renvoie vers `#/budget`.

### 2. Module de routage maison, très court

`lib/route.ts` : `parseRoute(hash) → Route` et `formatRoute(route) → hash` (fonctions pures, testées par Vitest), plus un état Svelte `route` alimenté par l'événement `hashchange`, et `navigate(route)` qui écrit `location.hash` (une entrée d'historique par changement d'écran ; `replace` pour corriger une adresse invalide).
Alternative écartée : une librairie de routage (`svelte-spa-router`…) : elle impose de découper l'application en composants de page, pour quatre écrans et deux paramètres. Le module reste remplaçable : les écrans ne dépendent que de `route` et `navigate`.

### 3. Branchements

- `HouseholdTabs` : la section vient de `route.section` ; `MainNav` appelle `navigate`.
- `BudgetView` : le mois vient de `route.month` (mois courant par défaut) ; changer de mois navigue.
- `VehiclesView` : le véhicule ouvert vient de `route.vehicleId` ; s'il n'existe pas dans le foyer (supprimé, autre foyer), retour à `#/vehicules` (`replace`).
- Changement de foyer : retour à la section courante, sans paramètre (le véhicule d'un foyer n'existe pas dans l'autre).

## Risks / Trade-offs

- [Une entrée d'historique par changement de mois] → acceptable ; c'est le comportement attendu de Précédent.
- [Modale ouverte puis F5] → la modale se ferme ; saisie perdue, comme aujourd'hui.

## Migration Plan

Aucune donnée. Les anciennes adresses (sans `#`) ouvrent Budget, comme aujourd'hui.

## Décisions

1. **Adresses avec `#`** plutôt que des adresses « propres » (voir décision 1).
2. **Module maison de quelques lignes** plutôt qu'une librairie de routage (voir décision 2), en écart assumé avec la règle « librairies connues » vu la taille du besoin : validé par Dorian.
3. **Le mois du budget fait partie de l'adresse** : F5 sur octobre reste sur octobre.
