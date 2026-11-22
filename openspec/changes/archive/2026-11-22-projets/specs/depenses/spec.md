## ADDED Requirements

### Requirement: Dépense rattachée à un projet

Le système SHALL permettre de rattacher, à la saisie ou à la correction, une dépense à un projet en cours du même foyer, ou de retirer ce rattachement.

#### Scenario: Rattachement

- **WHEN** un membre saisit une dépense de 400 € en choisissant le projet « Salle de bain »
- **THEN** la dépense est enregistrée et apparaît dans la fiche du projet

#### Scenario: Projet terminé ou inconnu

- **WHEN** un membre rattache une dépense à un projet terminé, abandonné, inconnu ou d'un autre foyer
- **THEN** l'API répond `400 Bad Request`

#### Scenario: Projet supprimé

- **WHEN** le projet d'une dépense est supprimé
- **THEN** la dépense reste, sans projet, et compte de nouveau dans le budget du mois
