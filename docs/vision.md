# Life Manager — Vision

## Problème
La gestion du quotidien du foyer est dispersée : budget, crédits, factures, entretien des voitures, documents importants, rappels, courses. Rien n'est centralisé ni partagé simplement au sein du couple.

## Utilisateurs
- Un foyer de 2 personnes (moi + ma femme), données partagées.
- Pas de multi-tenant public visé à court terme (mais open source : d'autres pourront l'auto-héberger).

## Objectifs
1. Saisir une dépense en quelques secondes, depuis le mobile.
2. Suivre l'entretien des véhicules (carnet, CT, vidanges…) avec rappels automatiques.
3. Partager une liste de courses en temps réel entre nous deux.
4. Servir de terrain d'apprentissage (Java backend, architecture, déploiement).

## Périmètre

### MVP
| Domaine | Contenu minimal |
|---|---|
| Budget | Saisie rapide d'une dépense (montant, catégorie, qui), vue mensuelle par catégorie |
| Véhicules | Fiche véhicule, carnet d'entretien, échéances (CT, révision) + rappel |
| Courses | Liste partagée, ajout/cochage rapide, synchro entre nous deux |
| Socle | Comptes utilisateurs, foyer partagé, rappels (notifications) |

### Ensuite (V2+)
- Crédits et budgets prévisionnels (échéanciers, reste à vivre)
- Factures (récurrentes, échéances, pièces jointes)
- Documents et démarches importantes (coffre-fort, dates clés)
- Rappels génériques

### Hors périmètre
- Agrégation bancaire automatique (API bancaires) — à reconsidérer plus tard, import CSV éventuellement
- Usage commercial / SaaS

## Contraintes
- **Plateformes** : mobile ET desktop, indispensable.
- **Backend** : Java avec framework, compilé en natif (GraalVM native image) pour limiter la consommation de ressources sur le VPS.
- **Front** : ouvert. Piste privilégiée : PWA unique (installable mobile + desktop, une seule base de code). Alternative : app mobile native + web.
- **Hébergement** : VPS OVH, auto-hébergé.
- **Open source** sur GitHub.
- **Données sensibles** (finances, documents) : sécurité et sauvegardes à traiter dès le départ.

## Questions ouvertes (pour le cadrage d'architecture)
- Framework Java compatible native image : Quarkus, Spring Boot (AOT), Micronaut… ?
- PWA ou mobile natif : besoin de notifications push fiables sur iOS ? Mode hors-ligne (liste de courses en magasin) ?
- Authentification : comptes locaux simples, ou fournisseur (Keycloak…) ?
- Base de données et stratégie de sauvegarde.
