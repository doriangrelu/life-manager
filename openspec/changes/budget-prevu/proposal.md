## Pourquoi

Le lot 4a montre le **réalisé** (dépensé, rentré, résultat). Il manque de quoi **piloter** le mois :

- se fixer un budget par catégorie (« 400 € de courses par mois ») et voir quand on le dépasse ;
- savoir ce qu'il reste vraiment pour vivre une fois les dépenses récurrentes payées.

C'est le lot **4b1 « Budget prévu et reste à vivre »** (v0.7.0). Les crédits suivront au lot **4b2** (v0.8.0), comme un type de dépense récurrente (choix de Dorian).

## Ce qui change

Tout reste **facultatif** : sans budget prévu, rien ne change à l'écran.

1. **Budget prévu par catégorie de dépenses**
   - On le règle depuis le Budget, en touchant une catégorie : un montant, et un choix **« Tous les mois »** (par défaut) ou **« Ce mois seulement »**.
   - « Tous les mois » : le montant vaut pour le mois affiché et tous les suivants, jusqu'au prochain changement. Les mois passés gardent le prévu qu'ils avaient : l'historique reste juste (utile pour les graphiques du lot 5).
   - « Ce mois seulement » : un ajustement ponctuel (décembre plus chargé), sans toucher aux autres mois.
   - On peut aussi retirer le prévu (ce mois seulement, ou à partir de ce mois).
2. **Suivi prévu / réel dans le Budget**
   - « Par catégorie » affiche **dépensé / prévu** et une barre : indigo, orange à partir de 80 %, rouge en cas de dépassement, avec une pastille (« 86 % », « + 34 € »).
   - Les dépenses récurrentes encore à venir dans le mois sont ajoutées en plus clair, pour voir le dépassement arriver.
   - Une catégorie avec un prévu apparaît même sans dépense ; une catégorie sans prévu reste comme aujourd'hui.
3. **Reste à vivre** (option « Rentrées et bilan » active)
   - Reste à vivre = rentrées prévues − dépenses récurrentes de la période (payées et à venir).
   - Ce qu'il en reste = reste à vivre − dépenses courantes déjà faites.
   - Affiché dans le bandeau du mois du **Budget** et dans le **Bilan** (pour la période choisie).

## Impact

- Backend `budget` : nouvel agrégat `CategoryBudget` (migration `budget/V5`), une route pour régler le prévu, prévu du mois et total des dépenses récurrentes dans la vue du mois, total des dépenses récurrentes dans le bilan. Aucun autre domaine touché.
- PWA : Budget (barres, pastilles, réglage du prévu, reste à vivre), Bilan (reste à vivre).
- Version **v0.7.0**.
- Specs : nouvelle capacité `budget-prevu` ; `bilan` complété.
