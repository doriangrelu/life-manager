## ADDED Requirements

### Requirement: Sens d'un mouvement récurrent

Une charge récurrente SHALL avoir un sens : dépense (par défaut, toutes les charges existantes) ou rentrée. Une échéance payée SHALL produire dans le budget, selon son sens, une dépense ou une rentrée, une seule fois par échéance. Pour un foyer qui suit ses rentrées, la PWA SHALL présenter deux panneaux, « Dépenses fixes » et « Rentrées fixes », avec le même fonctionnement.

#### Scenario: Charge existante

- **WHEN** la nouvelle version démarre avec la charge « Internet »
- **THEN** son sens est « dépense » et son comportement ne change pas

#### Scenario: Rentrée récurrente payée

- **WHEN** l'échéance d'un salaire récurrent de 2 450 € est payée
- **THEN** une rentrée de 2 450 € apparaît dans le mois, et aucune dépense

### Requirement: Échéances à venir d'une période

Le système SHALL fournir, pour une période, les échéances récurrentes encore ouvertes (à venir ou à confirmer) du foyer, avec leur montant prévu et leur sens, pour calculer le prévu du bilan.

#### Scenario: Prévisions de novembre

- **WHEN** au 15 novembre un membre demande les échéances ouvertes de novembre
- **THEN** il obtient le salaire prévu le 28 et les charges restant à payer en novembre, sans celles déjà payées
