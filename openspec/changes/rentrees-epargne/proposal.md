## Why

Le budget ne connaît aujourd'hui que les dépenses : impossible de savoir si le mois est « dans le vert », ni combien le foyer met de côté. Idée du frère de Dorian, rangée au lot 4 de `docs/feuille-de-route.md`. Ce change en est la **première moitié (4a)** ; le budget prévisionnel et les crédits suivront dans un second change (4b), qui s'appuiera sur celui-ci.

## What Changes

- **Rentrées d'argent** : salaire, prime, remboursement, aide, autre. Montant, date, libellé, **reçue par** un membre ou par le foyer. Saisie, modification, suppression, comme une dépense.
- **Rentrées récurrentes** (salaire…) : elles arrivent seules chaque mois, sur le modèle des charges récurrentes. Montant fixe → enregistrée seule ; montant variable → « à confirmer ».
- **Épargne mensuelle** : chaque mois, une case « mis de côté » avec un montant, et une **épargne récurrente** possible (virement automatique vers un livret). Comptée à part des dépenses ; cumul affiché.
- **Écran du mois** : le bandeau montre **rentrées, dépenses, épargne et solde** (rentrées − dépenses − épargne), et une section « Rentrées du mois ». Le bouton + propose « Dépense », « Rentrée » ou « Épargne ».

## Capabilities

### New Capabilities

- `rentrees` : rentrées ponctuelles et récurrentes.
- `epargne` : montants mis de côté chaque mois, ponctuels ou récurrents, et leur cumul.

### Modified Capabilities

- `budget-mensuel` : rentrées, épargne et solde du mois.
- `charges-recurrentes` : le calendrier des échéances sert aussi aux rentrées et à l'épargne récurrentes (voir design).

## Impact

- `budget` : agrégats `Income` et `Saving`, tables dans le schéma `budget`, vue du mois enrichie.
- `bills` : un mouvement récurrent a un **sens** (charge, rentrée, épargne) ; une échéance payée publie l'événement correspondant.
- PWA : écran du mois, formulaires de rentrée et d'épargne, écran des récurrents.
- Rien à faire sur le VPS (migrations automatiques).

## Hors périmètre (change 4b)

- Budget prévisionnel par catégorie, reste à vivre, suivi prévu / réel et alertes.
- Crédits (capital, taux, échéancier, capital restant dû).
- Plusieurs comptes d'épargne nommés (Livret A, PEL…), intérêts : plus tard si besoin.
- Graphiques (lot 5).
