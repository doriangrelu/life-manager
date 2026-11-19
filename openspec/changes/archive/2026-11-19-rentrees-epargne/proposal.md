## Why

Le budget ne connaît que les dépenses : impossible de savoir si le foyer est **en positif ou en négatif** sur un mois, un trimestre ou une année, ni combien il a mis de côté. Idée du frère de Dorian (lot 4 de `docs/feuille-de-route.md`), affinée avec Dorian en trois itérations le 2026-11-19. Ce change est la **première moitié du lot 4 (4a)** ; le prévisionnel par catégorie et les crédits viendront en 4b.

Principe directeur, voulu par Dorian : **simple et optionnel**. Un foyer qui ne veut pas suivre ses rentrées ne voit **aucun changement**.

## What Changes

- **Réglage du foyer « Suivre les rentrées (bilan) »**, désactivé par défaut, réglé par le propriétaire. Désactivé : l'application reste exactement comme aujourd'hui.
- **Rentrées d'argent** (mode activé), saisies comme une dépense : montant, date, catégorie de rentrée, libellé, **reçue par** un membre ou par le foyer. Le bouton + du Budget propose « Dépense » (par défaut) ou « Rentrée ».
- **Catégories de rentrées**, libres comme celles des dépenses, avec des suggestions au départ : Salaire, Prime, Remboursement, Aides, Autre.
- **Bandeau du mois** (mode activé) : **Dépensé**, **Rentré** et **Résultat** (rentré − dépensé), en vert ou en rouge.
- **Charges** (mode activé) : deux panneaux, **Dépenses fixes** et **Rentrées fixes**. Même mécanique : montant fixe enregistré seul, montant variable à confirmer, rappels.
- **Bilan** (mode activé) : nouveau sous-onglet de l'espace Argent (Budget · Charges · Bilan). Pour un mois, un trimestre, une année ou les 12 derniers mois :
  - le **réalisé** et le **prévu en fin de période** (réalisé + rentrées et charges récurrentes encore à venir) ;
  - le **résultat** en couleur, et l'**épargne** = résultat positif de la période (« vous avez mis de côté 1 200 € ») ;
  - les rentrées par catégorie et par personne, les dépenses par catégorie.

## Capabilities

### New Capabilities

- `rentrees` : rentrées ponctuelles, catégories de rentrées, rentrées récurrentes.
- `bilan` : bilan par période, réalisé et prévu, résultat et épargne.

### Modified Capabilities

- `foyer` : réglage « Suivre les rentrées ».
- `budget-mensuel` : bandeau Dépensé / Rentré / Résultat quand le réglage est activé.
- `charges-recurrentes` : sens d'un mouvement récurrent (dépense ou rentrée), deux panneaux.

## Impact

- `household` : réglage du foyer (colonne, route réservée au propriétaire).
- `budget` : agrégat `Income`, catégories typées (dépense / rentrée), vue du mois et bilan par période.
- `bills` : sens d'un mouvement récurrent ; une échéance payée transmet ce sens au budget ; prévisions de la période.
- PWA : bouton +, bandeau, panneaux des charges, sous-onglet Bilan, réglage du foyer.
- Rien à faire sur le VPS (migrations automatiques).

## Hors périmètre

- Solde de départ, trésorerie, comptes et livrets (abandonnés par Dorian).
- Saisie d'épargne à part (l'épargne est le résultat positif).
- Prévisionnel par catégorie, reste à vivre détaillé, crédits (change 4b) ; graphiques (lot 5).
