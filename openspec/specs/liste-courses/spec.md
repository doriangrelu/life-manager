# liste-courses Specification

## Purpose
Tenir les listes de courses d'un foyer, rangées en sous-listes (« Courses », « Bricolage », « Vêtements »…), que tous ses membres alimentent et cochent, et qui restent à jour sur chaque appareil sans action de leur part.

## Requirements

### Requirement: Ajout d'un article

Le système SHALL permettre à un membre d'ajouter un article à une sous-liste de son foyer avec un libellé de 1 à 60 caractères. L'article est créé non coché. Deux articles peuvent porter le même libellé.

#### Scenario: Nouvel article

- **WHEN** un membre ajoute « 2 l de lait » à la sous-liste « Courses »
- **THEN** l'article apparaît non coché dans la sous-liste « Courses »

#### Scenario: Libellé invalide

- **WHEN** un membre ajoute un article au libellé vide ou de plus de 60 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte ou modifie ses listes
- **THEN** l'API répond `404 Not Found`

### Requirement: Consultation de la liste

Le système SHALL permettre à un membre de consulter une sous-liste de son foyer : d'abord les articles non cochés, du plus ancien au plus récent, puis les articles cochés, du dernier coché au premier.

#### Scenario: Ordre de la liste

- **WHEN** la sous-liste contient « Pain » puis « Lait » non cochés, et « Œufs » coché
- **THEN** le membre obtient « Pain », « Lait », puis « Œufs » signalé coché

#### Scenario: Partage

- **WHEN** un membre ajoute un article à une sous-liste puis un autre membre du foyer la consulte
- **THEN** le second membre voit l'article

#### Scenario: Sous-listes séparées

- **WHEN** un membre ajoute « Vis de 6 » à « Bricolage » puis consulte « Courses »
- **THEN** « Vis de 6 » n'apparaît pas dans « Courses »

### Requirement: Cochage d'un article

Le système SHALL permettre à un membre de cocher un article de la liste de son foyer, et de le décocher. Cocher un article déjà coché, ou décocher un article non coché, est sans effet.

#### Scenario: Article pris

- **WHEN** un membre coche « Lait »
- **THEN** l'article est signalé coché et passe avec les articles cochés

#### Scenario: Erreur de cochage

- **WHEN** un membre décoche un article coché
- **THEN** l'article redevient non coché

#### Scenario: Article d'un autre foyer

- **WHEN** un membre coche un article inconnu ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Suppression d'articles

Le système SHALL permettre à un membre de supprimer définitivement un article d'une sous-liste de son foyer, et de vider d'un coup les articles cochés d'une sous-liste : ceux-ci quittent la sous-liste et sont rangés dans son historique des achats.

#### Scenario: Article supprimé

- **WHEN** un membre supprime « Pain »
- **THEN** l'article n'apparaît plus dans la sous-liste, ni dans son historique

#### Scenario: Fin des courses

- **WHEN** un membre vide les articles cochés d'une sous-liste qui contient deux articles cochés et un non coché
- **THEN** seul l'article non coché reste dans la sous-liste, et les deux articles cochés figurent dans son historique

### Requirement: Synchronisation entre membres

La PWA SHALL rafraîchir la liste affichée au plus 5 secondes après une modification faite par un autre membre, tant que l'écran de la liste est visible, et immédiatement quand il redevient visible.

#### Scenario: Ajout par l'autre membre

- **WHEN** un membre a l'écran de la liste ouvert et qu'un autre membre ajoute un article
- **THEN** l'article apparaît sur l'écran du premier membre dans les 5 secondes, sans action de sa part

### Requirement: Sous-listes par défaut

Le système SHALL doter chaque nouveau foyer de trois sous-listes de courses : « Courses », « Bricolage » et « Vêtements ».

#### Scenario: Nouveau foyer

- **WHEN** un utilisateur crée un foyer puis consulte ses sous-listes de courses
- **THEN** il obtient « Courses », « Bricolage » et « Vêtements », toutes vides

### Requirement: Gestion des sous-listes

Le système SHALL permettre à un membre de lister les sous-listes de son foyer (triées par nom, avec le nombre d'articles non cochés de chacune), d'en ajouter, d'en renommer et d'en supprimer. Un nom fait 1 à 30 caractères et est unique dans le foyer sans tenir compte des majuscules. Supprimer une sous-liste supprime ses articles et son historique.

#### Scenario: Nouvelle sous-liste

- **WHEN** un membre ajoute la sous-liste « Pharmacie »
- **THEN** elle apparaît, vide, parmi les sous-listes du foyer

#### Scenario: Nom déjà pris

- **WHEN** un membre ajoute ou renomme une sous-liste en « courses » alors que « Courses » existe
- **THEN** l'opération est refusée avec une erreur `409 Conflict`

#### Scenario: Suppression

- **WHEN** un membre supprime la sous-liste « Bricolage » qui contient des articles
- **THEN** la sous-liste, ses articles et son historique disparaissent

#### Scenario: Sous-liste d'un autre foyer

- **WHEN** un membre consulte, renomme ou supprime une sous-liste inconnue ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Choix de la sous-liste dans la PWA

La PWA SHALL afficher les sous-listes du foyer dans l'onglet Courses, avec le nombre d'articles à acheter de chacune, permettre d'en ouvrir une, de les gérer, et SHALL rouvrir la dernière sous-liste consultée sur l'appareil.

#### Scenario: Retour dans l'onglet

- **WHEN** un membre a consulté « Bricolage » puis revient plus tard dans l'onglet Courses
- **THEN** la sous-liste « Bricolage » est affichée
