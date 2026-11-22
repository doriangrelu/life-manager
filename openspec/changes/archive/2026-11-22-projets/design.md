## Contexte

Lot 8, validé par Dorian le 2026-11-22 (voir le proposal). Trois domaines sont touchés : un nouveau domaine `project`, puis `budget` et `reminder`, qui n'en voient que le package `contract`, comme pour `bills` aujourd'hui.

## Décisions

### Domaine `project`

- Agrégat `Project` : `ProjectId`, `HouseholdId`, `ProjectName` (1 à 60 caractères), `description` (500 caractères au plus, facultative), `ProjectColor` (liste fixe de 8 couleurs : `INDIGO`, `TEAL`, `GREEN`, `AMBER`, `ORANGE`, `RED`, `PINK`, `SLATE` ; la PWA les traduit en jetons clair/sombre), `plannedCents` (facultatif, ≥ 0), `startOn` / `targetOn` (facultatifs, fin ≥ début), `ProjectStatus` (`ACTIVE`, `DONE`, `ABANDONED`), `countsInBudget` (booléen, vrai par défaut), `createdAt`.
- Agrégat séparé `ProjectTask` (petit agrégat, liste sans limite pratique) : `TaskId`, `ProjectId`, `HouseholdId`, titre (1 à 120 caractères), `assignee` (membre du foyer, facultatif), `dueOn` (facultatif), `doneAt` (facultatif), `position`.
- Le domaine garde sa copie des membres (`household_member`, alimentée par `MembershipKnownV1`, comme les autres) pour vérifier le responsable d'une tâche ; un membre parti → ses tâches n'ont plus de responsable.
- Supprimer un projet supprime ses tâches. Pas de suppression automatique des dépenses.
- Routes : `/api/households/{h}/projects` (liste, ajout), `/{p}` (lecture, modification, suppression), `/{p}/tasks` (ajout), `/{p}/tasks/{t}` (modification, suppression), `/{p}/tasks/{t}/done` (POST fait / DELETE pas fait), `/{p}/tasks/order` (PUT).
- Événements de contrat (boîte d'envoi) :
  - `ProjectChangedV1(householdId, projectId, name, color, status, countsInBudget, plannedCents, occurredAt)` à chaque ajout ou modification ;
  - `ProjectDeletedV1(householdId, projectId, occurredAt)` ;
  - `TaskDeadlineChangedV1(taskId, householdId, projectName, title, dueOn, occurredAt)` à chaque changement d'une tâche ou de son projet ; `dueOn` vaut `null` quand il n'y a plus rien à rappeler (tâche faite, sans date, supprimée, projet terminé, abandonné ou supprimé). Un seul événement, sur le modèle de `ChargeDeadlinesChangedV1`.

### `budget`

- Colonne `expense.project_id` (UUID, facultative, sans clé étrangère).
- Table `project_ref(project_id, household_id, name, color, status, counts_in_budget)` tenue par `ProjectChangedV1` / `ProjectDeletedV1` (idempotent).
- Rattacher une dépense : le projet doit exister dans la copie, être du foyer et `ACTIVE` (sinon 400). Une dépense déjà rattachée à un projet terminé garde son rattachement si on la corrige sans toucher au projet.
- `ProjectDeletedV1` : les dépenses perdent leur projet (elles redeviennent normales, donc comptées) et la ligne de copie est supprimée.
- Hors budget = dépense dont le projet a `counts_in_budget = false`. Ces dépenses sont écartées des totaux du mois, des catégories, du budget prévu, du reste à vivre, des barres et du `GET /report` ; elles restent dans la liste des opérations du mois avec `projectId` et `offBudget: true`.
- Nouvelle route `GET /api/households/{h}/projects/{p}/expenses` (dans `budget`) : dépenses du projet (toutes dates) et total, pour la fiche du projet. Projet inconnu dans la copie → 404.
- Effacement du foyer / départ d'un membre : `project_ref` effacée avec le foyer.

### `reminder`

- Réutilise la table `deadline` existante (source = la tâche) avec un nouveau type d'échéance `TASK` (rappel 7 jours avant), tenue par `TaskDeadlineChangedV1`. Sujet affiché : « titre · projet », raccourci à 60 caractères.
- `GET /reminders` montre donc les tâches dont la date est passée ou tombe dans les 7 jours, en kind `TASK`. Pas de « c'est fait » depuis le bandeau ni de lien : on coche la tâche dans la fiche du projet.

### PWA

- Section `projects` (#/projets, et #/projets/{id} pour la fiche) dans l'espace Maison.
- Liste : projets en cours en haut, puis repli « Terminés et abandonnés » ; pastille couleur, avancement des tâches (3 / 7, avec une barre), budget prévu. Le dépensé n'est que sur la fiche (une seule lecture par projet ouvert).
- Vocabulaire : l'interface dit **« Budget à part »** plutôt que « hors budget », expression déjà employée par le Budget pour les catégories sans budget prévu.
- Fiche : en-tête (nom, couleur, état, dates, option hors budget), barre dépensé/prévu, tâches (cocher, glisser pour ordonner, responsable, date), dépenses du projet.
- Formulaire de dépense : champ « Projet » facultatif (projets en cours), affiché seulement si le foyer a au moins un projet en cours.
- Opérations du mois : pastille du projet ; mention « Budget à part » et montant grisé et barré si le projet a un budget à part.
- Rappels : tâche datée affichée « Tâche — titre · projet ».

## Risques

- Copies locales décalées le temps de la boîte d'envoi (quelques secondes) : un projet tout juste créé peut être refusé à la saisie d'une dépense pendant ce délai. La PWA recharge la liste des projets du budget avant d'ouvrir le formulaire ; acceptable.
- Basculer un projet en « hors budget » change les totaux des mois passés : voulu (le choix vaut pour tout le projet), à signaler par une phrase dans l'option.
