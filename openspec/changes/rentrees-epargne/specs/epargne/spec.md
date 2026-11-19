## ADDED Requirements

### Requirement: Épargne mensuelle

Le système SHALL permettre à un membre d'enregistrer un montant mis de côté pour son foyer : montant strictement positif en centimes, date, libellé optionnel, et auteur (un membre ou le foyer). L'épargne SHALL être comptée à part des dépenses. Un membre SHALL pouvoir modifier et supprimer une épargne de son foyer, et voir le cumul épargné.

#### Scenario: Virement vers un livret

- **WHEN** un membre enregistre 200 € mis de côté le 2026-11-05
- **THEN** l'épargne de novembre 2026 vaut 200 €, le total des dépenses ne change pas, et le cumul épargné augmente de 200 €

### Requirement: Épargne récurrente

Le système SHALL permettre de déclarer une épargne récurrente (ex. virement automatique mensuel), avec les mêmes règles de calendrier que les charges récurrentes ; chaque échéance SHALL produire une seule épargne.

#### Scenario: Virement automatique

- **WHEN** une épargne récurrente de 150 € a son échéance le 5 du mois
- **THEN** le 5, 150 € sont enregistrés comme mis de côté pour le mois
