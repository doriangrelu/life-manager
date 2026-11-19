## Historique

- Nouvel agrégat `ReminderDone` dans `reminder` (petit, immuable) : id, foyer, titre recopié, date prévue, date faite (`doneOn`), `doneBy` (membre). Pas de lien obligatoire vers le rappel : il peut être supprimé ensuite.
- `CompletePersonalReminderUseCase` enregistre la ligne dans la même transaction que « C'est fait » (le membre courant vient du contexte, comme ailleurs).
- Table `reminder_done` (migration `reminder/V4`) : `id`, `household_id`, `title`, `due_on`, `done_on`, `done_by`, index `(household_id, done_on desc)`.
- `GET /api/households/{h}/reminder-history?limit=20&before=<date>` : lignes du plus récent au plus ancien ; « Voir plus » repasse la date de la dernière ligne reçue. `DELETE /api/households/{h}/reminder-history/{id}` → 204 ; inconnu ou autre foyer → 404. Contrôle d'accès par `ReminderAccess`.
- Purge : tâche planifiée quotidienne (comme `ChargeScheduler`) qui supprime les lignes dont `done_on` a plus d'un an. Use case interne exempté du contrôle de foyer dans `ArchitectureTest`.
- Effacement : `HouseholdData` du domaine efface aussi `reminder_done` (foyer supprimé). Membre parti : rien à faire, la PWA affiche « Ancien membre » pour un id inconnu.

## Version

- PWA : `VITE_APP_VERSION` passé en `build-arg` par `release.yml` (nom du tag) ; en dev : `dev`.
- API : l'image n'est pas reconstruite au tag (elle est renommée depuis `sha-…`), donc la version vient du **déploiement** : `deploy.sh` exporte `LM_VERSION=vX.Y.Z` dans l'environnement du conteneur ; l'API la lit (`app.version`, défaut `dev`).
- `GET /api/version` public (comme la santé) : `{ "version": "v0.12.0" }`. Rien d'autre (ni commit, ni dépendances).
- Pied de page : `PWA v0.12.0` ; si l'API répond une autre version : `PWA v0.12.0 · API v0.11.0`. Sur mobile en bas de l'écran Foyer ; sur grand écran sous le menu latéral. Petit texte gris, sans gêner la barre du bas. Les liens CGU / confidentialité viendront avec la v0.13.0.
