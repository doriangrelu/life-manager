## Pourquoi

Lot 8 de la feuille de route, avancé avant les e-mails (6b) par Dorian le 2026-11-22 : il le juge plus intéressant à l'usage. Un foyer mène des projets (travaux, vacances, mariage, achat d'une voiture…) qui mêlent **de l'argent** (un budget, des dépenses) et **des choses à faire**. Aujourd'hui, ces dépenses se perdent dans le budget du mois, et les tâches n'ont pas de place.

## Ce que je propose (à valider)

- **Un projet** : un nom, une description facultative, un **budget prévu facultatif**, des dates facultatives (début, fin visée), un état (en cours, terminé). Visible par tout le foyer, comme le reste.
- **Dépenses rattachées** : à la saisie ou à la correction d'une dépense, un choix facultatif **« Projet »**. L'écran du projet montre le total dépensé face au budget prévu (même barre que le budget prévu par catégorie) et la liste des dépenses. Une dépense de projet **reste une dépense normale** : elle compte dans son mois, sa catégorie et le Bilan.
- **Liste de tâches** : titre, case « fait », et en option qui s'en charge et une date. Une tâche avec une date apparaît dans les rappels.
- **Place dans la navigation** : un sous-onglet **« Projets »** dans l'espace **Maison** (Véhicules · Rappels · Projets). La barre du bas reste à 4 entrées.
- **Hors périmètre de cette version** : épargne dédiée à un projet, pièces jointes (devis, factures), partage hors du foyer, modèles de projet.

## Questions pour Dorian

1. **Contenu d'un projet** : nom, description, budget prévu, dates, état (en cours / terminé), ça te va ? Faut-il aussi « abandonné », ou une photo / couleur ?
2. **Dépenses** : rattacher une dépense existante à un projet depuis la saisie de la dépense te convient ? Et une dépense de projet doit-elle bien continuer à compter dans le budget du mois et sa catégorie (ma recommandation), ou en être sortie ?
3. **Tâches** : une simple liste à cocher, ou aussi « qui s'en charge » et une date (avec rappel) ? Je recommande les deux en option, sans rien imposer.
4. **Navigation** : sous-onglet « Projets » dans l'espace Maison, ou plutôt dans l'espace Argent ?
5. **Charges récurrentes** : faut-il pouvoir rattacher aussi un récurrent à un projet (ex. un crédit travaux) ? Je propose de le garder pour plus tard.
6. **Découpage** : une seule version (v0.14.0), ou d'abord projets + tâches, puis les dépenses rattachées ?

## Impact (si validé)

- Nouveau domaine `project` (schéma `project`, migrations, cloisonnement par foyer, effacement avec le foyer).
- `budget` : un identifiant de projet facultatif sur la dépense (sans clé étrangère, comme la catégorie d'une charge), et un total des dépenses par projet.
- `reminder` : les tâches datées deviennent des rappels (événement de contrat, comme les échéances des charges).
- PWA : écran Projets (liste, fiche, tâches), champ « Projet » dans la saisie d'une dépense.
