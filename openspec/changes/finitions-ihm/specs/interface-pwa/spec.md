## ADDED Requirements

### Requirement: Retours visuels de chargement et d'attente

La PWA SHALL afficher des formes de chargement à la place d'un contenu en cours de premier chargement, et SHALL n'afficher un message de liste vide qu'une fois les données reçues. Un bouton dont l'action est en cours SHALL l'indiquer par une animation et rester inactif jusqu'à la fin. Les éléments cliquables SHALL réagir au survol de la souris.

#### Scenario: Ouverture d'un onglet

- **WHEN** un membre ouvre l'onglet Véhicules et que la liste n'est pas encore reçue
- **THEN** des formes de chargement s'affichent, et non le message « Aucun véhicule »

#### Scenario: Enregistrement en cours

- **WHEN** un membre enregistre une dépense
- **THEN** le bouton affiche une animation d'attente et ne peut pas être actionné une seconde fois

### Requirement: Saisie du montant

La fenêtre de saisie d'une dépense SHALL centrer le montant et sa devise, et SHALL placer le curseur dans le champ du montant à l'ouverture.

#### Scenario: Nouvelle dépense

- **WHEN** un membre ouvre la saisie d'une dépense
- **THEN** il peut taper le montant immédiatement, affiché centré avec « € »
