## Context

Cinq sections à plat aujourd'hui (Budget, Charges, Courses, Véhicules, Foyer) ; la feuille de route en ajoute au moins six (prévisionnel, graphiques, comptes entre membres, projets, documents, rappels). Sur un téléphone, au-delà de cinq onglets, les libellés deviennent illisibles.

## Goals / Non-Goals

**Goals** : une navigation qui tient pour les lots à venir ; les retouches demandées ; moins de code dupliqué côté PWA.
**Non-Goals** : refonte visuelle, nouveaux écrans métier.

## Decisions

### Navigation par espaces (PWA uniquement)

| Espace | Écrans | Plus tard |
|---|---|---|
| Argent (`Wallet`) | Budget, Charges | Prévisionnel, Comptes, Projets, Graphiques |
| Courses (`ListChecks`) | Courses (accès direct, usage en magasin) | — |
| Maison (`House`) | Véhicules | Documents, Rappels |
| Foyer (`Users`) | Foyer | — |

- **Mobile** : barre du bas à 4 entrées. Un espace qui a plusieurs écrans affiche sous l'en-tête une rangée de pastilles (sous-onglets), défilable si besoin. Toucher un espace rouvre son **dernier écran visité** (mémorisé sur l'appareil) ; toucher l'espace déjà ouvert revient à son premier écran.
- **Grand écran (≥ 900 px)** : menu latéral avec un titre par espace et toutes ses entrées visibles. Pas de pastilles.
- **Adresses inchangées** (`#/budget`, `#/charges`, `#/vehicules`…) : l'espace se déduit de la section. Une table `SPACES` (section → espace) dans `lib/navigation.ts` est la seule source ; un futur écran s'y ajoute en une ligne.
- Un espace à un seul écran (Courses, Foyer, et Maison pour l'instant) n'affiche pas de pastilles.

### Retours demandés

- **« Aujourd'hui »** : pastille dans la barre du mois du budget, visible seulement hors du mois courant.
- **Retour en haut** : utilitaire `scrollToTop(element)` qui remonte le conteneur défilant le plus proche (corps de modale ou page). Utilisé après une saisie enchaînée et à chaque changement d'écran.
- **Noms de personnes** : `personName()` (majuscule à chaque mot et après un trait d'union) appliqué à la sortie de l'API (`getHousehold`, `whoami`). Les noms d'affichage restent enregistrés tels quels (ils viennent de Keycloak). `lib/members.ts` regroupe « nom d'un membre, ou Commun » et les options payeur et bénéficiaire, aujourd'hui copiées dans quatre composants.

### Ordre des courses (domaine `shopping`, schéma `shopping`)

- Colonne `position INTEGER NOT NULL` sur `shopping_item` et `shopping_list` (migration `V4`) : valeurs initiales = ordre actuel (`added_at` pour les articles, `lower(name)` pour les sous-listes). Un nouvel élément prend `max + 1`, donc il va en fin de liste.
- La position n'est **pas** dans les agrégats (ce n'est pas un invariant métier) : le stockage la gère derrière les ports (`reorder`, création en fin de liste, mises à jour ciblées du nom ou du cochage). Use cases `ReorderShoppingItems` et `ReorderShoppingLists` : ils reçoivent la liste ordonnée des identifiants, vérifient qu'ils appartiennent tous à la sous-liste (ou au foyer), puis attribuent 0..n. Des identifiants inconnus donnent 404 ; un article manquant dans la liste garde sa place après les autres.
- API : `PUT /api/households/{h}/shopping-lists/order` et `PUT /api/households/{h}/shopping-lists/{l}/items/order`, corps `{ "ids": [...] }`, réponse `204`.
- Tri : articles non cochés par `position`, puis cochés du dernier au premier ; sous-listes par `position`.
- PWA : `svelte-dnd-action` (librairie connue : souris, tactile et clavier), avec une **poignée** pour ne pas gêner le défilement au doigt. Pendant un glisser, le rechargement automatique (toutes les 5 s) est suspendu ; l'ordre est envoyé au lâcher, puis la liste est rechargée.

### Atomic Design (PWA)

- `lib/ui/atoms` : briques sans logique métier (Chip, Skeleton, ProgressBar, AmountInput…).
- `lib/ui/molecules` : assemblages réutilisables (Segmented, Modal, ConfirmDialog, TextDialog, Fab, SubNav…).
- `lib/ui/organisms` : blocs complets d'écran (MainNav…).
- `lib/components/<section>` : écrans et blocs propres à une section.
- Règle écrite dans `docs/architecture.md` (section PWA) : une logique utilisée deux fois va dans `lib/` ; un élément visuel utilisé deux fois va dans `lib/ui`.

## Risks / Trade-offs

- Un clic de plus pour passer de Budget à Charges sur mobile → compensé par la mémoire du dernier écran par espace et par les pastilles visibles en haut.
- Deux membres qui réordonnent en même temps : le dernier enregistré gagne (acceptable pour un foyer).
- Déplacement des fichiers `lib/ui` : beaucoup d'imports modifiés, vérifiés par `svelte-check` et les tests.

## Migration Plan

Migration `shopping` additive. Pas de reprise côté PWA (la préférence « dernier écran » démarre vide). Retour arrière : version précédente, la colonne `position` est ignorée.
