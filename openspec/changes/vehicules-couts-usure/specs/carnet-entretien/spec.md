## ADDED Requirements

### Requirement: Coût d'une opération dans le budget

Le système SHALL permettre, à l'ajout d'une opération avec un coût, de l'envoyer au budget avec une catégorie et un payeur ; une dépense du même montant est alors créée pour tout le foyer, à la date de l'opération.

#### Scenario: Révision ajoutée au budget

- **WHEN** un membre ajoute une révision de 320 € à la « Clio » en cochant « Ajouter au budget », catégorie Transport, payée par lui
- **THEN** une dépense de 320 € « révision · clio » apparaît dans le budget du mois de l'opération

#### Scenario: Sans coût

- **WHEN** un membre envoie au budget une opération sans coût ou à 0 €
- **THEN** l'API répond `400 Bad Request`

#### Scenario: Payeur hors du foyer

- **WHEN** le payeur choisi n'est pas membre du foyer
- **THEN** l'API répond `400 Bad Request` et rien n'est enregistré

### Requirement: Ancienne opération ajoutée au budget

Le système SHALL permettre d'envoyer au budget, après coup, une opération déjà saisie qui a un coût, une seule fois.

#### Scenario: Après coup

- **WHEN** un membre clique « Ajouter au budget » sur une ancienne opération de 80 €
- **THEN** une dépense de 80 € est créée et l'opération porte la mention « Dans le budget »

#### Scenario: Déjà dans le budget

- **WHEN** l'opération est déjà dans le budget
- **THEN** l'API répond `409 Conflict` et aucune deuxième dépense n'est créée

### Requirement: Dépense retirée avec l'opération

Le système SHALL supprimer la dépense d'une opération quand l'opération est supprimée ; la suppression du véhicule garde les dépenses.

#### Scenario: Opération supprimée

- **WHEN** un membre supprime une opération envoyée au budget
- **THEN** sa dépense disparaît du budget

#### Scenario: Véhicule vendu

- **WHEN** un membre supprime le véhicule
- **THEN** les dépenses déjà créées restent dans le budget
