## Why

Retour de Dorian après essai : une liste unique mélange l'alimentaire, le bricolage et les vêtements, alors qu'on ne les achète pas au même endroit. Et refaire chaque semaine la même liste est fastidieux : il faut pouvoir remettre en un geste les articles déjà achetés. Ce change revient sur la décision « une seule liste par foyer » prise sans Dorian (`openspec/changes/archive/2026-11-16-liste-courses-partagee/design.md`).

## What Changes

- **Sous-listes** : chaque foyer a plusieurs listes thématiques. Un nouveau foyer reçoit « Courses », « Bricolage » et « Vêtements » ; les membres peuvent en ajouter, en renommer et en supprimer. Chaque article appartient à une sous-liste.
- **Historique des achats** : « Vider les articles cochés » ne supprime plus les articles, il les range dans l'historique de leur sous-liste (un libellé n'y figure qu'une fois, avec sa dernière date d'achat et le nombre d'achats).
- **Remise en liste** : depuis l'historique, un membre sélectionne un ou plusieurs articles et les remet d'un coup dans la sous-liste. Il peut aussi retirer un libellé de l'historique.
- **PWA** : choix de la sous-liste dans l'onglet Courses, écran d'historique avec sélection multiple.
- **Reprise des données** : les articles existants rejoignent la sous-liste « Courses » de leur foyer.

## Capabilities

### New Capabilities

- `historique-courses` : historique des achats par sous-liste et remise en liste.

### Modified Capabilities

- `liste-courses` : sous-listes ; l'ajout, la consultation et le vidage des articles cochés se font par sous-liste ; le vidage alimente l'historique.

## Impact

- Backend, domaine `shopping` uniquement : nouvel agrégat de sous-liste, entrée d'historique, migration `V2` du schéma `shopping` (reprise des données comprise), nouvelles routes sous `/api/households/{id}/shopping-lists/…`. Les anciennes routes `/shopping-items` sont retirées (**BREAKING** pour l'API, la PWA étant le seul client).
- PWA : onglet Courses.

## Hors périmètre

- Déplacer un article d'une sous-liste à une autre.
- Articles par défaut gérés comme des modèles (Dorian a choisi l'historique).
- Quantités structurées, rayons ou tri par magasin.
- Temps réel et hors-ligne (inchangés : rafraîchissement toutes les 5 s).
