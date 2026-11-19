## Context

- `budget` (schéma `budget`) : agrégats `Expense` (montant, date, catégorie, payeur, bénéficiaire, libellé) et `Category` ; vue `MonthlyBudget` (total, par catégorie, par payeur, par bénéficiaire, liste).
- `bills` (schéma `bills`) : charges récurrentes avec calendrier d'échéances, montant fixe (payé seul) ou variable (« à confirmer »), rappels, factures ; une échéance payée publie `ChargePaidV1`, que `budget` transforme en dépense (lien unique par échéance).

Un salaire ou un virement d'épargne mensuel suit **exactement** la même mécanique qu'une charge : seul le sens change.

## Goals / Non-Goals

**Goals** : rentrées et épargne au même niveau de confort que les dépenses (saisie rapide, récurrence) ; solde du mois juste ; zéro duplication de la mécanique des échéances.
**Non-Goals** : prévisionnel, crédits, comptes d'épargne multiples (change 4b et suivants).

## Decisions

### Domaine `budget` : deux nouveaux agrégats

- `Income` : `IncomeId`, foyer, `Money` (> 0), date (pas dans le futur), `IncomeKind` (SALARY, BONUS, REFUND, AID, OTHER), libellé optionnel, reçue par (`MemberId`, ou null = foyer). Tables `budget.income`.
- `Saving` : `SavingId`, foyer, `Money` (> 0), date, libellé optionnel (« Livret A »…), mis de côté par (membre ou foyer). Table `budget.saving`.
- Vue du mois `MonthlyBudget` : `incomeCents`, `savingCents`, `balanceCents = incomes − expenses − savings`, liste des rentrées, liste des épargnes ; cumul d'épargne depuis le début (`savedToDateCents`).
- Cloisonnement : mêmes services d'accès que les dépenses (`BudgetAccess`).

### Récurrence : les échéances de `bills` gagnent un sens

- `RecurringCharge` gagne `FlowDirection` : `EXPENSE` (aujourd'hui), `INCOME`, `SAVING`. Colonne `direction` (défaut `EXPENSE`, migration additive). Pour une rentrée, « payeur » = qui la reçoit ; pas de catégorie de budget.
- Une échéance payée publie `ChargePaidV1` **avec le sens** (`direction`, champ ajouté ; absent = dépense, pour la compatibilité). `budget` crée selon le sens une dépense, une rentrée ou une épargne, toujours avec le lien unique par échéance (pas de doublon au rejeu).
- Rappels : inchangés (une rentrée variable « à confirmer » produit le même rappel).
- PWA : l'écran « Charges » devient **« Récurrents »**, avec un filtre Charges / Rentrées / Épargne et un formulaire qui demande d'abord le sens.

### PWA (espace Argent)

- Bandeau du mois : quatre chiffres (rentrées, dépenses, épargne, solde ; solde rouge s'il est négatif).
- Bouton + : petit menu « Dépense » (par défaut, comme aujourd'hui), « Rentrée », « Épargne ».
- Sections « Rentrées du mois » et « Épargne du mois » sous les dépenses, repliables.

## Risks / Trade-offs

- Renommer « Charges » en « Récurrents » change une habitude toute neuve → les adresses restent (`#/charges`), seul le libellé change.
- `ChargePaidV1` évolue : champ ajouté, lecture tolérante (sans champ = dépense) ; les événements déjà en boîte d'envoi restent valides.

## Migration Plan

Migrations additives : `budget` (tables `income`, `saving`, lien d'échéance élargi), `bills` (colonne `direction`). Rien à faire sur le VPS. Retour arrière : version précédente, les nouvelles tables sont ignorées.

## Open Questions (à trancher par Dorian)

1. **Découpage** : 4a (rentrées, épargne, solde) maintenant, 4b (prévisionnel, crédits) ensuite. D'accord ?
2. **Récurrents** : salaire et virement d'épargne gérés dans l'écran des charges renommé « Récurrents » (même mécanique, un filtre par sens), ou un écran séparé par type ?
3. **Rentrées par personne** : on note qui reçoit (comme « payé par ») pour voir plus tard qui apporte quoi, ou seulement le foyer ?
4. **Épargne** : un seul total « mis de côté » par mois, ou déjà plusieurs comptes nommés (Livret A, PEL…) ?
5. **Solde** : rentrées − dépenses − épargne (ce qui reste vraiment) : c'est bien le chiffre que tu veux voir en grand ?
