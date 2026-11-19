## Pourquoi

Le lot 4a montre le **réalisé** (dépensé, rentré, résultat) et un prévu global en fin de période. Il manque de quoi **piloter** le mois :

- se fixer un budget par catégorie (« 400 € de courses par mois ») et voir quand on le dépasse ;
- savoir ce qu'il reste vraiment pour vivre une fois les charges fixes et les crédits payés ;
- suivre ses crédits en cours (capital restant dû, fin du prêt) sans tableur à côté.

## Ce qui change (proposition, à valider par Dorian)

Tout reste **facultatif** : sans budget prévu ni crédit, rien ne change à l'écran.

1. **Budget prévu par catégorie**
   - Un montant mensuel par catégorie de dépenses, le même chaque mois, modifiable à tout moment.
   - Dans le Budget, « Par catégorie » affiche **dépensé / prévu** avec une barre : indigo, puis orange à partir de 80 %, rouge en cas de dépassement.
   - Les charges récurrentes encore à venir dans le mois sont comptées, en plus clair, pour voir le dépassement venir.
   - Une catégorie sans montant prévu reste affichée comme aujourd'hui.
2. **Reste à vivre** (option « Rentrées et bilan » active)
   - Reste à vivre du mois = rentrées prévues − charges fixes − mensualités de crédit.
   - Ce qui reste = reste à vivre − dépenses courantes déjà faites.
   - Affiché dans le bandeau du mois du Budget.
3. **Crédits**
   - Un crédit est une **charge récurrente d'un type particulier**, dans l'écran Récurrents. On saisit le capital, le taux, la durée, la date de la première échéance et, si besoin, l'assurance mensuelle.
   - La mensualité est calculée, et l'échéancier est consultable avec le capital restant dû à date.
   - La charge s'arrête seule après la dernière échéance.
   - Les mensualités arrivent dans le Budget comme les autres charges.

## Impact (si la proposition est retenue)

- `budget` : montant prévu sur la catégorie (migration `budget/V5`), API de réglage, prévu renvoyé dans la vue du mois.
- `bills` : crédit = charge récurrente avec ses conditions d'emprunt (migration `bills/V4`), calcul de l'échéancier et du capital restant dû.
- PWA : Budget (barres prévu/réel, reste à vivre), Catégories (saisie du prévu), Récurrents (type Crédit, échéancier).
- Découpage proposé : **4b1 « Budget prévu et reste à vivre »** (v0.7.0), puis **4b2 « Crédits »** (v0.8.0).

Les questions ouvertes sont dans `design.md`. **Rien ne sera implémenté avant les réponses de Dorian.**
