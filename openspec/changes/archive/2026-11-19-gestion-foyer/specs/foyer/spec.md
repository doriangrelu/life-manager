## ADDED Requirements

### Requirement: Propriétaire du foyer

Chaque foyer SHALL avoir un seul propriétaire, membre du foyer : son créateur, ou pour un foyer existant son plus ancien membre. Le propriétaire SHALL pouvoir transmettre ce rôle à un autre membre. La consultation d'un foyer SHALL indiquer son propriétaire. Une action réservée au propriétaire, demandée par un autre membre, SHALL être refusée avec une erreur `403 Forbidden`.

#### Scenario: Créateur propriétaire

- **WHEN** un utilisateur crée le foyer « Maison »
- **THEN** il en est le propriétaire

#### Scenario: Transmission

- **WHEN** le propriétaire de « Maison » transmet la propriété à un autre membre
- **THEN** ce membre devient propriétaire et l'ancien propriétaire reste simple membre

#### Scenario: Action réservée

- **WHEN** un membre qui n'est pas propriétaire tente de retirer un autre membre
- **THEN** l'API répond `403 Forbidden` et rien ne change

### Requirement: Retrait d'un membre

Le propriétaire SHALL pouvoir retirer un autre membre du foyer. Le membre retiré SHALL perdre aussitôt l'accès au foyer et à toutes ses données, dans tous les domaines. Ses dépenses passées SHALL rester, attribuées à un « Ancien membre ». Les charges récurrentes qu'il payait SHALL être payées par le propriétaire, et celles dont il était bénéficiaire SHALL devenir communes.

#### Scenario: Retrait

- **WHEN** le propriétaire retire Bob du foyer « Maison »
- **THEN** Bob ne voit plus « Maison » et toute requête de Bob sur ce foyer répond `404 Not Found`

#### Scenario: Données conservées

- **WHEN** Bob, retiré, avait payé une dépense de 20 € en novembre
- **THEN** la dépense reste dans le budget de novembre, payée par « Ancien membre »

#### Scenario: Le propriétaire ne se retire pas

- **WHEN** le propriétaire tente de se retirer lui-même
- **THEN** la demande est refusée avec une erreur `409 Conflict`

### Requirement: Départ d'un membre

Un membre qui n'est pas propriétaire SHALL pouvoir quitter un foyer, avec les mêmes effets qu'un retrait. Le propriétaire SHALL d'abord transmettre la propriété ou supprimer le foyer.

#### Scenario: Départ

- **WHEN** Bob quitte le foyer « Maison »
- **THEN** il n'en est plus membre et n'y a plus accès

#### Scenario: Propriétaire

- **WHEN** le propriétaire tente de quitter son foyer
- **THEN** la demande est refusée avec une erreur `409 Conflict`

### Requirement: Annulation d'une invitation

Le propriétaire SHALL pouvoir annuler le code d'invitation actif de son foyer ; le code annulé SHALL être refusé comme un code inconnu.

#### Scenario: Code annulé

- **WHEN** le propriétaire annule le code actif puis quelqu'un saisit ce code
- **THEN** l'adhésion est refusée avec une erreur `400 Bad Request`

#### Scenario: Membre non propriétaire

- **WHEN** un membre qui n'est pas propriétaire tente d'annuler le code
- **THEN** l'API répond `403 Forbidden` et le code reste valable

### Requirement: Renommage du foyer

Le propriétaire SHALL pouvoir renommer son foyer, avec les mêmes règles de nom qu'à la création.

#### Scenario: Renommage

- **WHEN** le propriétaire renomme « Maison » en « Maison Grelu »
- **THEN** tous les membres voient le foyer « Maison Grelu »

#### Scenario: Membre non propriétaire

- **WHEN** un membre qui n'est pas propriétaire tente de renommer le foyer
- **THEN** l'API répond `403 Forbidden`

### Requirement: Suppression d'un foyer

Le propriétaire SHALL pouvoir supprimer son foyer en confirmant son nom. La suppression SHALL être définitive et SHALL effacer toutes les données du foyer dans tous les domaines (membres, invitations, dépenses, catégories, charges, factures, véhicules, carnets, pièces jointes, courses, historique, rappels), fichiers compris, y compris après un arrêt du serveur pendant l'opération.

#### Scenario: Suppression

- **WHEN** le propriétaire supprime « Maison » en confirmant le nom « maison »
- **THEN** le foyer n'existe plus pour aucun de ses anciens membres et aucune de ses données ni aucun de ses fichiers ne subsiste

#### Scenario: Nom mal confirmé

- **WHEN** le propriétaire demande la suppression en confirmant un autre nom
- **THEN** la suppression est refusée avec une erreur `400 Bad Request` et rien n'est supprimé

## MODIFIED Requirements

### Requirement: Invitation par code

Le système SHALL permettre au propriétaire d'un foyer, et à lui seul, de générer un code d'invitation à usage unique pour ce foyer, valable 7 jours. La génération d'un nouveau code SHALL invalider le code précédent non utilisé de ce foyer.

#### Scenario: Génération d'un code

- **WHEN** le propriétaire demande un code d'invitation pour son foyer
- **THEN** un code court et difficilement devinable est retourné avec sa date d'expiration

#### Scenario: Régénération

- **WHEN** le propriétaire génère un nouveau code alors qu'un code précédent n'a pas été utilisé
- **THEN** le code précédent n'est plus accepté

#### Scenario: Membre non propriétaire

- **WHEN** un membre qui n'est pas propriétaire demande un code d'invitation
- **THEN** l'API répond `403 Forbidden`

### Requirement: Consultation d'un foyer

Le système SHALL permettre à un membre de consulter un de ses foyers : nom, propriétaire, liste des membres (nom d'affichage, date d'adhésion). Le code d'invitation actif SHALL n'être renvoyé qu'au propriétaire.

#### Scenario: Consultation par un membre

- **WHEN** un membre consulte un de ses foyers
- **THEN** il obtient le nom du foyer, son propriétaire et la liste de ses membres, sans le code d'invitation

#### Scenario: Consultation par le propriétaire

- **WHEN** le propriétaire consulte son foyer alors qu'un code est actif
- **THEN** il obtient aussi ce code et sa date d'expiration

#### Scenario: Changement de nom d'un membre

- **WHEN** un membre modifie son nom d'affichage dans Keycloak puis utilise l'application
- **THEN** son nouveau nom apparaît dans la liste des membres de chacun de ses foyers

### Requirement: Parcours foyer dans la PWA

La PWA SHALL charger la liste des foyers de l'utilisateur via l'API du domaine foyer. Elle SHALL proposer à un utilisateur connecté sans foyer de créer un foyer ou d'en rejoindre un avec un code. Elle SHALL permettre à un membre de plusieurs foyers de choisir le foyer affiché, mémoriser ce choix sur l'appareil, et à tout moment créer ou rejoindre un autre foyer. L'écran d'un foyer SHALL afficher ses membres et son propriétaire, permettre à un membre de quitter le foyer, et au propriétaire de renommer le foyer, de générer, copier et annuler le code d'invitation, de transmettre la propriété, de retirer un membre et de supprimer le foyer après avoir retapé son nom. Quand le foyer affiché n'est plus accessible, la PWA SHALL basculer sur un autre foyer de l'utilisateur, ou sur l'écran « Créer ou rejoindre un foyer » s'il n'en a plus, avec un message explicatif.

#### Scenario: Premier lancement sans foyer

- **WHEN** un utilisateur connecté qui n'a aucun foyer ouvre la PWA
- **THEN** l'écran « Créer ou rejoindre un foyer » s'affiche

#### Scenario: Membre de plusieurs foyers

- **WHEN** un membre de plusieurs foyers ouvre la PWA
- **THEN** le dernier foyer choisi sur cet appareil s'affiche, et il peut basculer vers un autre foyer

#### Scenario: Foyer devenu inaccessible

- **WHEN** Bob a la PWA ouverte sur « Maison » et que le propriétaire le retire du foyer
- **THEN** à sa prochaine action, la PWA lui indique qu'il n'a plus accès à « Maison » et affiche un autre de ses foyers, ou l'écran « Créer ou rejoindre un foyer »

#### Scenario: Suppression confirmée par le nom

- **WHEN** le propriétaire ouvre « Supprimer le foyer »
- **THEN** le bouton de suppression reste inactif tant que le nom du foyer n'est pas retapé à l'identique
