## Context

Keycloak 26.4 en développement, 26.8 en production (version `latest` mise à jour chaque nuit sur le VPS). Le thème de connexion par défaut est `keycloak.v2` (PatternFly 5). En production, `~/keycloak/themes` est monté en lecture seule dans `/opt/keycloak/themes` et est vide. La PWA définit son identité dans `frontend/src/app.css` (variables de couleur, rayons, police Plus Jakarta Sans) et `frontend/public/logo.svg`.

## Goals / Non-Goals

**Goals :** même identité que la PWA, en clair et en sombre ; maintenance minimale malgré les mises à jour automatiques de Keycloak.

**Non-Goals :** réécrire les gabarits FreeMarker de Keycloak.

## Decisions

1. **Hériter de `keycloak.v2` et ne surcharger que du CSS** (variables PatternFly + quelques sélecteurs), plus le logo et les messages. Alternative : gabarits `.ftl` personnalisés — plus de contrôle, mais à reprendre à chaque évolution de Keycloak, risqué avec la mise à jour automatique. Revenir en arrière : remettre `loginTheme` vide.
2. **Couleurs recopiées depuis `app.css`** (pas de partage de fichier entre PWA et thème) : deux livrables indépendants ; quelques valeurs à tenir alignées, notées en commentaire.
3. **Police embarquée dans le thème** (fichier woff2 latin de Plus Jakarta Sans, licence OFL) : aucune requête vers un service externe.
4. **Français par défaut** via le realm (`defaultLocale=fr`, déjà prévu) et un fichier `messages_fr.properties` pour le titre.
5. **Pas de domaine métier touché** : uniquement de la configuration Keycloak.

## Risks / Trade-offs

- **Keycloak en `latest`** : une nouvelle version peut changer le DOM de `keycloak.v2` et dégrader l'apparence (jamais le fonctionnement, puisque seuls des styles sont surchargés).
- **Redémarrage de Keycloak en production** pour charger un nouveau thème : coupure courte de la connexion pour toutes les applications du serveur ; à planifier.

## Migration Plan

1. Copier `infra/keycloak/themes/life-manager` dans `~/keycloak/themes/` du VPS. 2. Redémarrer Keycloak (`docker compose -f ~/keycloak/docker-compose.yml restart`). 3. Realm `life-manager` → *Realm settings → Themes → Login theme* : `life-manager`. Retour arrière : remettre le thème par défaut dans le realm.
