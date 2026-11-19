## ADDED Requirements

### Requirement: Crédit

Le système SHALL permettre de marquer une dépense récurrente comme un crédit, avec la mensualité de son assurance (0 si aucune), comprise dans le montant payé chaque mois. Un crédit est une dépense mensuelle à montant fixe, et chaque échéance produit une seule dépense du budget, égale au montant total.

#### Scenario: Crédit avec assurance

- **WHEN** un membre ajoute le crédit « Prêt immobilier » de 612,40 € par mois, dont 24 € d'assurance
- **THEN** la charge est enregistrée comme un crédit avec 24 € d'assurance, et chaque échéance produit une dépense de 612,40 €

#### Scenario: Crédit incohérent

- **WHEN** un membre ajoute un crédit à montant variable, non mensuel, en rentrée, ou dont l'assurance dépasse le montant total
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

### Requirement: Dernière échéance

Le système SHALL accepter, pour une charge récurrente, une dernière échéance facultative, jamais antérieure à la première. Aucune échéance n'est prévue ni annoncée après elle, et la charge passe à l'état terminé une fois la dernière échéance arrivée.

#### Scenario: Fin du crédit

- **WHEN** la dernière échéance d'un crédit arrive
- **THEN** elle produit sa dépense, aucune échéance suivante n'est prévue, et le crédit n'est plus actif

#### Scenario: Prévisions bornées

- **WHEN** un membre demande les échéances attendues d'une période qui dépasse la dernière échéance d'un crédit
- **THEN** il n'obtient aucune échéance de ce crédit après cette date

#### Scenario: Dates incohérentes

- **WHEN** un membre saisit une dernière échéance antérieure à la première
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Crédits dans la PWA

La PWA SHALL proposer, à l'ajout d'une dépense récurrente, le choix entre « Dépense » et « Crédit ». Pour un crédit, elle demande la mensualité du prêt, la mensualité de l'assurance (facultative) et la date de la dernière mensualité (facultative), puis affiche sur sa ligne le total mensuel, le détail prêt et assurance, et, avec une date de fin, le nombre de mensualités restantes et le mois de fin.

#### Scenario: Saisie d'un crédit

- **WHEN** un membre choisit « Crédit » et saisit 588,40 € de prêt, 24 € d'assurance et une dernière mensualité en mars 2049
- **THEN** le crédit apparaît dans Récurrents à 612,40 € par mois, avec le détail « prêt 588,40 € + assurance 24,00 € », le nombre de mensualités restantes et « fin en mars 2049 »

#### Scenario: Crédit terminé

- **WHEN** la dernière mensualité d'un crédit est passée
- **THEN** le crédit apparaît parmi les charges terminées
