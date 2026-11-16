## Purpose

Tenir une liste de courses unique par foyer, que tous ses membres alimentent et cochent, et qui reste à jour sur chaque appareil sans action de leur part.

## ADDED Requirements

### Requirement: Ajout d'un article

Le système SHALL permettre à un membre d'ajouter un article à la liste de son foyer avec un libellé de 1 à 60 caractères. L'article est créé non coché. Deux articles peuvent porter le même libellé.

#### Scenario: Nouvel article

- **WHEN** un membre ajoute « 2 l de lait »
- **THEN** l'article apparaît non coché dans la liste du foyer

#### Scenario: Libellé invalide

- **WHEN** un membre ajoute un article au libellé vide ou de plus de 60 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte ou modifie sa liste
- **THEN** l'API répond `404 Not Found`

### Requirement: Consultation de la liste

Le système SHALL permettre à un membre de consulter la liste de son foyer : d'abord les articles non cochés, du plus ancien au plus récent, puis les articles cochés, du dernier coché au premier.

#### Scenario: Ordre de la liste

- **WHEN** la liste contient « Pain » puis « Lait » non cochés, et « Œufs » coché
- **THEN** le membre obtient « Pain », « Lait », puis « Œufs » signalé coché

#### Scenario: Partage

- **WHEN** un membre ajoute un article puis un autre membre du foyer consulte la liste
- **THEN** le second membre voit l'article

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

Le système SHALL permettre à un membre de supprimer un article de la liste de son foyer, et de supprimer d'un coup tous les articles cochés.

#### Scenario: Article supprimé

- **WHEN** un membre supprime « Pain »
- **THEN** l'article n'apparaît plus dans la liste

#### Scenario: Fin des courses

- **WHEN** un membre vide les articles cochés d'une liste qui contient deux articles cochés et un non coché
- **THEN** seul l'article non coché reste dans la liste

### Requirement: Synchronisation entre membres

La PWA SHALL rafraîchir la liste affichée au plus 5 secondes après une modification faite par un autre membre, tant que l'écran de la liste est visible, et immédiatement quand il redevient visible.

#### Scenario: Ajout par l'autre membre

- **WHEN** un membre a l'écran de la liste ouvert et qu'un autre membre ajoute un article
- **THEN** l'article apparaît sur l'écran du premier membre dans les 5 secondes, sans action de sa part
