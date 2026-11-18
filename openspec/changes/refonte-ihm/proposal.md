## Why

Retour de Dorian après essai : l'interface fonctionne mais paraît peu moderne, manque de fenêtres modales et ses couleurs sont tristes. Une maquette de 4 écrans mobiles a été validée (« Les maquettes sont parfaites ») : [Life Manager — refonte IHM](https://claude.ai/artifact/UYLcEA5D9v4ksTNiRQihaY). Ce change l'applique à toute la PWA, avant les trois changes fonctionnels en attente qui réutiliseront ses composants.

## What Changes

- **Thème** : palette de la maquette (indigo principal, accents vert et orange, fond gris bleuté), police Plus Jakarta Sans embarquée dans l'application, coins arrondis, cartes ; thème sombre dérivé ; contrastes conformes WCAG AA.
- **Navigation** : barre d'onglets en bas sur mobile (Budget, Courses, Véhicules, Foyer), menu latéral sur grand écran ; en-tête avec le sélecteur de foyer et le profil.
- **Fenêtres modales** : tous les formulaires de saisie et de modification (dépense, catégorie, véhicule, opération d'entretien, création ou adhésion à un foyer) s'ouvrent dans une fenêtre modale : panneau qui monte du bas sur mobile, fenêtre centrée sur grand écran. Les confirmations de suppression utilisent aussi une modale au lieu de la boîte native du navigateur.
- **Écrans** : budget (carte du total, barres par catégorie, dernières dépenses, bouton d'ajout flottant), courses, véhicules (échéances en cartes colorées selon l'urgence) et foyer redessinés selon la maquette.
- **Bibliothèque de composants** interne à la PWA (modale, onglets, puces, sélecteur segmenté, carte, bouton flottant) et icônes d'une bibliothèque reconnue.

## Capabilities

### New Capabilities

- `interface-pwa` : règles transverses de l'interface (navigation, fenêtres modales, thème, accessibilité, affichage mobile et grand écran).

### Modified Capabilities

_Aucune : les parcours fonctionnels décrits dans les autres specs ne changent pas._

## Impact

- `frontend/` uniquement : styles globaux, nouveaux composants, tous les écrans existants.
- Nouvelles dépendances front : `@fontsource-variable/plus-jakarta-sans` (police servie par l'application, sans appel à Google), `lucide-svelte` (icônes).
- Aucun changement backend ni d'API.

## Hors périmètre

- Nouvelles fonctionnalités : elles arrivent avec `categorie-libre-depenses`, `sous-listes-courses` et `pieces-jointes-entretien`, dessinées dans le nouveau style.
- Animations élaborées, graphiques (au-delà des barres par catégorie), personnalisation des couleurs par l'utilisateur.
- Internationalisation.
