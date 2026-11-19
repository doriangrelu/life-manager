## ADDED Requirements

### Requirement: Conditions d'utilisation acceptées

Le système SHALL publier des conditions d'utilisation et une politique de confidentialité lisibles sans être connecté. Il SHALL demander à chaque utilisateur d'accepter la version en vigueur des conditions avant d'utiliser l'application, à la première connexion puis à chaque nouvelle version, et SHALL garder la version acceptée et sa date.

#### Scenario: Première connexion

- **WHEN** un utilisateur qui n'a jamais accepté les conditions se connecte
- **THEN** la PWA lui montre les conditions et n'ouvre l'application qu'après « J'accepte »

#### Scenario: Nouvelle version

- **WHEN** la version des conditions change
- **THEN** chaque utilisateur doit les accepter de nouveau à sa prochaine visite

#### Scenario: Version périmée

- **WHEN** un client envoie l'acceptation d'une autre version que celle en vigueur
- **THEN** l'API répond `400 Bad Request`

### Requirement: Export des données personnelles

Le système SHALL permettre à un utilisateur de télécharger en JSON les données qu'il a lui-même saisies : son profil, ses foyers (nom, rôle, date d'arrivée) et, dans chaque domaine, ce dont il est l'auteur (dépenses, rentrées, catégories, budgets prévus, récurrents créés ou payés par lui, véhicules, entretiens, listes et articles de courses, rappels et rappels faits), les fichiers décrits sans leur contenu. L'export MUST NOT contenir de secret (codes d'invitation, mot de passe, jetons), ni d'identifiant technique autre que le lien vers le foyer, ni les données des autres membres. Un utilisateur peut faire au plus un export par minute.

#### Scenario: Export

- **WHEN** un utilisateur demande ses données
- **THEN** il reçoit un fichier JSON avec son profil, ses foyers et ce qu'il a saisi

#### Scenario: Données des autres

- **WHEN** un autre membre du foyer a saisi une dépense
- **THEN** cette dépense n'apparaît pas dans l'export de l'utilisateur

#### Scenario: Trop d'exports

- **WHEN** un utilisateur demande un second export dans la même minute
- **THEN** l'API répond `429 Too Many Requests`

### Requirement: Suppression du compte

Le système SHALL permettre à un utilisateur de supprimer son compte après avoir retapé son e-mail. Chaque foyer dont il est le seul membre est supprimé avec ses données ; un foyer dont il est propriétaire avec d'autres membres passe au membre de son choix, ou à défaut au membre arrivé le plus tôt ; il quitte tous ses autres foyers. Son profil est effacé et son compte de connexion Keycloak supprimé.

#### Scenario: Confirmation incorrecte

- **WHEN** l'e-mail retapé ne correspond pas
- **THEN** l'API répond `400 Bad Request` et rien n'est supprimé

#### Scenario: Seul membre

- **WHEN** un utilisateur seul dans son foyer supprime son compte
- **THEN** le foyer et toutes ses données sont supprimés

#### Scenario: Propriétaire sans choix

- **WHEN** un propriétaire supprime son compte sans désigner de nouveau propriétaire
- **THEN** le foyer passe au membre arrivé le plus tôt, qui garde toutes les données

#### Scenario: Compte de connexion

- **WHEN** la suppression aboutit
- **THEN** le compte Keycloak de l'utilisateur n'existe plus
