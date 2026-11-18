> Relu et validé par Dorian le 2026-11-18 (réponses aux « Open Questions » du design : toutes acceptées).

## 1. Socle commun des pièces jointes

- [x] 1.1 [kernel/domain + infrastructure] Extraire `AttachedFile`, `FileSize`, `AttachmentMediaType`, `AttachmentFileName`, `StorageKey`, le port de stockage disque et la détection Tika dans `kernel`
- [x] 1.2 [vehicle] Brancher le carnet sur ce socle, sans changement de comportement (tests existants verts)

## 2. Domaine `bills` : charges

- [x] 2.1 [bills/infrastructure] Schéma, compte `bills_app`, datasource, Flyway ; `create-database.sh` et init de dev ; test de migration
- [x] 2.2 [bills/domain] Value objects (`ChargeName`, `Frequency`, `ChargeSchedule`, `AmountKind`) et calcul des dates d'échéance (+ tests, mois courts)
- [x] 2.3 [bills/domain] Agrégat `RecurringCharge` : création, modification, résiliation, invariants (+ tests)
- [x] 2.4 [bills/application + infrastructure/in] Use cases et API : ajouter, lister, consulter, modifier, résilier une charge ; cloisonnement par foyer (copie locale des membres) (+ tests d'intégration)
- [x] 2.5 [household + bills] Rappel des membres au démarrage (`MembershipKnownV1`) : les foyers créés avant le domaine `bills` y sont connus

## 3. Domaine `bills` : échéances

- [x] 3.1 [bills/domain] Agrégat `ChargeOccurrence` : statuts, confirmation, saut, invariants (+ tests)
- [x] 3.2 [bills/application] `AdvanceChargeSchedules` idempotent (création anticipée, paiement fixe, « à confirmer » variable, rattrapage) (+ tests)
- [x] 3.3 [bills/infrastructure/in] Déclenchement quotidien et au démarrage ; API des échéances : lister, confirmer, sauter (+ tests d'intégration)
- [x] 3.4 [bills] Factures jointes aux échéances sur le socle commun (+ tests d'intégration)

## 4. Liens entre domaines

- [x] 4.1 [bills/contract + infrastructure/out] Événements `ChargePaidV1` et `ChargeDeadlinesChangedV1`
- [x] 4.2 [budget] Écoute de `ChargePaidV1` : dépense liée à l'échéance par la table `charge_expense` (clé = échéance), repli sur « Autre » (+ tests, rejeu sans doublon)
- [x] 4.3 [reminder] Type de source `CHARGE`, copie des échéances, fenêtre de 7 jours, échéances à confirmer (+ tests)

## 5. PWA

- [x] 5.1 [frontend/api + lib] API des charges et calculs d'affichage (total mensuel estimé, tri) (+ tests)
- [x] 5.2 [frontend/components] Onglet « Charges » : liste, échéances à confirmer, saisie et modification d'une charge
- [x] 5.3 [frontend/components] Fiche d'une charge : échéances, confirmation, saut, factures jointes
- [x] 5.4 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre

## 6. Livraison

- [x] 6.1 [ci] Parcours natif étendu aux charges (création, échéance, dépense, facture)
- [x] 6.2 [doc] `docs/deploiement.md` : création du schéma `bills` en production (`add-domain-schema.sh`) ; architecture et feuille de route à jour
- [ ] 6.3 [infra/vps] Schéma `bills` créé sur le VPS (avec accord de Dorian), version publiée et déployée
