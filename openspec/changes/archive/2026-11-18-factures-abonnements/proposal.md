## Why

Une grande partie des dépenses du foyer revient chaque mois ou chaque année (électricité, internet, téléphone, assurances, abonnements). Aujourd'hui il faut les ressaisir à la main, on oublie des échéances, et les factures sont éparpillées. C'est le lot 1 de la feuille de route (`docs/feuille-de-route.md`).

## What Changes

- **Charges récurrentes** du foyer : nom, montant prévu, fréquence (mensuelle, bimestrielle, trimestrielle, annuelle), jour d'échéance, catégorie du budget, payeur, montant **fixe** ou **variable**.
- **Échéances** calculées pour chaque charge, consultables à l'avance.
- **Dépense créée toute seule** dans le budget à l'échéance d'une charge à montant fixe ; pour une charge à montant variable (électricité…), l'échéance passe « à confirmer » et le membre saisit le montant réel, ce qui crée la dépense.
- Une échéance peut être **sautée** (mois offert, résiliation en cours) sans créer de dépense.
- **Rappel** quelques jours avant chaque échéance, dans le bandeau de rappels et les notifications existants.
- **Facture jointe** à une échéance (PDF ou photo), mêmes règles que les pièces jointes du carnet.
- **PWA** : écran « Charges » (liste, prochaine échéance, total mensuel estimé), saisie, échéances à confirmer.

## Capabilities

### New Capabilities

- `charges-recurrentes` : charges, échéances, confirmation, saut, factures jointes.

### Modified Capabilities

- `depenses` : une dépense peut provenir d'une échéance de charge (sans doublon).
- `rappels` : les échéances de charges deviennent des rappels.
- `interface-pwa` : accès à l'écran des charges.

## Impact

- Nouveau domaine `bills` (schéma, datasource, Flyway, stockage `bills/`).
- `budget` : écoute un nouvel événement de contrat et enregistre la dépense correspondante.
- `reminder` : copie des échéances de charges, en plus de celles des véhicules.
- PWA : nouvel écran, navigation.

## Hors périmètre

- Prélèvement réel ou lien bancaire, import de relevés.
- Crédits et échéanciers d'emprunt, budget prévisionnel et reste à vivre (lot 3, qui s'appuiera sur ces charges).
- Répartition d'une charge entre plusieurs payeurs.
- Historique des changements de tarif d'une charge (le nouveau montant vaut pour les échéances suivantes).
