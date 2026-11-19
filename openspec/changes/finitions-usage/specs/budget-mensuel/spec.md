## MODIFIED Requirements

### Requirement: Écran du mois dans la PWA

La PWA SHALL afficher la vue du mois en cours pour le foyer sélectionné, avec les noms des membres, et permettre de passer au mois précédent ou suivant, de revenir en un geste au mois en cours depuis un autre mois, ainsi que de modifier ou supprimer une dépense depuis la liste.

#### Scenario: Navigation entre les mois

- **WHEN** un membre ouvre l'écran du mois puis demande le mois précédent
- **THEN** la vue du mois précédent s'affiche

#### Scenario: Retour au mois en cours

- **WHEN** un membre consulte le budget de juin 2026 alors qu'on est en novembre 2026 et touche « Aujourd'hui »
- **THEN** la vue de novembre 2026 s'affiche et le bouton « Aujourd'hui » disparaît
