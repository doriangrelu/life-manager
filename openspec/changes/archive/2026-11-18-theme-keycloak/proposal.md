## Why

La page de connexion est celle de Keycloak par défaut : en anglais par défaut, au nom technique « life-manager », sans rapport visuel avec l'application. Pour le foyer, c'est la première page vue ; elle doit ressembler à Life Manager et rassurer.

## What Changes

- **Thème de connexion `life-manager`** (Keycloak, type `login`) aux couleurs de la PWA : fond, carte blanche arrondie, bouton principal indigo, police Plus Jakarta Sans, logo de l'application, mode sombre suivant le système.
- Textes en **français** par défaut (le titre devient « Life Manager »).
- Le thème est **versionné dans le dépôt** (`infra/keycloak/themes/life-manager`), utilisé en développement et en production.
- Realm de dev et de production : `loginTheme = life-manager`.
- Déploiement en production : copie du thème dans `~/keycloak/themes` du VPS puis **redémarrage de Keycloak** (≈ 1 minute de coupure de connexion pour toutes les applications du serveur), à faire au moment choisi par Dorian.

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `comptes-utilisateurs` : la page de connexion porte l'identité de l'application.

## Impact

- Nouveaux fichiers : `infra/keycloak/themes/life-manager/login/` (gabarit de propriétés, CSS, logo, police, messages).
- `docker-compose.yml` (dev) : montage du dossier des thèmes.
- Realms dev et prod : thème de connexion.
- Aucun changement du backend ni de la PWA.

## Hors périmètre

- Thèmes `account` (gestion du compte), `admin` et `email` : la gestion du compte n'est pas proposée aux membres, et aucun courriel n'est envoyé.
- Modification des parcours de connexion (inscription, mot de passe oublié restent fermés).
- Thème des autres realms du serveur.
