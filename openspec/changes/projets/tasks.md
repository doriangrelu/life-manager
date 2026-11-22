## 1. Backend — domaine project

- [ ] 1.1 [backend/project] Agrégats `Project` et `ProjectTask`, value objects (nom, couleur, état, dates) et tests domaine
- [ ] 1.2 [backend/project] Schéma `project` (migration V1), dépôts, copie des membres, effacement avec le foyer et départ d'un membre
- [ ] 1.3 [backend/project] Use cases et routes projets et tâches (contrôle d'accès au foyer) + tests d'intégration
- [ ] 1.4 [backend/project] Événements de contrat `ProjectChangedV1`, `ProjectDeletedV1`, `TaskScheduledV1`, `TaskUnscheduledV1` relayés par la boîte d'envoi

## 2. Backend — budget et reminder

- [ ] 2.1 [backend/budget] `expense.project_id`, copie `project_ref` tenue par événements, contrôle du rattachement
- [ ] 2.2 [backend/budget] Dépenses hors budget écartées des totaux du mois et du bilan ; `projectId` / `offBudget` dans les opérations ; `GET /projects/{p}/expenses` + tests
- [ ] 2.3 [backend/reminder] Copie `task_deadline`, tâches datées dans `GET /reminders` (kind `TASK`) + tests

## 3. PWA

- [ ] 3.1 [frontend] Section « Projets » dans l'espace Maison : liste, création, modification, suppression
- [ ] 3.2 [frontend] Fiche d'un projet : tâches (cocher, ordonner, responsable, date), dépensé / prévu, dépenses
- [ ] 3.3 [frontend] Champ « Projet » dans la saisie d'une dépense ; mention « hors budget » dans les opérations ; tâches dans les rappels
- [ ] 3.4 [frontend] Vérification au navigateur (mobile et bureau, clair et sombre)

## 4. Livraison

- [ ] 4.1 [infra/ci] Parcours natif étendu (projet, tâche, dépense rattachée, hors budget)
- [ ] 4.2 [docs] Documentation et feuille de route
- [ ] 4.3 [infra/vps] Schéma `project` créé sur le VPS, v0.14.0 publiée et déployée
