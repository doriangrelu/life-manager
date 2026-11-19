## Context

- `budget` (schéma `budget`) : `Expense` (montant, date, catégorie, payeur, bénéficiaire, libellé) et `Category` (nom libre par foyer, archivable, catégories par défaut à la création) ; vue `MonthlyBudget`.
- `bills` (schéma `bills`) : charges récurrentes, échéances (fixe = payée seule, variable = à confirmer), rappels ; une échéance payée publie `ChargePaidV1`, transformé par `budget` en dépense (lien unique par échéance).
- `household` : foyer, propriétaire (lot 3).
- Une rentrée récurrente (salaire) suit **exactement** la mécanique d'une charge, avec le signe inverse.

## Goals / Non-Goals

**Goals** : savoir si l'on est en positif ou en négatif, par période, en réalisé et en prévu ; zéro changement pour qui n'active pas le réglage ; aucune duplication de la mécanique des échéances.
**Non-Goals** : solde de départ, comptes, épargne saisie, prévisionnel par catégorie, crédits, graphiques.

## Decisions

### Réglage du foyer (`household`)

- `Household.incomeTracking` (booléen, faux par défaut) ; `enableIncomeTracking(by)` / `disableIncomeTracking(by)` réservés au propriétaire. Colonne `household.income_tracking`, migration additive.
- `PUT /api/households/{h}/settings` `{ incomeTracking }` (403 pour un non-propriétaire) ; la consultation du foyer renvoie le réglage.
- Le réglage ne fait que **montrer ou cacher** dans la PWA : les données existent ou non indépendamment. Désactiver ne supprime rien.

### Rentrées (`budget`)

- Agrégat `Income` : foyer, `Money` (> 0), date (pas dans le futur), catégorie de rentrée, libellé optionnel, reçue par (`MemberId` ou null = foyer). Table `budget.income`.
- `Category` gagne `CategoryKind` (`EXPENSE`, `INCOME`) ; les catégories existantes sont `EXPENSE` (migration avec défaut). Unicité du nom par foyer **et par sens**. Catégories de rentrées suggérées, créées au premier usage du réglage, une seule fois : Salaire, Prime, Remboursement, Aides, Autre.
- Une rentrée ne peut utiliser qu'une catégorie `INCOME`, une dépense qu'une catégorie `EXPENSE`.
- Routes : `/api/households/{h}/incomes` (lister par mois, ajouter, modifier, supprimer) et `/api/households/{h}/income-categories` ; cloisonnement par `BudgetAccess`.

### Vue du mois et bilan (`budget`)

- `MonthlyBudget` gagne `incomeCents`, `resultCents = incomeCents − totalCents` et la liste des rentrées (les champs existants ne changent pas).
- `GET /api/households/{h}/report?from=YYYY-MM&to=YYYY-MM` : **réalisé** de la période (rentrées par catégorie et par personne, dépenses par catégorie, résultat) et résultat de chaque mois.
- Le **prévu** = réalisé + échéances récurrentes encore ouvertes dans la période. Ces échéances vivent dans `bills` : la PWA les obtient de `bills` (route ci-dessous) et fait l'addition. Aucun domaine ne lit l'autre ; pas de nouvel événement.

### Récurrents (`bills`)

- `RecurringCharge` gagne `FlowDirection` (`EXPENSE` par défaut, `INCOME`). Colonne `direction`, migration additive. Pour une rentrée, « payeur » = qui la reçoit, et la catégorie est une catégorie de rentrée.
- `ChargePaidV1` gagne `direction` (absent = `EXPENSE`, lecture tolérante) ; `budget` crée une dépense ou une rentrée selon le sens, toujours une seule par échéance.
- `GET /api/households/{h}/charges/forecast?from=YYYY-MM-DD&to=YYYY-MM-DD` : échéances encore ouvertes (à venir ou à confirmer) de la période, avec montant prévu et sens.
- Rappels inchangés (une rentrée variable à confirmer produit le même rappel « montant à confirmer »).

### PWA

- Le réglage du foyer est lu avec le foyer. Mode désactivé : écrans actuels inchangés.
- Mode activé :
  - **Budget** : le + ouvre un petit choix « Dépense » / « Rentrée » ; bandeau à trois chiffres (Dépensé, Rentré, Résultat en couleur) ; section « Rentrées du mois ».
  - **Charges** : sélecteur « Dépenses fixes » / « Rentrées fixes » en haut de l'écran ; le formulaire s'adapte au sens.
  - **Bilan** : sous-onglet de l'espace Argent (ajouté à la navigation par espaces seulement quand le mode est activé) ; sélecteur Mois / Trimestre / Année / 12 derniers mois ; tableau Réalisé / Prévu ; épargne = résultat positif de la période ; détails.
- **Foyer** : interrupteur « Suivre les rentrées (bilan) » pour le propriétaire.

## Risks / Trade-offs

- Prévu calculé par la PWA à partir de deux domaines : simple et sans couplage, mais le chiffre n'existe pas côté serveur (acceptable : il n'est qu'affiché).
- Catégories typées : une catégorie ne peut pas servir aux deux sens (choix voulu : la liste reste lisible).
- `ChargePaidV1` évolue avec un champ optionnel : les événements déjà dans une boîte d'envoi restent valides.

## Migration Plan

Migrations additives : `household` (`income_tracking`), `budget` (`category.kind`, table `income`, lien d'échéance vers une rentrée), `bills` (`direction`). Rien sur le VPS. Retour arrière : version précédente, les nouvelles colonnes et tables sont ignorées.

## Open Questions

Tranchées par Dorian le 2026-11-19 (trois itérations) : approche « bilan » par période, réalisé et prévu ; pas de solde de départ, pas de comptes ; rentrées saisies comme les dépenses avec catégories libres suggérées, qui reçoit noté ; deux panneaux dans Charges ; épargne = résultat positif ; périodes mois / trimestre / année / 12 derniers mois ; nom « Bilan » ; existant intact grâce à un réglage.

Reste à confirmer : le réglage « Suivre les rentrées » est **au niveau du foyer, réglé par le propriétaire** (proposé), plutôt que par appareil.
