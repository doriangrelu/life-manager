# comptes-utilisateurs Specification

## Purpose
Identifier l'utilisateur de chaque requête : authentification déléguée à Keycloak avec des jetons opaques vérifiés par introspection, profil local tenu à jour depuis Keycloak, et endpoint « whoami » pour la PWA.

## Requirements

### Requirement: Authentification par jeton opaque introspecté

Le système SHALL déléguer l'inscription, la connexion et la réinitialisation de mot de passe à Keycloak via OpenID Connect. L'API backend SHALL traiter le jeton d'accès comme opaque : elle MUST NOT le décoder ni valider sa signature localement, et SHALL le vérifier exclusivement par l'endpoint d'introspection de Keycloak. Seuls les endpoints de santé sont accessibles sans jeton.

#### Scenario: Jeton actif

- **WHEN** un client appelle un endpoint de l'API avec un jeton que l'introspection Keycloak déclare actif pour l'audience attendue
- **THEN** la requête est traitée au nom de l'utilisateur identifié par le `sub` retourné par l'introspection

#### Scenario: Jeton absent, inactif ou révoqué

- **WHEN** un client appelle un endpoint de l'API sans jeton, ou avec un jeton que l'introspection déclare inactif (expiré, révoqué, inconnu)
- **THEN** l'API répond `401 Unauthorized` sans exécuter le traitement

#### Scenario: Keycloak indisponible

- **WHEN** l'introspection ne peut pas aboutir et aucun résultat valide n'est en cache pour ce jeton
- **THEN** l'API répond `503 Service Unavailable` et ne traite pas la requête

#### Scenario: Endpoint de santé public

- **WHEN** un client appelle l'endpoint de santé sans jeton
- **THEN** l'API répond sans exiger d'authentification

### Requirement: Profil utilisateur local

Le système SHALL créer automatiquement un profil utilisateur local lors du premier appel authentifié d'un utilisateur, lié de manière unique à son identifiant Keycloak (`sub`), et SHALL renseigner l'email et le nom d'affichage à partir de la réponse d'introspection.

#### Scenario: Premier appel d'un nouvel utilisateur

- **WHEN** un utilisateur authentifié dont le `sub` est inconnu appelle l'API pour la première fois
- **THEN** un profil local est créé avec son `sub`, son email et son nom d'affichage

#### Scenario: Appels suivants

- **WHEN** un utilisateur dont le profil existe déjà appelle l'API
- **THEN** aucun nouveau profil n'est créé et l'email et le nom d'affichage sont mis à jour s'ils ont changé

### Requirement: Endpoint whoami

Le système SHALL exposer un endpoint `whoami` retournant uniquement le profil de l'utilisateur courant (identifiant, email, nom d'affichage). La PWA SHALL obtenir l'identité de l'utilisateur uniquement par cet endpoint, sans lire le contenu du jeton ni appeler le `userinfo` de Keycloak.

#### Scenario: Consultation du profil

- **WHEN** un utilisateur authentifié appelle `whoami`
- **THEN** la réponse contient son identifiant, son email et son nom d'affichage, et aucune donnée d'un autre domaine

### Requirement: Connexion depuis la PWA

La PWA SHALL authentifier l'utilisateur via le flux OIDC Authorization Code avec PKCE auprès de Keycloak et SHALL joindre le jeton d'accès à chaque appel de l'API.

#### Scenario: Utilisateur non connecté

- **WHEN** un utilisateur non connecté ouvre la PWA
- **THEN** il est redirigé vers la page de connexion Keycloak, puis ramené dans la PWA une fois authentifié

#### Scenario: Déconnexion

- **WHEN** un utilisateur connecté choisit de se déconnecter
- **THEN** sa session Keycloak est terminée et la PWA revient à l'état non connecté
