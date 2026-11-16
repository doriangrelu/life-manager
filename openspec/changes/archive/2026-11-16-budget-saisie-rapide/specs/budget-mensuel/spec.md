## Purpose

Donner aux membres d'un foyer une vue claire de leurs dépenses d'un mois : combien au total, dans quelles catégories, payé par qui et pour qui.

## ADDED Requirements

### Requirement: Total et répartition par catégorie

Le système SHALL fournir, pour un foyer et un mois donnés, le total des dépenses datées de ce mois et le total par catégorie (catégories archivées comprises), du plus grand au plus petit montant.

#### Scenario: Mois avec dépenses

- **WHEN** un membre consulte un mois comportant 30 € et 25 € en « Courses » et 45 € en « Transport »
- **THEN** il obtient un total de 100 €, puis « Courses » 55 € et « Transport » 45 €, triés par montant décroissant

#### Scenario: Mois sans dépense

- **WHEN** un membre consulte un mois sans aucune dépense
- **THEN** il obtient un total de 0 € et des répartitions vides

### Requirement: Répartition par personne

Le système SHALL fournir, pour le même mois, le total payé par chaque membre et le total des dépenses faites pour chaque bénéficiaire, « commun » compris.

#### Scenario: Payé par et pour

- **WHEN** Alice a payé 60 € pour le commun et Bob 40 € pour Alice dans le mois
- **THEN** la vue indique Alice 60 € et Bob 40 € côté payeurs, commun 60 € et Alice 40 € côté bénéficiaires

### Requirement: Détail des dépenses du mois

Le système SHALL fournir la liste des dépenses du mois avec toutes leurs informations, de la plus récente à la plus ancienne selon leur date.

#### Scenario: Liste

- **WHEN** un membre consulte le détail d'un mois
- **THEN** il obtient chaque dépense du mois (montant, catégorie, date, payeur, bénéficiaire, libellé), la plus récente en premier

### Requirement: Cloisonnement de la vue mensuelle

Le système SHALL réserver la vue mensuelle d'un foyer à ses membres.

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte sa vue mensuelle
- **THEN** l'API répond `404 Not Found`

### Requirement: Écran du mois dans la PWA

La PWA SHALL afficher la vue du mois en cours pour le foyer sélectionné, avec les noms des membres, et permettre de passer au mois précédent ou suivant, ainsi que de modifier ou supprimer une dépense depuis la liste.

#### Scenario: Navigation entre les mois

- **WHEN** un membre ouvre l'écran du mois puis demande le mois précédent
- **THEN** la vue du mois précédent s'affiche
