## ADDED Requirements

### Requirement: Création de foyers

Le système SHALL permettre à tout utilisateur authentifié de créer un foyer en lui donnant un nom ; le créateur en devient automatiquement membre. Un utilisateur SHALL pouvoir être membre de plusieurs foyers, dans la limite de 10.

#### Scenario: Création d'un premier foyer

- **WHEN** un utilisateur sans foyer crée un foyer nommé « Maison »
- **THEN** le foyer « Maison » est créé et l'utilisateur en est l'unique membre

#### Scenario: Création d'un foyer supplémentaire

- **WHEN** un utilisateur déjà membre du foyer « Maison » crée un foyer nommé « Maison de famille »
- **THEN** le foyer est créé et l'utilisateur est membre des deux foyers

#### Scenario: Limite atteinte

- **WHEN** un utilisateur membre de 10 foyers tente d'en créer un autre
- **THEN** la création est refusée avec une erreur `409 Conflict`

#### Scenario: Nom invalide

- **WHEN** un utilisateur tente de créer un foyer avec un nom vide ou de plus de 100 caractères
- **THEN** la création est refusée avec une erreur `400 Bad Request`

### Requirement: Liste des foyers de l'utilisateur

Le système SHALL permettre à un utilisateur de lister les foyers dont il est membre.

#### Scenario: Liste

- **WHEN** un utilisateur membre de deux foyers demande la liste de ses foyers
- **THEN** il obtient exactement ces deux foyers

### Requirement: Invitation par code

Le système SHALL permettre à un membre d'un foyer de générer un code d'invitation à usage unique pour ce foyer, valable 7 jours. La génération d'un nouveau code SHALL invalider le code précédent non utilisé de ce foyer.

#### Scenario: Génération d'un code

- **WHEN** un membre demande un code d'invitation pour l'un de ses foyers
- **THEN** un code court et difficilement devinable est retourné avec sa date d'expiration

#### Scenario: Régénération

- **WHEN** un membre génère un nouveau code alors qu'un code précédent n'a pas été utilisé
- **THEN** le code précédent n'est plus accepté

### Requirement: Adhésion à un foyer

Le système SHALL permettre à un utilisateur de rejoindre un foyer en saisissant un code d'invitation valide, qu'il soit déjà membre d'autres foyers ou non. Un code SHALL être consommé à sa première utilisation réussie.

#### Scenario: Code valide

- **WHEN** un utilisateur saisit un code valide, non expiré et non utilisé, d'un foyer dont il n'est pas membre
- **THEN** il devient membre de ce foyer, en plus de ses éventuels autres foyers, et le code est marqué comme utilisé

#### Scenario: Code invalide, expiré ou déjà utilisé

- **WHEN** un utilisateur saisit un code inconnu, expiré ou déjà utilisé
- **THEN** l'adhésion est refusée avec une erreur `400 Bad Request` et un message ne révélant pas lequel de ces cas s'applique

#### Scenario: Déjà membre du foyer

- **WHEN** un utilisateur saisit un code valide d'un foyer dont il est déjà membre
- **THEN** l'adhésion est refusée avec une erreur `409 Conflict` et le code reste utilisable

#### Scenario: Limite atteinte

- **WHEN** un utilisateur membre de 10 foyers saisit un code valide
- **THEN** l'adhésion est refusée avec une erreur `409 Conflict` et le code reste utilisable

### Requirement: Consultation d'un foyer

Le système SHALL permettre à un membre de consulter un de ses foyers : nom, liste des membres (nom d'affichage, date d'adhésion) et code d'invitation actif s'il existe.

#### Scenario: Consultation par un membre

- **WHEN** un membre consulte un de ses foyers
- **THEN** il obtient le nom du foyer et la liste de ses membres

### Requirement: Cloisonnement des données par foyer

Le système SHALL vérifier, pour toute requête ciblant un foyer, que l'utilisateur authentifié en est membre avant tout traitement, et SHALL traiter un foyer dont il n'est pas membre, ainsi que toutes ses ressources, comme inexistant.

#### Scenario: Accès à un foyer dont on n'est pas membre

- **WHEN** un utilisateur cible un foyer existant dont il n'est pas membre
- **THEN** l'API répond `404 Not Found`, sans révéler l'existence du foyer

#### Scenario: Accès à la ressource d'un autre foyer

- **WHEN** un membre du foyer A tente d'accéder à une ressource du foyer B dont il n'est pas membre
- **THEN** l'API répond `404 Not Found`, sans révéler l'existence de la ressource

### Requirement: Parcours foyer dans la PWA

La PWA SHALL proposer à un utilisateur connecté sans foyer de créer un foyer ou d'en rejoindre un avec un code. Elle SHALL permettre à un membre de plusieurs foyers de choisir le foyer affiché, mémoriser ce choix sur l'appareil, et à tout moment créer ou rejoindre un autre foyer. L'écran d'un foyer SHALL afficher ses membres et permettre de générer et copier un code d'invitation.

#### Scenario: Premier lancement sans foyer

- **WHEN** un utilisateur connecté qui n'a aucun foyer ouvre la PWA
- **THEN** l'écran « Créer ou rejoindre un foyer » s'affiche

#### Scenario: Membre de plusieurs foyers

- **WHEN** un membre de plusieurs foyers ouvre la PWA
- **THEN** le dernier foyer choisi sur cet appareil s'affiche, et il peut basculer vers un autre foyer
