## Why

Retour de Dorian après essai de la PWA : en choisissant « Autre » sur une dépense, il s'attendait à pouvoir taper librement sa catégorie. Aujourd'hui, il faut quitter la saisie, passer par « Gérer les catégories », créer la catégorie, puis revenir : c'est contraire à l'objectif de saisie en quelques secondes. Par ailleurs, « Autre » est classée par ordre alphabétique au milieu de la liste, alors qu'elle sert de dernier recours.

## What Changes

- **Nouvelle catégorie à la saisie** : une dépense peut être enregistrée (ou corrigée) avec un **nom de catégorie** à la place d'une catégorie existante. Si le foyer a déjà une catégorie de ce nom (sans tenir compte des majuscules), elle est utilisée, et réactivée si elle était archivée ; sinon elle est créée. Tout se fait en une seule opération.
- **PWA** : choisir « Autre » fait apparaître un champ « Préciser (facultatif) ». Laissé vide, la dépense reste dans « Autre » ; rempli, la dépense va dans la catégorie tapée, qui sera ensuite proposée comme les autres.
- **« Autre » toujours en dernier** dans la liste des catégories (API et PWA), les autres restant triées par nom.

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `depenses` : saisie et correction avec un nom de catégorie libre ; précision de « Autre » dans la PWA.
- `categories-depenses` : la catégorie « Autre » est listée en dernier.

## Impact

- Backend, domaine `budget` uniquement : commande de saisie et de correction (champ facultatif `newCategoryName`), service applicatif de choix de catégorie, tri des catégories.
- API : `POST` et `PUT /api/households/{id}/expenses` acceptent `categoryId` **ou** `newCategoryName` (exactement l'un des deux).
- PWA : formulaire de dépense.

## Hors périmètre

- Sous-catégories ou hiérarchie de catégories.
- Fusion de catégories existantes.
- Suggestions automatiques de catégorie selon le libellé.
