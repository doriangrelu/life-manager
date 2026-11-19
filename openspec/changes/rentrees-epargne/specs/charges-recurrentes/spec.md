## ADDED Requirements

### Requirement: Sens d'un mouvement récurrent

Une charge récurrente SHALL avoir un sens : dépense (par défaut, toutes les charges existantes), rentrée ou épargne. Une échéance payée SHALL produire dans le budget, selon son sens, une dépense, une rentrée ou une épargne, une seule fois par échéance. La PWA SHALL présenter les mouvements récurrents dans un même écran, filtrable par sens.

#### Scenario: Charge existante

- **WHEN** la nouvelle version démarre avec la charge « Internet »
- **THEN** son sens est « dépense » et son comportement ne change pas

#### Scenario: Rentrée récurrente payée

- **WHEN** l'échéance d'un salaire récurrent de 2 450 € est payée
- **THEN** une rentrée de 2 450 € apparaît dans le mois, et aucune dépense
