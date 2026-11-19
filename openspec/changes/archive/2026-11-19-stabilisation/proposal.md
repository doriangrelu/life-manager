## Pourquoi

Treize versions ont été livrées en peu de temps (v0.4.0 à v0.13.0), et plusieurs recettes de Dorian sont encore en attente. Avant le lot 7 « Comptes entre membres », Dorian demande un **petit lot de stabilisation** : chercher les bugs, les corriger, et améliorer ou optimiser le code existant **si c'est utile**. Aucune nouvelle fonctionnalité.

Livré en **patch v0.13.1**, juste après la v0.13.0 (`donnees-personnelles`). Demandé par Dorian le 2026-11-19.

## Ce qui change

### 1. Chasse aux bugs (priorité)

Revue du code et essais au navigateur, écran par écran, sur mobile et sur bureau, en clair et en sombre : Budget, Récurrents, Bilan, Courses, Véhicules, Rappels, Foyer, Mon compte, CGU.

Points à regarder en premier :
- **cas limites** : changement de mois ou d'année, 29 février, fuseau (serveur en UTC, utilisateur en France), montants à 0 ou très grands, listes vides, textes très longs ;
- **droits** : membre et propriétaire, ancien membre, foyer supprimé pendant que l'écran est ouvert ;
- **interface** : chargements, doubles clics sur « Enregistrer », messages d'erreur, retour arrière, rechargement de la PWA après une mise à jour ;
- **données** : `ChargeScheduler` (échéances, fin des crédits), rattrapage des événements par la boîte d'envoi, purge de l'historique des rappels.

Chaque bug trouvé est d'abord couvert par un test qui échoue, puis corrigé.

### 2. Améliorations du code (seulement si ça en vaut la peine)

- **Gros composants Svelte** à découper s'ils mélangent plusieurs rôles : `ChargesView` (≈ 420 lignes), `BudgetView` (≈ 400), `ShoppingView`, `ChargeDetail`.
- **Doublons** entre formulaires (dépense, rentrée, récurrent) et entre domaines backend (les `HouseholdDataAdapter`, les sources d'export).
- **Performance** : requêtes faites dans une boucle côté backend, appels API en double au chargement d'un écran, rafraîchissement des courses toutes les 5 s (garder, mais vérifier qu'il s'arrête bien quand l'écran est quitté).
- **Robustesse du parcours natif** (`native-smoke.sh`) : il ne doit pas dépendre de l'état du compte de dev (échec constaté à 10 foyers).

### 3. Règles du lot

- Comportement inchangé pour l'utilisateur, sauf pour corriger un bug.
- Pas de changement de schéma de base, sauf si un bug l'exige (à signaler à Dorian avant).
- Chaque correction fait l'objet d'un commit court et clair. La liste des bugs corrigés sert de message de recette.

## Hors périmètre

- Toute nouvelle fonctionnalité, dont le lot 7, le lot 6b (e-mails) et le lot 5b (coût des véhicules).
- La réparation du HTTPS sortant de l'exécutable natif (prérequis du lot 6b, traitée avec lui).
- Le test de restauration sur une machine vierge (`deploiement-vps`, tâche 4.2).
- Les grosses refontes (changement de librairie, réécriture d'un domaine).

## Impact

- Backend et PWA, tous domaines, sans nouvelle route.
- Version **v0.13.1** (patch).
