> Approche validée par Dorian le 2026-11-18 (« Planifie cela je suis ok »).

## 1. Socle commun

- [x] 1.1 [kernel/infrastructure] Entité et requêtes communes de la boîte d'envoi, enregistrement d'un événement (JSON, type retrouvé par introspection)
- [x] 1.2 [kernel/infrastructure] Facteur : distribution ordonnée, erreurs et mise de côté, déclenchement après validation, relance planifiée, ménage

## 2. Domaines producteurs

- [x] 2.1 [identity, household, vehicle, bills / infrastructure] Migration `outbox` dans chaque schéma, dépôt et fabrique du facteur
- [x] 2.2 [identity, household, vehicle, bills / infrastructure/out] Relais branchés sur la boîte d'envoi (`BEFORE_COMMIT`)

## 3. Vérification

- [x] 3.1 [kernel + bills/budget] Tests : arrêt simulé avant distribution, destinataire en erreur puis rétabli, mise de côté, changement annulé, doublon sans effet
- [ ] 3.2 [ci] Suite complète et parcours natif verts

## 4. Livraison

- [x] 4.1 [doc] Architecture : règle de la boîte d'envoi ; feuille de route : dette soldée
- [ ] 4.2 [infra/vps] Version publiée et déployée
