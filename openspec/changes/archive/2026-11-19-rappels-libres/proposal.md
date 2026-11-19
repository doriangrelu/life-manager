## Pourquoi

Aujourd'hui, les rappels viennent tout seuls des véhicules (contrôle technique, révision) et des dépenses récurrentes. Rien ne permet de noter soi-même « déclarer les impôts », « ramoner la cheminée », « renouveler la mutuelle ».

C'est le **lot 6a « Rappels libres »** (v0.11.0). Les e-mails (Brevo, e-mails de Keycloak aux couleurs de l'application, réparation du HTTPS sortant du natif) sont **planifiés plus tard** à la demande de Dorian : lot **6b**.

## Ce qui change (proposition, à valider par Dorian)

1. **Un écran « Rappels »** dans l'espace **Maison**, à côté de Véhicules (comme prévu par la navigation par espaces).
   - En haut, les rappels à venir et en retard, tous confondus : ceux qu'on a saisis et ceux qui viennent des véhicules et des récurrents.
   - Dessous, la liste des rappels saisis, pour les modifier ou les supprimer.
2. **Saisir un rappel** : un titre (« Ramonage »), une date, une note facultative, une **récurrence** (aucune, chaque mois, chaque trimestre, chaque année) et un **délai** pour être prévenu (le jour même, 1 semaine avant, 1 mois avant).
3. **Le rappel apparaît** dans le bandeau des rappels et dans les notifications de l'appareil, comme les autres, à partir du délai choisi.
4. **« C'est fait »** : un rappel ponctuel disparaît ; un rappel récurrent passe tout seul à sa date suivante.

## Impact

- Backend, domaine `reminder` : nouvel agrégat « rappel libre » (migration `reminder/V3`), ajout, modification, suppression, « c'est fait » ; les rappels libres rejoignent la liste des rappels existante. Effacés avec le foyer.
- PWA : écran Rappels dans l'espace Maison, formulaire avec contrôles de saisie et icônes « i ».
- Version **v0.11.0**.

## Questions pour Dorian

1. **Récurrence** : aucune, chaque mois, chaque trimestre, chaque année, ça suffit ? (Pas de « tous les 2 ans » ni de jour de la semaine.)
2. **Délai pour être prévenu** : le jour même, 1 semaine avant ou 1 mois avant, au choix pour chaque rappel. Par défaut, 1 semaine. Ça te va ?
3. **Visibilité** : un rappel saisi est visible par **tout le foyer** (comme le reste), ou faut-il pouvoir le garder **pour soi** ?
4. **Écran** : un sous-onglet « Rappels » dans l'espace Maison, qui montre aussi les rappels automatiques à venir (véhicules, récurrents) ?
