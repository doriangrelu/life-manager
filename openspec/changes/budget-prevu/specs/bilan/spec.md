## ADDED Requirements

### Requirement: Reste à vivre de la période

Le système SHALL renvoyer avec le bilan le total des dépenses de la période nées d'une dépense récurrente, et la PWA SHALL afficher le reste à vivre de la période : rentrées prévues moins dépenses récurrentes (payées et à venir), et ce qu'il en reste une fois les dépenses courantes déduites.

#### Scenario: Trimestre en cours

- **WHEN** un membre consulte le bilan du trimestre en cours
- **THEN** il voit le reste à vivre du trimestre et ce qu'il en reste, calculés sur le réalisé et sur les échéances à venir de la période

#### Scenario: Période passée

- **WHEN** un membre consulte le bilan d'une période terminée
- **THEN** le reste à vivre est calculé sur le seul réalisé
