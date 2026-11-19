# Métriques du VPS : Beszel + oauth2-proxy

Adresse : **https://metrics.dorian-grelu.fr**. Installé dans `~/beszel` sur le VPS (sources ici). Mis en service le 2026-11-19.

- **Beszel** (≈ 11 Mo) : tableau de bord avec historique (processeur, mémoire, swap, disque, réseau, et chaque conteneur).
- **beszel-agent** (≈ 3 Mo, réseau de l'hôte) : mesure le serveur et Docker ; parle au tableau de bord par le fichier `beszel_socket/beszel.sock`, sans port réseau. Il n'accepte que la clé du tableau de bord (`beszel_agent_data/hub.pub`).
- **oauth2-proxy** (`beszel-oauth2-proxy`) : connexion Keycloak (realm `infra`, client `oauth2-proxy` partagé avec Dozzle, adresses de `emails-autorises.txt`). Beszel accepte l'e-mail transmis **seulement** depuis l'adresse fixe d'oauth2-proxy (10.89.0.10, réseau privé sans Internet) ; connexion par mot de passe coupée.
- Secrets (`~/beszel/.env`, 600) : secret du client (copie de celui de `~/dozzle/.env`), secret de cookie, mot de passe aléatoire du premier utilisateur (jamais utilisé).

## Mise en service (une fois)

1. DNS : enregistrement A `metrics` → 152.228.237.190 (fait par Dorian).
2. Keycloak (Dorian) : realm `infra` → *Clients* → `oauth2-proxy` → *Valid redirect URIs* : ajouter `https://metrics.dorian-grelu.fr/oauth2/callback` ; *Web origins* : ajouter `https://metrics.dorian-grelu.fr` → *Save*.
3. Première connexion sur https://metrics.dorian-grelu.fr, puis **Add System** : nom `VPS`, *Host / IP* : `/beszel_socket/beszel.sock` (Beszel affiche une commande d'installation d'agent : l'ignorer, l'agent tourne déjà).

## Exploitation

- Le secret du client change (Keycloak) : le mettre à jour dans `~/dozzle/.env` **et** `~/beszel/.env`, puis `docker compose up -d` dans les deux dossiers.
- Mise à jour : changer les versions fixées (`henrygd/beszel`, `henrygd/beszel-agent`, même numéro) après lecture des notes de version, puis `docker compose up -d`.
- Sauvegarde : configuration et `.env` dans l'archive de la nuit ; l'historique des métriques (`beszel_data`) n'est **pas** sauvegardé (fichiers de root, perte sans gravité). Après une restauration : relancer, refaire l'étape 3.
- Couper : `cd ~/beszel && docker compose stop`, retirer `~/infra/conf.d/metrics.caddy`, recharger Caddy.
