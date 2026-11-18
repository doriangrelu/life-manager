## Context

Domaines existants : `budget` (dépenses, catégories), `reminder` (copie des échéances de véhicules reçues par `VehicleDeadlinesChangedV1`), `vehicle` (pièces jointes du carnet : stockage disque `vehicle/`, type vérifié par Tika). Communication par événements de contrat publiés après commit, sans outbox. Montants en centimes (`long`).

## Goals / Non-Goals

**Goals :** ne plus ressaisir les charges récurrentes ; ne plus oublier une échéance ; retrouver chaque facture.

**Non-Goals :** prévisionnel et reste à vivre (lot 3), lien bancaire.

## Decisions

### 1. Nouveau domaine `bills` (charges)

Une charge récurrente n'est pas une dépense : elle a son propre cycle (échéances, confirmation, saut, factures). Un domaine dédié garde `budget` simple et prépare le lot 3 (prévisionnel), qui lira les charges. Schéma `bills`, compte `bills_app`, datasource et Flyway dédiés, dossier de fichiers `bills/`.

Alternative écartée : une « dépense récurrente » dans `budget`. Plus rapide, mais mélange deux cycles de vie dans un agrégat déjà central.

### 2. Modèle

- **Agrégat `RecurringCharge`** : `ChargeId`, `HouseholdId`, `ChargeName` (normalisé comme les autres noms), `Money` prévu, `AmountKind` (`FIXED` / `VARIABLE`), `Frequency` (`MONTHLY`, `BIMONTHLY`, `QUARTERLY`, `YEARLY`), `DueDay` (1–31, ramené au dernier jour des mois courts), date de première échéance, `budgetCategoryId`, `payerId` et `beneficiaryId` (`null` = commun) — identifiants opaques d'autres domaines, vérifiés par le budget à la création de la dépense, état actif / résilié.
- **Agrégat `ChargeOccurrence`** (une échéance) : `OccurrenceId`, charge, date d'échéance, montant réel, statut `UPCOMING` → `TO_CONFIRM` → `PAID` ou `SKIPPED`, factures jointes (10 au plus). Agrégat séparé : petit, et on agit sur une échéance sans charger tout l'historique.
- Invariants : une seule échéance par charge et par date ; une échéance payée ou sautée ne change plus ; une charge résiliée ne crée plus d'échéance.

### 3. Échéancier quotidien

Un use case `AdvanceChargeSchedules`, déclenché chaque jour (Micronaut `@Scheduled`, 6 h, fuseau `Europe/Paris`) et au démarrage :
- crée l'échéance suivante de chaque charge active (au plus un an d'avance) ;
- à la date d'échéance : charge **fixe** → `PAID` au montant prévu ; charge **variable** → `TO_CONFIRM`.

Idempotent (contrainte d'unicité charge + date) : relancé deux fois, il ne crée rien de plus. Une application arrêtée plusieurs jours rattrape les échéances passées au redémarrage.

### 4. Lien avec le budget : événement `ChargePaidV1`

Quand une échéance passe `PAID`, `bills` publie `ChargePaidV1(occurrenceId, householdId, chargeName, amountCents, paidOn, budgetCategoryId, payerId, beneficiaryId)`. `budget` l'écoute et enregistre la dépense (libellé = nom de la charge), avec `sourceOccurrenceId` en colonne **unique** : un événement rejoué ne crée jamais de doublon. Catégorie archivée ou inconnue → catégorie « Autre » ; payeur parti du foyer → dépense non créée et échéance signalée (journal), cas très rare. Une dépense issue d'une charge reste modifiable et supprimable comme les autres.

Risque connu (inchangé) : sans outbox, une dépense peut manquer si l'application s'arrête entre le commit et la publication ; la dette « outbox » de la feuille de route devient prioritaire juste après ce lot.

### 5. Rappels : événement `ChargeDeadlinesChangedV1`

`bills` publie, pour chaque charge, sa prochaine échéance non payée (`chargeId, householdId, chargeName, nextDueOn` ou `null`). `reminder` garde cette copie, comme pour les véhicules, avec un **type de source** (`VEHICLE` / `CHARGE`). Fenêtre de rappel des charges : **7 jours** avant l'échéance (30 jours pour les véhicules) ; une échéance « à confirmer » reste en rappel jusqu'à sa confirmation.

### 6. Factures jointes : socle commun dans `kernel`

Les règles de pièces jointes sont identiques au carnet. Le code générique (`AttachedFile`, `FileSize`, `AttachmentMediaType`, `AttachmentFileName`, `StorageKey`, port de stockage et détection Tika) passe dans `kernel` ; chaque domaine garde son propre dossier, ses propres tables de métadonnées et ses use cases. Le carnet (`vehicle`) est migré sur ce socle sans changement de comportement.

### 7. PWA

Nouvel onglet **« Charges »** (icône reçu) entre Budget et Courses : total mensuel estimé, échéances à confirmer en tête, puis les charges triées par prochaine échéance. Fiche d'une charge : ses échéances passées et à venir, factures. La saisie reprend les composants existants (montant centré, chips de catégorie, segments).

## Risks / Trade-offs

- **Cinq onglets** sur mobile : encore lisible ; à revoir si d'autres sections arrivent (menu « Plus »).
- **Montants variables** : la dépense n'existe qu'après confirmation ; le budget du mois peut être incomplet tant qu'une échéance n'est pas confirmée (signalé dans l'écran du mois).
- **Tâche planifiée** dans une seule instance : suffisant ici (un seul serveur).

## Migration Plan

Nouveau schéma `bills` et compte `bills_app` : à ajouter à `create-database.sh` et, en production, créés une fois sur le VPS avant le déploiement (procédure dans `docs/deploiement.md`). Colonne `source_occurrence_id` dans `budget.expense` (migration additive). Retour arrière : version précédente, le schéma `bills` reste inutilisé.

## Open Questions (à trancher par Dorian)

1. **Confirmation** : dépense créée seule pour un montant fixe, « à confirmer » pour un montant variable — ou tout à confirmer ?
2. **Rappel** : 7 jours avant l'échéance, ça te va ?
3. **Factures** : jointes à chaque échéance (facture du mois) — faut-il aussi un document par charge (contrat) ?
4. **Navigation** : un 5ᵉ onglet « Charges », ou une section dans l'onglet Budget ?
5. **Payeur et bénéficiaire** : comme une dépense — un membre paie, pour le foyer (« Commun ») par défaut ou pour un membre. D'accord ?
