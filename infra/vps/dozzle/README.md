# Interface web Docker : Dozzle + oauth2-proxy

Adresse : **https://docker.dorian-grelu.fr**. Installé dans `~/dozzle` sur le VPS (sources ici).

- **Dozzle** (≈ 10 Mo de mémoire) : état des conteneurs, journaux en direct, mémoire et processeur, boutons démarrer / arrêter / relancer. Pas de terminal dans les conteneurs.
- **oauth2-proxy** : personne n'atteint Dozzle sans s'être connecté à Keycloak (realm `infra`, code à usage unique) avec une adresse listée dans `~/dozzle/emails-autorises.txt`.
- Chemin : Internet → Caddy (`~/infra/conf.d/docker.caddy`) → `dozzle-oauth2-proxy` → réseau privé → `dozzle`. Dozzle n'est ni sur le réseau `socle`, ni sur Internet.
- ⚠ Dozzle lit le socket Docker : quiconque entre a les pleins pouvoirs sur le serveur.

## Mise en service (une fois)

### 1. DNS — Dorian

Chez le gestionnaire du domaine `dorian-grelu.fr` (là où est `maison`) : enregistrement **A**, nom `docker`, valeur `152.228.237.190`.

> Mise en service du 2026-11-19 : le realm a été **importé** depuis un JSON généré par Claude (secret aléatoire écrit en même temps dans `~/dozzle/.env`, adresse dans `emails-autorises.txt`) ; l'étape 3 n'a donc pas servi. Dorian a ensuite activé *Always use lightweight access token* et durci le realm. Les étapes manuelles ci-dessous restent valables pour une réinstallation.

### 2. Keycloak — Dorian (console https://iabilis.fr/admin, compte administrateur)

Aucun redémarrage de Keycloak n'est nécessaire.

1. **Créer le realm** : menu des realms en haut à gauche → *Create realm* → nom `infra` → *Create*.
2. **Créer le client** (dans le realm `infra`) : *Clients* → *Create client*
   - *Client type* : OpenID Connect ; *Client ID* : `oauth2-proxy` → *Next* ;
   - *Client authentication* : **On** ; *Standard flow* : coché ; *Direct access grants* : décoché → *Next* ;
   - *Valid redirect URIs* : `https://docker.dorian-grelu.fr/oauth2/callback` ; *Web origins* : `https://docker.dorian-grelu.fr` → *Save*.
3. **Audience** (oauth2-proxy vérifie que le jeton lui est destiné) : client `oauth2-proxy` → onglet *Client scopes* → `oauth2-proxy-dedicated` → *Configure a new mapper* → *Audience*
   - *Name* : `audience oauth2-proxy` ; *Included Client Audience* : `oauth2-proxy` ; *Add to ID token* : On ; *Add to access token* : On → *Save*.
4. **Copier le secret** : client `oauth2-proxy` → onglet *Credentials* → *Client Secret* (bouton copier). Ne l'envoie à personne : il sert à l'étape 3.
5. **Créer ton compte** : *Users* → *Add user*
   - *Required user actions* : **Configure OTP** ; *Email verified* : **On** (sinon oauth2-proxy refuse) ;
   - *Username*, *Email*, prénom, nom → *Create* ;
   - onglet *Credentials* → *Set password* → mot de passe solide, *Temporary* : Off.
   À la première connexion, Keycloak te fera scanner un QR code avec une application d'authentification (Google Authenticator, Microsoft Authenticator, Aegis…) ; le code sera ensuite demandé à chaque connexion.

### 3. Adresse et secret sur le serveur — Dorian

```bash
ssh -t ubuntu@152.228.237.190 '~/dozzle/configurer-acces.sh'
```
Il demande l'adresse e-mail du compte créé à l'étape 2 et le secret du client (saisie masquée).

### 4. Démarrage — Claude

```bash
cd ~/dozzle && docker compose up -d && docker logs dozzle-oauth2-proxy
cp ~/dozzle/docker.caddy.a-installer ~/infra/conf.d/docker.caddy
docker exec infra-caddy-1 caddy reload --config /etc/caddy/Caddyfile   # sans coupure des autres sites
```
Vérifier : `https://docker.dorian-grelu.fr` redirige vers Keycloak ; un appel direct sans connexion ne montre rien de Dozzle.

## Exploitation

- Ajouter une personne : son adresse dans `emails-autorises.txt` + son compte dans le realm `infra`, puis `docker compose restart oauth2-proxy`.
- Couper l'interface : `cd ~/dozzle && docker compose stop` (et retirer `~/infra/conf.d/docker.caddy` + recharger Caddy).
- Mise à jour : changer les versions fixées dans `docker-compose.yml` (lire leurs notes de version), puis `docker compose up -d`.
- Sauvegarde : `~/dozzle` est dans l'archive de la nuit (avec `.env`). Rien d'autre à sauvegarder : Dozzle ne stocke rien.
