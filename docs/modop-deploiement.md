# Mode opératoire : déployer une nouvelle version

Pour mettre en production une nouvelle version de Life Manager, sans aide.
Durée : environ 15 minutes, dont 10 d'attente de la construction des images.

> Première installation sur un serveur neuf, sauvegarde, restauration : voir [deploiement.md](deploiement.md).

## Vue d'ensemble

```
Ton PC                      GitHub                          VPS
──────                      ──────                          ───
1. git tag vX.Y.Z  ──push──▶ 2. construit les images  ──▶ 3. deploy.sh vX.Y.Z
                               (≈ 10 min)                    tire les images, redémarre,
                                                             vérifie, met à jour Caddy
```

Une **version** = un tag Git `vX.Y.Z`. Le même numéro désigne le code, les images Docker et la configuration de production. Revenir en arrière = redéployer le numéro précédent.

---

## Étape 0 — Vérifier que `main` est prêt

Sur ton PC, dans le dépôt :

```bash
git checkout main
git pull
git status
```

- `git status` doit afficher « nothing to commit, working tree clean ».
- Sur https://github.com/doriangrelu/life-manager/actions, le dernier **CI** de `main` doit être vert ✅.

Ne pas aller plus loin si la CI est rouge.

## Étape 1 — Choisir le numéro de version

Regarder la dernière version publiée :

```bash
git fetch --tags
git tag --sort=-v:refname | head -3
```

Choisir le suivant :

| Changement | Exemple | Nouveau numéro |
|---|---|---|
| Correctif | bug corrigé | `v0.1.1` → `v0.1.2` |
| Nouvelle fonctionnalité | nouvel écran | `v0.1.2` → `v0.2.0` |

**Noter aussi le numéro actuellement en production** (le plus récent) : c'est lui qui servira en cas de retour arrière.

## Étape 2 — Publier la version

Remplacer `v0.2.0` par le numéro choisi :

```bash
git tag -a v0.2.0 -m "Ce que contient la version, en une phrase"
git push origin v0.2.0
```

## Étape 3 — Attendre les images (≈ 10 minutes)

1. Ouvrir https://github.com/doriangrelu/life-manager/actions
2. Cliquer sur la ligne **« Publication des images »** portant ton numéro.
3. Attendre que les deux jobs (**life-manager-api** et **life-manager-web**) soient verts ✅.

Si un job est rouge ❌ : ne pas déployer. Ouvrir le job pour lire l'erreur, corriger sur `main`, puis publier un **nouveau** numéro (ne jamais réutiliser un tag déjà poussé).

## Étape 4 — Déployer sur le VPS

```bash
ssh ubuntu@152.228.237.190
cd ~/life-manager
infra/deploy/deploy.sh v0.2.0
```

Le script fait tout seul, dans l'ordre :

1. place le dépôt du serveur sur la version ;
2. télécharge les images ;
3. redémarre l'API et la PWA (quelques secondes d'indisponibilité de Life Manager uniquement) ;
4. attend que l'API réponde ;
5. met à jour le routage dans Caddy (sans couper les autres sites).

**Résultat attendu**, à la dernière ligne :

```
==> Life Manager v0.2.0 déployé. Retour arrière : infra/deploy/deploy.sh v0.1.2
```

Les migrations de base de données s'appliquent toutes seules au démarrage de l'API.

## Étape 5 — Vérifier

1. Ouvrir https://maison.dorian-grelu.fr et recharger la page (**Ctrl + Maj + R** sur PC ; sur mobile, fermer et rouvrir l'application).
2. Se connecter, ouvrir **Budget**, **Courses**, **Véhicules** : les données doivent s'afficher.
3. Tester rapidement ce que la version a changé.

Optionnel, sur le VPS :

```bash
docker ps --format '{{.Names}}  {{.Status}}' | grep life-manager
docker logs life-manager-api --since 10m 2>&1 | grep -E 'ERROR|Startup completed'
```

Les deux conteneurs doivent être « Up », et les journaux doivent contenir « Startup completed » sans « ERROR ».

C'est terminé. Se déconnecter du VPS : `exit`.

---

## En cas de problème : retour arrière

Si le script s'arrête sur une erreur, ou si l'application ne marche pas après le déploiement, **redéployer la version précédente** (celle notée à l'étape 1) :

```bash
cd ~/life-manager
infra/deploy/deploy.sh v0.1.2
```

Puis analyser tranquillement le problème (journaux ci-dessous) et le corriger dans une nouvelle version.

> ⚠️ Si la version contenait une migration qui **supprime** des données (rare, signalée dans la description du changement), lancer d'abord une sauvegarde à la main : `~/infra/backup.sh`. Un retour arrière ne recrée pas les données supprimées.

## Problèmes fréquents

| Message ou symptôme | Cause probable | Que faire |
|---|---|---|
| `le dépôt a des modifications locales` | un fichier a été modifié à la main sur le VPS | `cd ~/life-manager && git status` pour voir lequel ; `git checkout -- <fichier>` pour annuler |
| `manifest unknown` / `not found` au téléchargement | images pas encore publiées, ou numéro mal tapé | vérifier l'étape 3 et l'orthographe du tag |
| `le service ne répond pas` | l'API ne démarre pas | lire `docker logs life-manager-api --tail 50`, puis retour arrière |
| Page « Le fournisseur d'identité est indisponible » | l'API n'arrive pas à joindre Keycloak | `docker ps \| grep keycloak` (doit être « healthy ») ; `docker logs life-manager-api --tail 30` |
| Page « Failed to fetch » | Keycloak injoignable depuis le navigateur | ouvrir https://iabilis.fr/realms/life-manager : doit afficher du texte, pas une erreur |
| `ssh: connect to host … Connection timed out` | adresse IP bannie par fail2ban (trop de connexions ratées ou rapprochées) | attendre 10 min, ou depuis la console KVM d'OVH : `sudo fail2ban-client set sshd unbanip <ton IP>` (IP visible sur https://api.ipify.org) |
| Ancienne version encore affichée | cache de la PWA dans le navigateur | Ctrl + Maj + R ; sur mobile, fermer complètement l'application et la rouvrir |

## Aide-mémoire

```bash
# PC : publier
git checkout main && git pull
git tag -a vX.Y.Z -m "…" && git push origin vX.Y.Z
# → attendre le vert sur GitHub Actions (« Publication des images »)

# VPS : déployer
ssh ubuntu@152.228.237.190
cd ~/life-manager && infra/deploy/deploy.sh vX.Y.Z

# VPS : revenir en arrière
cd ~/life-manager && infra/deploy/deploy.sh <version précédente>

# VPS : diagnostiquer
docker ps | grep life-manager
docker logs life-manager-api --tail 50
```
