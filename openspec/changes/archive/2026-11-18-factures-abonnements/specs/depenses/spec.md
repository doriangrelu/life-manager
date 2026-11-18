## ADDED Requirements

### Requirement: Dépense issue d'une charge récurrente

Le système SHALL enregistrer une dépense quand une échéance de charge récurrente est payée : même montant, date de l'échéance, catégorie, payeur et bénéficiaire de la charge, libellé égal au nom de la charge. Une même échéance SHALL ne produire qu'une seule dépense. Si la catégorie est archivée ou inconnue, la dépense SHALL être rangée dans « Autre ». La dépense SHALL rester modifiable et supprimable comme les autres.

#### Scenario: Échéance payée

- **WHEN** l'échéance du 2026-12-05 d'« Internet » (39,99 €, catégorie Abonnements) est payée
- **THEN** une dépense de 39,99 € datée du 2026-12-05, catégorie Abonnements, libellé « internet », apparaît dans le budget de décembre

#### Scenario: Pas de doublon

- **WHEN** l'information de paiement d'une même échéance arrive deux fois
- **THEN** une seule dépense existe
