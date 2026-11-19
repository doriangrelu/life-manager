## Pourquoi

Retours de Dorian après la v0.7.0 (budget prévu) :

- voir d'un coup d'œil le **total prévu face au total dépensé**, pas seulement catégorie par catégorie ;
- savoir, sur une période, **si les budgets prévus sont respectés** : un rapport dans le Bilan.

(Les catégories qui ont un budget prévu s'affichent déjà avec 0 € dépensé tant qu'aucune dépense n'y est classée : c'est le cas depuis la v0.7.0.)

## Ce qui change

1. **Budget, « Par catégorie »** : une ligne de synthèse en tête, « Budget prévu : dépensé / prévu », avec sa barre (mêmes couleurs : orange à 80 %, rouge au-delà). Elle additionne les catégories qui ont un prévu ; les dépenses des catégories sans prévu sont rappelées à part (« + X € hors budget »).
2. **Bilan, nouvel onglet « Budgets »** (à côté de la vue actuelle, renommée « Résultat ») pour la période choisie (mois, trimestre, année, 12 mois) :
   - en tête : total dépensé / total prévu, et « N catégories sur M dans le budget » ;
   - une ligne par catégorie : prévu, dépensé, écart (« reste 88 € » ou « dépassé de 34 € »), barre colorée ;
   - sur plusieurs mois : « budget tenu 2 mois sur 3 » pour chaque catégorie.
   - Le prévu d'une période est la somme des prévus de chaque mois (règles datées, ajustements d'un mois compris). Les mois à venir ne comptent pas : on compare ce qui est comparable.
   - L'onglet n'apparaît que si au moins un budget prévu existe sur la période.

## Impact

- Backend `budget` : le bilan (`GET /report`) renvoie en plus le prévu de chaque catégorie mois par mois (`monthPlans`) et les dépenses par catégorie mois par mois (`monthExpenses`). Aucune migration, aucun autre domaine touché.
- PWA : Budget (ligne de synthèse), Bilan (onglets Résultat / Budgets).
- Version **v0.9.0** (avec les contrôles de saisie des formulaires d argent et les montants des dépenses en rouge avec un signe moins).
- Specs : `budget-prevu` et `bilan` complétés.
