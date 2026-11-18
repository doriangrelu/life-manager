> Proposition à relire par Dorian (voir « Open Questions » du design) avant toute implémentation.

## 1. Socle commun des pièces jointes

- [ ] 1.1 [kernel/domain + infrastructure] Extraire `AttachedFile`, `FileSize`, `AttachmentMediaType`, `AttachmentFileName`, `StorageKey`, le port de stockage disque et la détection Tika dans `kernel`
- [ ] 1.2 [vehicle] Brancher le carnet sur ce socle, sans changement de comportement (tests existants verts)

## 2. Domaine `bills` : charges

- [ ] 2.1 [bills/infrastructure] Schéma, compte `bills_app`, datasource, Flyway ; `create-database.sh` et init de dev ; test de migration
- [ ] 2.2 [bills/domain] Value objects (`ChargeName`, `Frequency`, `DueDay`, `AmountKind`) et calcul des dates d'échéance (+ tests, mois courts)
- [ ] 2.3 [bills/domain] Agrégat `RecurringCharge` : création, modification, résiliation, invariants (+ tests)
- [ ] 2.4 [bills/application + infrastructure/in] Use cases et API : ajouter, lister, consulter, modifier, résilier une charge ; cloisonnement par foyer (copie locale des membres) (+ tests d'intégration)

## 3. Domaine `bills` : échéances

- [ ] 3.1 [bills/domain] Agrégat `ChargeOccurrence` : statuts, confirmation, saut, invariants (+ tests)
- [ ] 3.2 [bills/application] `AdvanceChargeSchedules` idempotent (création anticipée, paiement fixe, « à confirmer » variable, rattrapage) (+ tests)
- [ ] 3.3 [bills/infrastructure/in] Déclenchement quotidien et au démarrage ; API des échéances : lister, confirmer, sauter (+ tests d'intégration)
- [ ] 3.4 [bills] Factures jointes aux échéances sur le socle commun (+ tests d'intégration)

## 4. Liens entre domaines

- [ ] 4.1 [bills/contract + infrastructure/out] Événements `ChargePaidV1` et `ChargeDeadlinesChangedV1`
- [ ] 4.2 [budget] Écoute de `ChargePaidV1` : dépense avec `source_occurrence_id` unique, repli sur « Autre » (+ tests, rejeu sans doublon)
- [ ] 4.3 [reminder] Type de source `CHARGE`, copie des échéances, fenêtre de 7 jours, échéances à confirmer (+ tests)

## 5. PWA

- [ ] 5.1 [frontend/api + lib] API des charges et calculs d'affichage (total mensuel estimé, tri) (+ tests)
- [ ] 5.2 [frontend/components] Onglet « Charges » : liste, échéances à confirmer, saisie et modification d'une charge
- [ ] 5.3 [frontend/components] Fiche d'une charge : échéances, confirmation, saut, factures jointes
- [ ] 5.4 [frontend] Vérification dans le navigateur : mobile et bureau, clair et sombre

## 6. Livraison

- [ ] 6.1 [ci] Parcours natif étendu aux charges (création, échéance, dépense, facture)
- [ ] 6.2 [doc] `docs/deploiement.md` : création du schéma `bills` en production ; architecture et feuille de route à jour
- [ ] 6.3 [infra/vps] Schéma `bills` créé sur le VPS (avec accord de Dorian), version publiée et déployée
