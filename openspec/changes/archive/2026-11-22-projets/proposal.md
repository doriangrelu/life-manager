## Pourquoi

Lot 8 de la feuille de route, avancé avant les e-mails (6b) par Dorian le 2026-11-22 : il le juge plus intéressant à l'usage. Un foyer mène des projets (travaux, vacances, mariage, achat d'une voiture…) qui mêlent **de l'argent** (un budget, des dépenses) et **des choses à faire**. Aujourd'hui, ces dépenses se perdent dans le budget du mois, et les tâches n'ont pas de place.

## Ce qui change

- **Un projet** : nom, description facultative, couleur, budget prévu facultatif, dates facultatives (début, fin visée), état (en cours, terminé, abandonné), option « compte dans le budget du mois » (cochée par défaut). Visible par tout le foyer.
- **Dépenses rattachées** : un choix facultatif « Projet » à la saisie ou à la correction d'une dépense (projets en cours seulement). La fiche du projet montre le total dépensé face au budget prévu et la liste des dépenses.
  - Projet **compté dans le budget** : la dépense reste une dépense normale (mois, catégorie, budget prévu, reste à vivre, Bilan).
  - Projet **hors budget** : ses dépenses sortent des totaux du mois, des catégories, du budget prévu, du reste à vivre et du Bilan ; elles restent visibles dans les opérations du mois, marquées « hors budget ».
- **Tâches** : titre, case « fait », et en option qui s'en charge (un membre du foyer) et une date. Une tâche datée non faite apparaît dans les rappels du foyer.
- **Navigation** : sous-onglet « Projets » dans l'espace Maison (Véhicules · Rappels · Projets).
- **Une seule version : v0.14.0.**
- **Hors périmètre** : charges récurrentes rattachées à un projet (plus tard), épargne dédiée, pièces jointes, partage hors du foyer, modèles de projet.

## Réponses de Dorian (2026-11-22)

1. Contenu : nom, description, budget, dates, **3 états** (en cours, terminé, abandonné) **et une couleur**.
2. Dépenses : **au choix par projet** (compter ou non dans le budget du mois), **compté par défaut**.
3. Tâches : responsable et date en option, rappel pour les tâches datées.
4. Navigation : espace **Maison**.
5. Récurrents : plus tard.
6. Découpage : une seule version v0.14.0.

## Impact

- Nouveau domaine `project` (schéma `project`, migrations, cloisonnement par foyer, effacement avec le foyer et départ d'un membre).
- `budget` : identifiant de projet facultatif sur la dépense ; copie locale des projets (existence, état, compté ou non) tenue par événements de contrat ; totaux qui écartent les dépenses hors budget ; liste des dépenses d'un projet.
- `reminder` : les tâches datées deviennent des rappels (copie tenue par événements de contrat).
- PWA : écran Projets (liste, fiche, tâches), champ « Projet » dans la saisie d'une dépense, mention « hors budget ».
