## MODIFIED Requirements

### Requirement: Liste des catégories

Le système SHALL permettre à un membre de lister les catégories de son foyer, triées par nom, en indiquant pour chacune si elle est archivée. La catégorie nommée « Autre », qui sert de dernier recours, SHALL toujours être placée en dernier.

#### Scenario: Consultation

- **WHEN** un membre consulte les catégories d'un foyer comportant une catégorie archivée
- **THEN** il obtient toutes les catégories, la catégorie archivée étant signalée comme telle

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte ses catégories
- **THEN** l'API répond `404 Not Found`

#### Scenario: « Autre » en dernier

- **WHEN** un membre consulte les catégories d'un foyer qui a « Abonnements », « Autre », « Courses » et « Transport »
- **THEN** il obtient « Abonnements », « Courses », « Transport », puis « Autre »
