## Context

Aujourd'hui, chaque domaine qui publie a un relais (`*ContractEventRelay`) : il écoute ses événements internes en `AFTER_COMMIT` et publie l'événement de contrat sur le bus Micronaut, en mémoire et de façon synchrone. Entre la validation et la publication, un arrêt brutal perd l'événement.

Producteurs et événements concernés :

| Domaine (schéma) | Événements de contrat | Destinataires |
|---|---|---|
| `identity` | `UserRegisteredV1`, `UserProfileChangedV1` | household |
| `household` | `HouseholdCreatedV1`, `MemberJoinedV1` | budget, vehicle, shopping, reminder, bills |
| `vehicle` | `VehicleDeadlinesChangedV1`, `VehicleRemovedV1` | reminder |
| `bills` | `ChargePaidV1`, `ChargeDeadlinesChangedV1` | budget, reminder |

`MembershipKnownV1` (rappel au démarrage) reste hors boîte d'envoi : c'est déjà un rejeu.

## Goals / Non-Goals

**Goals** : aucun événement perdu ; ordre conservé dans un domaine ; même réactivité qu'aujourd'hui (distribution dans la foulée de la requête) ; code commun dans `kernel`, presque rien par domaine.

**Non-Goals** : broker externe, plusieurs instances, exactement-une-fois (on garantit au moins une fois + destinataires idempotents).

## Decisions

- **Table `outbox` dans chaque schéma producteur** (`identity.outbox`, `household.outbox`, `vehicle.outbox`, `bills.outbox`) : `id` (bigserial, ordre), `type` (nom de la classe de l'événement), `payload` (JSON), `created_at`, `sent_at`, `attempts`, `last_error`. Aucune table partagée entre domaines.
- **Enregistrement** : le relais écoute ses événements internes en `BEFORE_COMMIT` et écrit l'événement de contrat dans sa boîte, sur la transaction en cours. Il inscrit aussi une synchronisation `afterCommit` qui déclenche le facteur : la distribution reste immédiate.
- **Facteur** (`kernel`) : un par domaine, verrou en mémoire (une seule distribution à la fois, donc dans l'ordre des `id`). Pour chaque événement non envoyé : publication sur le bus, puis `sent_at`, dans une transaction du domaine producteur. En cas d'erreur d'un destinataire : `attempts + 1`, `last_error`, arrêt du lot (l'ordre est gardé) ; au-delà de 10 échecs, l'événement est ignoré par le facteur et journalisé en erreur.
- **Relance** : tâche planifiée toutes les 5 s pour tous les facteurs, plus un passage au démarrage. Ménage quotidien des événements envoyés depuis plus de 7 jours.
- **Sérialisation** : JSON via Micronaut Serde (les événements de contrat sont `@Serdeable`) ; le type est retrouvé par les introspections Micronaut, sans réflexion (compatible natif).
- **Code commun** : `kernel.infrastructure.outbox` (entité, requêtes de base, facteur, planification). Chaque domaine déclare son dépôt `@Repository("<domaine>")` qui hérite des requêtes communes, et une fabrique qui assemble son facteur.
- **Idempotence des destinataires** : vérifiée pour tous (ajout de membre « si absent », catégories et listes par défaut « si aucune », dépense liée à l'échéance par clé, rappels remplacés en bloc, nom de membre remplacé).

## Risks / Trade-offs

- Un événement peut être distribué deux fois (plantage entre la distribution et `sent_at`) → destinataires idempotents, testés.
- Un destinataire en panne retarde les événements suivants du même domaine (jusqu'à 10 essais, ~1 min) → acceptable pour un foyer, et l'ordre est préservé.
- Le verrou en mémoire suppose une seule instance → c'est le cas ; une base partagée par plusieurs instances demanderait un verrou PostgreSQL.

## Migration Plan

Migration additive `outbox` dans les quatre schémas, appliquée au démarrage par Flyway. Pas de reprise de données. Retour arrière : version précédente, les tables restent inutilisées.
