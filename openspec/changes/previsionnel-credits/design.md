## Contexte

Faits établis dans le code (2026-11-19) :

- Aucune notion de montant prévu ou de plafond par catégorie : ni champ, ni colonne, ni API.
- Une charge récurrente porte déjà une catégorie de budget (`budgetCategoryId`), et `GET /charges/forecast` la renvoie avec chaque échéance à venir. Le prévu par catégorie peut donc être calculé par la PWA, sans couplage entre `budget` et `bills`, comme le prévu du Bilan au lot 4a.
- Les paiements de charges deviennent des dépenses ou des rentrées du Budget via `ChargePaidV1` : une mensualité de crédit gérée comme une charge y arrive donc sans rien ajouter.
- Le mot « crédit » n'existe nulle part dans le code.

## Choix proposés

- **Prévu stocké sur la catégorie** (`category.planned_cents`, facultatif), dans le domaine `budget` : c'est une propriété de la catégorie, pas un nouvel agrégat.
- **Calcul du prévu et du reste à vivre par la PWA**, à partir de la vue du mois (réalisé + prévu par catégorie) et du prévisionnel des charges (`bills`) : même principe que le Bilan, sans couplage serveur.
- **Crédit dans `bills`** : une charge récurrente de type `LOAN` avec ses conditions (capital, taux annuel, nombre d'échéances, assurance mensuelle). La mensualité vient de la formule classique de l'amortissement à échéances constantes, arrondie au centime. Le dernier mois absorbe l'écart d'arrondi. Le tout en centimes (`long`), le taux en points de base (`int`, 3,45 % = 345), donc jamais de nombre à virgule dans le métier.
- **Alerte = visuelle** dans l'application. Les notifications et e-mails viendront au lot 6 (Brevo).

## Questions pour Dorian

1. **Budget prévu** : un seul montant par catégorie, valable tous les mois, ça te suffit ? Ou faut-il pouvoir l'ajuster pour un mois donné (décembre plus chargé, par exemple) ?
2. **Alerte de dépassement** : une barre orange à 80 % et rouge au-delà, plus une pastille sur la catégorie, ça te va ? (Les notifications arriveront au lot 6.)
3. **Reste à vivre** : la définition « rentrées prévues − charges fixes − mensualités, moins les dépenses déjà faites » te convient ? Tu le veux dans le bandeau du Budget, dans le Bilan, ou les deux ?
4. **Crédits** :
   - Intégrés aux Récurrents comme un type de charge (ma proposition), ou un écran à part ?
   - Mensualité calculée à partir du capital, du taux et de la durée (ma proposition), ou recopiée telle quelle depuis l'offre de prêt ?
5. **Découpage** : 4b1 « Budget prévu et reste à vivre » d'abord, puis 4b2 « Crédits » ? Deux livraisons plus petites et plus rapides à recetter.
