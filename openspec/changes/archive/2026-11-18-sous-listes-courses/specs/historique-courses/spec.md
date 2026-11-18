## Purpose

Remonter rapidement une liste de courses à partir des articles déjà achetés, sous-liste par sous-liste, sans tout retaper.

## ADDED Requirements

### Requirement: Historique des achats

Le système SHALL tenir, pour chaque sous-liste, un historique des articles achetés : chaque libellé n'y figure qu'une fois (sans tenir compte des majuscules), avec la date de son dernier achat et le nombre de fois où il a été acheté. L'historique est trié du plus récemment acheté au plus ancien.

#### Scenario: Premier achat

- **WHEN** un membre vide les articles cochés « Lait » et « Pain » de « Courses »
- **THEN** l'historique de « Courses » contient « Lait » et « Pain », achetés une fois, à la date du vidage

#### Scenario: Article racheté

- **WHEN** « lait » est de nouveau coché puis vidé dans « Courses »
- **THEN** l'historique contient un seul « Lait », acheté deux fois, en tête de l'historique

#### Scenario: Historique propre à la sous-liste

- **WHEN** un membre consulte l'historique de « Bricolage »
- **THEN** il n'y voit aucun article acheté dans « Courses »

### Requirement: Remise en liste depuis l'historique

Le système SHALL permettre à un membre de remettre dans une sous-liste, en une seule opération, un ou plusieurs libellés de son historique : chacun redevient un article non coché de la sous-liste.

#### Scenario: Liste de la semaine

- **WHEN** un membre sélectionne « Lait », « Pain » et « Œufs » dans l'historique de « Courses » et les remet en liste
- **THEN** les trois articles apparaissent non cochés dans « Courses »

#### Scenario: Libellé inconnu

- **WHEN** un membre demande de remettre en liste un élément d'historique inconnu ou d'une autre sous-liste
- **THEN** l'API répond `404 Not Found` et aucun article n'est ajouté

### Requirement: Oubli d'un article de l'historique

Le système SHALL permettre à un membre de retirer définitivement un libellé de l'historique d'une sous-liste.

#### Scenario: Article qu'on n'achète plus

- **WHEN** un membre retire « Couches » de l'historique de « Courses »
- **THEN** « Couches » n'apparaît plus dans l'historique

### Requirement: Historique dans la PWA

La PWA SHALL proposer, depuis une sous-liste, un écran d'historique où l'on coche plusieurs articles avant de les remettre en liste d'un geste.

#### Scenario: Sélection multiple

- **WHEN** un membre ouvre l'historique de « Courses », sélectionne trois articles et valide
- **THEN** la sous-liste « Courses » s'affiche avec les trois articles à acheter
