## 1. Backend — domaine project

- [x] 1.1 [backend/project] Agrégats `Project` et `ProjectTask`, value objects (nom, couleur, état, dates) et tests domaine
- [x] 1.2 [backend/project] Schéma `project` (migration V1), dépôts, copie des membres, effacement avec le foyer et départ d'un membre
- [x] 1.3 [backend/project] Use cases et routes projets et tâches (contrôle d'accès au foyer) + tests d'intégration
- [x] 1.4 [backend/project] Événements de contrat `ProjectChangedV1`, `ProjectDeletedV1`, `TaskDeadlineChangedV1` relayés par la boîte d'envoi

## 2. Backend — budget et reminder

- [x] 2.1 [backend/budget] `expense.project_id`, copie `project_ref` tenue par événements, contrôle du rattachement
- [x] 2.2 [backend/budget] Dépenses à budget à part écartées des totaux du mois et du bilan (vue `counted_expense`) ; `projectId` / `offBudget` dans les opérations ; `GET /projects/{p}/expenses` + tests
- [x] 2.3 [backend/reminder] Type d'échéance `TASK`, tâches datées dans `GET /reminders` + tests

## 3. PWA

- [x] 3.1 [frontend] Section « Projets » dans l'espace Maison : liste, création, modification, suppression
- [x] 3.2 [frontend] Fiche d'un projet : tâches (cocher, ordonner, responsable, date), dépensé / prévu, dépenses
- [x] 3.3 [frontend] Champ « Projet » dans la saisie d'une dépense ; mention « Budget à part » dans les opérations ; tâches dans les rappels
- [x] 3.4 [frontend] Vérification au navigateur (mobile et bureau, clair et sombre)

## 4. Livraison

- [x] 4.1 [infra/ci] Parcours natif étendu (projet, tâche, dépense rattachée, budget à part)
- [x] 4.2 [docs] Documentation et feuille de route
- [x] 4.3 [infra/vps] Schéma `project` créé sur le VPS, v0.14.0 publiée et déployée
