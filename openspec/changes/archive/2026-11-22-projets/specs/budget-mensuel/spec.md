## ADDED Requirements

### Requirement: Dépenses de projet hors budget

Le système SHALL écarter des totaux du mois, des catégories, du budget prévu, du reste à vivre et du bilan les dépenses rattachées à un projet dont l'option « compte dans le budget du mois » est décochée, tout en les gardant dans la liste des opérations du mois, marquées « Budget à part ».

#### Scenario: Projet hors budget

- **WHEN** une dépense de 800 € est rattachée au projet « Vacances », réglé hors budget, en juillet
- **THEN** le total dépensé de juillet, sa catégorie et le bilan ne comptent pas ces 800 €, et la dépense figure dans les opérations de juillet avec la mention « Budget à part »

#### Scenario: Projet compté

- **WHEN** une dépense de 400 € est rattachée au projet « Salle de bain », compté dans le budget
- **THEN** elle compte dans le total du mois, sa catégorie et le bilan comme toute dépense

#### Scenario: Changement d'option

- **WHEN** un membre décoche l'option d'un projet qui a déjà des dépenses
- **THEN** ces dépenses sortent des totaux de tous les mois concernés
