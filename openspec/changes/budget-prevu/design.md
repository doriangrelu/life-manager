## Contexte

Faits établis dans le code (2026-11-19) :

- Aucune notion de montant prévu par catégorie n'existe.
- Une charge récurrente porte déjà une catégorie de budget, et `GET /charges/forecast` la renvoie avec chaque échéance à venir (`budgetCategoryId`, `direction`).
- Les paiements de charges deviennent des dépenses du Budget via `ChargePaidV1` ; le lien est gardé dans la table `budget.charge_expense`.

## Réponses de Dorian (2026-11-19)

- Le prévu doit pouvoir être ajusté pour un mois, mais le cas simple (le même montant décliné sur tous les mois) doit rester le plus facile.
- Reste à vivre : dans le Budget **et** dans le Bilan.
- Crédits : ce sont des récurrents (lot 4b2).
- Découpage 4b1 puis 4b2 : d'accord.
- Alerte (barre orange puis rouge, pastille) : pas d'objection ; notifications au lot 6.

## Décisions

### Modèle du prévu : des règles datées

Agrégat `CategoryBudget` (domaine `budget`) : foyer, catégorie, mois, montant en centimes (ou absent = « pas de prévu »), et portée :

- `FROM_MONTH` : vaut à partir de ce mois, jusqu'à la règle suivante ;
- `MONTH_ONLY` : vaut pour ce mois seulement.

Prévu d'une catégorie pour un mois M = la règle `MONTH_ONLY` de M si elle existe, sinon la règle `FROM_MONTH` la plus récente dont le mois ≤ M. Une seule règle par (catégorie, mois, portée) : régler à nouveau la remplace.

Pourquoi pas un simple champ sur la catégorie : changer le montant réécrirait le prévu des mois passés, et fausserait les comparaisons prévu / réel du lot 5.

Invariants : catégorie de dépenses du foyer, non archivée ; montant entre 0,01 € et 1 000 000 € (comme une dépense) ; mois au format `AAAA-MM`.

### API (domaine `budget`)

- `PUT /api/households/{h}/expense-categories/{c}/budget` avec `{ "month": "2026-11", "plannedCents": 40000, "scope": "FROM_MONTH" }` (`plannedCents` absent = retirer le prévu) → `204`. Accès contrôlé par le service `…Access` du domaine, comme les autres use cases.
- Vue du mois (`GET …/budget?month=`) : ajout de `plans` (`categoryId`, `plannedCents`, `scope` de la règle appliquée) et de `recurringExpenseCents` (total des dépenses du mois nées d'une charge récurrente, via `charge_expense`).
- Bilan (`GET …/report`) : ajout de `recurringExpenseCents` sur la période.

### Calculs dans la PWA (pas de couplage entre domaines)

Comme le « prévu » du Bilan au lot 4a, la PWA combine la vue du mois (`budget`) et les échéances à venir (`bills`, `/charges/forecast`) :

- par catégorie : réalisé + échéances de dépenses à venir de la catégorie, comparés au prévu ;
- reste à vivre = (rentrées réalisées + rentrées à venir) − (dépenses récurrentes payées + à venir) ;
- ce qu'il en reste = reste à vivre − dépenses courantes (dépenses réalisées − dépenses récurrentes payées).

Pour un mois passé, il n'y a plus d'échéances à venir : tout vient du réalisé.

### Interface

- Budget, « Par catégorie » : toucher une ligne ouvre une petite fenêtre « Budget prévu » (montant, choix Tous les mois / Ce mois seulement, bouton Retirer). Un lien « Définir un budget » apparaît tant qu'aucun prévu n'existe.
- Bandeau du mois : ligne « Reste à vivre » sous le résultat, seulement si l'option « Rentrées et bilan » est active et qu'il y a des rentrées prévues.
- Bilan : carte « Reste à vivre » de la période.

## Hors périmètre

- Crédits (4b2), notifications de dépassement (lot 6), prévu sur les rentrées, graphiques prévu / réel (lot 5).
