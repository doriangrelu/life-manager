## Pourquoi

Depuis la v0.11.0, « C'est fait » sur un rappel saisi le fait disparaître (ponctuel) ou le fait passer à sa date suivante (récurrent) : il ne reste aucune trace. Dorian veut un **historique des rappels effectués**, avec la possibilité d'**en retirer une ligne**.

Fait partie du **lot 6c** (avec `donnees-personnelles`), avant le lot 7.

## Ce qui change (proposition, à valider par Dorian)

1. **Chaque « C'est fait »** sur un rappel saisi ajoute une ligne à l'historique : titre du rappel, date prévue, date à laquelle c'est fait, **qui** l'a fait.
2. Dans l'écran **Rappels** (espace Maison), une section **« Historique »** sous la liste, du plus récent au plus ancien, repliée par défaut, avec les 20 dernières lignes puis « Voir plus ».
3. Chaque ligne a un bouton **« Retirer »** (petite croix), avec confirmation.
4. Supprimer un rappel **ne vide pas** son historique (le titre est recopié dans la ligne).
5. L'historique est effacé avec le foyer ; un membre parti devient « Ancien membre ».

## Impact

- Backend, domaine `reminder` : petite table `reminder_done` (migration `reminder/V4`), écrite par le use case « C'est fait » ; routes `GET /reminder-history` et `DELETE /reminder-history/{id}` ; effacement avec le foyer ; inclus dans l'export de données (change `donnees-personnelles`).
- PWA : section « Historique » de l'écran Rappels.

## Questions pour Dorian

1. **Périmètre** : seulement les rappels **saisis** (ceux qui ont « C'est fait ») ? Les rappels automatiques se règlent ailleurs (entretien noté dans le carnet, récurrent payé) et ont déjà leur propre historique (carnet, opérations du mois).
2. **Qui peut retirer une ligne** : n'importe quel membre du foyer (comme pour le reste, ma recommandation), ou seulement celui qui l'a faite ?
3. **Durée** : on garde tout, ou on efface seul au-delà d'un an (par exemple) ?
4. **Annuler un « C'est fait »** par erreur (le rappel revient à sa date d'avant) : utile tout de suite, ou plus tard ?
