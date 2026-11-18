## Context

PWA Svelte 5 + Vite (`frontend/`). Styles actuels : un `app.css` global avec quelques variables, composants par écran (`budget/`, `shopping/`, `vehicle/`, `HouseholdTabs` en onglets en haut), formulaires affichés à la place de l'écran, confirmations par `confirm()` et `prompt()` du navigateur. Référence visuelle validée par Dorian : [maquette](https://claude.ai/artifact/UYLcEA5D9v4ksTNiRQihaY), 4 écrans mobiles.

## Goals / Non-Goals

**Goals:**
- Reproduire fidèlement la maquette sur mobile, et l'adapter proprement au grand écran.
- Des composants réutilisables pour les trois changes fonctionnels qui suivent.
- Ne rien changer aux parcours ni aux appels d'API.

**Non-Goals:**
- Une bibliothèque de composants externe complète (Material, Skeleton…) : trop lourde et trop éloignée de la maquette.

## Decisions

### 1. Jetons de thème

Variables CSS dans `app.css`, tirées de la maquette : `--color-primary` (#4F46E5), `--color-primary-soft` (#E0E7FF), `--color-success` (#10B981), `--color-warning` (#F97316), `--color-bg` (#F4F5FA), `--color-surface` (#FFFFFF), `--color-text` (#15172A), `--color-text-muted` (#5B5F78), `--color-border` (#E3E6F0), rayons (12, 14, 20, 24 px), ombres, espacements. Un bloc `@media (prefers-color-scheme: dark)` redéfinit les mêmes variables (fond #0F1020, surfaces #1A1C2E, primaire éclairci #818CF8). Les composants n'utilisent que des variables. Les couleurs par catégorie de dépense sont calculées depuis le nom (palette fixe de 8 teintes contrôlées en contraste).

### 2. Police et icônes

- `@fontsource-variable/plus-jakarta-sans` : la police est servie par l'application, donc disponible hors-ligne et sans appel à Google.
- `lucide-svelte` : icônes vectorielles cohérentes, importées une par une (le reste n'entre pas dans le bundle).

### 3. Composants (`frontend/src/lib/ui/`)

- `Modal.svelte` : basé sur l'élément natif `<dialog>` et `showModal()`, qui gère le focus et la touche Échap ; rendu en panneau qui monte du bas sous 900 px, centré au-delà ; renvoie le focus à l'ouvreur.
- `ConfirmDialog.svelte` : modale de confirmation (titre, détail, action destructrice) qui remplace `confirm()`, et `prompt()` pour le renommage.
- `BottomNav.svelte` / `SideNav.svelte` : la même liste de sections, choisie par une requête média.
- `Chip`, `Segmented`, `Card`, `Fab`, `AmountInput`, `ProgressBar`.
Tous accessibles : vrais `<button>`, `aria-pressed`, `aria-current`, `aria-label` sur les boutons sans texte.

### 4. Structure des écrans

`App.svelte` : en-tête (foyer, profil) + zone de contenu + navigation. `HouseholdTabs` devient le routeur des quatre sections. Chaque écran reprend la maquette ; les formulaires existants (`ExpenseForm`, `VehicleForm`, `MaintenanceForm`, `CreateOrJoin`, catégories) sont placés dans `Modal`, sans changer leur logique.

### 5. Vérification

`svelte-check` et Vitest pour la logique ; vérification visuelle dans le navigateur à 360, 390 et 1280 px, en thème clair et sombre ; contraste des couples de couleurs contrôlé par un petit test Vitest sur les jetons.

## Risks / Trade-offs

- [Régression d'un parcours en réorganisant les écrans] → les appels d'API ne changent pas ; parcours complet revérifié à deux comptes.
- [Thème sombre non dessiné dans la maquette] → dérivé des mêmes jetons ; à valider par Dorian lors de la vérification.
- [`<dialog>` et anciens navigateurs] → pris en charge par tous les navigateurs actuels visés (mobile et desktop récents).

## Migration Plan

Livraison front seule ; retour arrière par un simple retour au commit précédent de `frontend/`.
