## MODIFIED Requirements

### Requirement: Consultation de la liste

Le système SHALL permettre à un membre de consulter une sous-liste de son foyer : d'abord les articles non cochés, dans l'ordre choisi par le foyer (un nouvel article arrive en dernier), puis les articles cochés, du dernier coché au premier.

#### Scenario: Ordre de la liste

- **WHEN** la sous-liste contient « Pain » puis « Lait » non cochés, et « Œufs » coché
- **THEN** le membre obtient « Pain », « Lait », puis « Œufs » signalé coché

#### Scenario: Partage

- **WHEN** un membre ajoute un article à une sous-liste puis un autre membre du foyer la consulte
- **THEN** le second membre voit l'article

#### Scenario: Sous-listes séparées

- **WHEN** un membre ajoute « Vis de 6 » à « Bricolage » puis consulte « Courses »
- **THEN** « Vis de 6 » n'apparaît pas dans « Courses »

### Requirement: Gestion des sous-listes

Le système SHALL permettre à un membre de lister les sous-listes de son foyer (dans l'ordre choisi par le foyer, une nouvelle sous-liste arrivant en dernier, avec le nombre d'articles non cochés de chacune), d'en ajouter, d'en renommer et d'en supprimer. Un nom fait 1 à 30 caractères et est unique dans le foyer sans tenir compte des majuscules. Supprimer une sous-liste supprime ses articles et son historique.

#### Scenario: Nouvelle sous-liste

- **WHEN** un membre ajoute la sous-liste « Pharmacie »
- **THEN** elle apparaît, vide, en dernier parmi les sous-listes du foyer

#### Scenario: Nom déjà pris

- **WHEN** un membre ajoute ou renomme une sous-liste en « courses » alors que « Courses » existe
- **THEN** l'opération est refusée avec une erreur `409 Conflict`

#### Scenario: Suppression

- **WHEN** un membre supprime la sous-liste « Bricolage » qui contient des articles
- **THEN** la sous-liste, ses articles et son historique disparaissent

#### Scenario: Sous-liste d'un autre foyer

- **WHEN** un membre consulte, renomme ou supprime une sous-liste inconnue ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

## ADDED Requirements

### Requirement: Ordre choisi par le foyer

Le système SHALL permettre à un membre de réordonner les articles non cochés d'une sous-liste et les sous-listes de son foyer ; le nouvel ordre SHALL être vu par tous les membres. La PWA SHALL proposer ce réordonnancement par glisser-déposer, à la souris, au doigt (par une poignée, sans gêner le défilement) et au clavier.

#### Scenario: Déplacer un article

- **WHEN** la sous-liste « Courses » contient « Pain », « Lait », « Beurre » et un membre glisse « Beurre » en premier
- **THEN** tous les membres voient « Beurre », « Pain », « Lait »

#### Scenario: Déplacer une sous-liste

- **WHEN** un membre glisse « Bricolage » avant « Courses »
- **THEN** les pastilles des sous-listes s'affichent « Bricolage », « Courses », « Vêtements » pour tout le foyer

#### Scenario: Article d'une autre sous-liste

- **WHEN** l'ordre envoyé contient un article inconnu ou d'une autre sous-liste
- **THEN** l'API répond `404 Not Found` et rien n'est modifié
