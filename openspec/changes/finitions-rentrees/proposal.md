## Pourquoi

Retours de Dorian après la livraison de la v0.6.0 (lot 4a) :

- l'option « Suivre les rentrées » est peu visible (cachée dans l'écran Foyer) et son nom n'est pas clair ;
- l'onglet « Charges » contient maintenant aussi des salaires et des aides : son nom ne colle plus ;
- dans le Budget, la liste des rentrées et celle des dépenses, l'une sous l'autre, chargent l'écran.

## Ce qui change

- **Nom de l'option** : « Rentrées et bilan », expliquée en une phrase : « Notez aussi l'argent qui rentre (salaires, aides…) pour voir chaque mois si vous êtes dans le vert et ce que vous mettez de côté. » Le texte est le même dans Foyer et dans la carte de mise en avant.
- **Mise en avant discrète** quand l'option est désactivée :
  - **Budget** : une petite carte sous le bandeau du mois, avec un bouton **Activer** pour le propriétaire et, pour les autres membres, « Demandez au propriétaire du foyer » ;
  - **Récurrents** : une seule ligne en bas de l'écran, avec le même bouton ;
  - une croix la ferme pour de bon sur l'appareil, et elle disparaît dès que l'option est active.
- **Onglet « Charges » renommé « Récurrents »**, pour tout le monde et quelle que soit l'option, car un nom de menu qui change selon un réglage serait perturbant. Avec l'option active, les panneaux s'appellent « Dépenses » et « Rentrées ». L'adresse `#/charges` reste valable.
- **Une seule liste dans le Budget** : « Opérations du mois », triée par date, avec des filtres **Tout · Dépenses · Rentrées**. Les rentrées se reconnaissent à une pastille verte avec une flèche entrante et à un montant vert « + ». Sans l'option, la liste reste « Dépenses du mois », sans filtres.

## Impact

- PWA seulement : aucun changement d'API ni de base. Version v0.6.1.
- Specs touchées : `interface-pwa` (nom de l'écran), `foyer` (présentation de l'option), `budget-mensuel` (liste unique, mise en avant), `charges-recurrentes` (mise en avant).
