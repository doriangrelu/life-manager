## Pourquoi

Depuis la v0.11.0, « C'est fait » sur un rappel saisi le fait disparaître (ponctuel) ou le fait passer à sa date suivante (récurrent) : il ne reste aucune trace. Dorian veut un **historique des rappels effectués**, avec la possibilité d'**en retirer une ligne**. Il veut aussi voir la **version déployée** dans un pied de page.

Première moitié du **lot 6c** (v0.12.0) ; la seconde est `donnees-personnelles` (v0.13.0). Découpage validé par Dorian le 2026-11-19.

## Ce qui change (validé par Dorian le 2026-11-19)

1. **Chaque « C'est fait »** sur un rappel saisi ajoute une ligne à l'historique : titre du rappel, date prévue, date à laquelle c'est fait, **qui** l'a fait.
2. Dans l'écran **Rappels** (espace Maison), une section **« Historique »** sous la liste, du plus récent au plus ancien, avec les 20 dernières lignes puis « Voir plus ».
3. Chaque ligne a un bouton **« Retirer »**, avec confirmation, ouvert à **tout membre** du foyer.
4. Supprimer un rappel **ne vide pas** son historique (le titre est recopié dans la ligne).
5. Les lignes de **plus d'un an** s'effacent toutes seules.
6. L'historique est effacé avec le foyer ; un membre parti devient « Ancien membre ».
7. **Pied de page** discret avec la version de la PWA et celle de l'API (les deux si elles diffèrent, juste après une livraison).

Décisions : seulement les rappels saisis (les rappels automatiques ont déjà leur trace dans le carnet et les opérations du mois) ; annuler un « C'est fait » : plus tard.

## Impact

- Backend, domaine `reminder` : table `reminder_done` (migration `reminder/V4`), écrite par « C'est fait » ; routes `GET /reminder-history` et `DELETE /reminder-history/{id}` ; purge des lignes de plus d'un an ; effacement avec le foyer. Route publique `GET /api/version`.
- PWA : section « Historique » de l'écran Rappels ; pied de page.
- Livraison : version passée à la construction de la PWA et au déploiement de l'API.
- Version **v0.12.0**.
