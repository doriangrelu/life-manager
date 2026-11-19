## Pourquoi

Lot 5 de la feuille de route : voir l'année d'un coup d'œil et suivre l'évolution mois par mois, maintenant que le réalisé (lot 4a) et le prévu (lot 4b) existent.

Une partie est déjà là : le Bilan sait afficher une **année** (total, répartition par catégorie, résultat de chaque mois) et le respect des budgets prévus. Il manque surtout les **courbes d'évolution** et le **coût d'un véhicule**.

## Ce qui change (proposition, à valider par Dorian)

### 5a — Graphiques du Bilan (v0.10.0)

Un troisième onglet du Bilan, **« Évolution »**, à côté de « Résultat » et « Budgets », pour la période choisie (le plus parlant sur « Année » ou « 12 mois ») :

1. **Rentrées et dépenses, mois par mois** : deux barres par mois (vert / rouge), et la courbe de l'**épargne cumulée** (ce qui a été mis de côté depuis le début de la période).
2. **Dépenses face au prévu** : les dépenses de chaque mois en barres, le total prévu en trait. On voit tout de suite les mois de dépassement.
3. **Une catégorie au choix** : on touche une catégorie (Courses, Loisirs…) et le graphique montre ses dépenses mois par mois, avec son prévu et sa moyenne.
4. Chaque graphique a sa version en texte pour les lecteurs d'écran, et on peut toucher un mois pour lire ses chiffres exacts.

Sans le suivi des rentrées, le graphique 1 ne montre que les dépenses ; sans budget prévu, les traits du prévu disparaissent.

### 5b — Coût d'un véhicule sur l'année (v0.11.0)

Sur la fiche d'un véhicule : son **coût de l'année** (et de l'année passée), mois par mois. Le carnet d'entretien sait déjà ce que coûte chaque opération ; le reste (assurance, crédit auto, carburant, péage…) est aujourd'hui dans le Budget, sans lien avec le véhicule. Voir la question 3.

## Comment (pour mémoire)

- Graphiques dessinés en SVG par la PWA, sans nouvelle librairie : légers, aux couleurs de l'application, en clair et en sombre, lisibles sur téléphone.
- 5a : PWA seulement. Les données existent déjà dans le bilan (`months`, `monthExpenses`, `monthPlans`).
- 5b : selon la réponse à la question 3.

## Questions pour Dorian

1. Un onglet **« Évolution »** dans le Bilan, avec ces trois graphiques : ça te va ? Un graphique à enlever, ou un autre qui te manque ?
2. **Épargne cumulée** : elle part de 0 au début de la période choisie (janvier pour « Année »). Ça te convient, ou tu veux un cumul depuis le tout premier mois saisi ?
3. **Coût d'un véhicule** — que doit-il compter ?
   - (a) seulement le carnet d'entretien (déjà possible, rien à saisir de plus) ;
   - (b) l'entretien **plus les dépenses récurrentes** qu'on rattache au véhicule (assurance, crédit auto, contrôle technique…) : un champ « Véhicule » facultatif sur un récurrent ;
   - (c) comme (b), **plus les dépenses ponctuelles** (carburant, péage, parking) : le même champ « Véhicule » sur une dépense.
4. **Découpage** : les graphiques (5a) d'abord, puis le coût des véhicules (5b) ?
