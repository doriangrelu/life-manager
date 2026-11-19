## Pourquoi

Trois petites gênes relevées par Dorian le 2026-11-19 (backlog « Petites corrections à traiter » de la feuille de route) :

- dans le carnet d'entretien, le type « Autre » ne permet pas de dire de quoi il s'agit, alors que la catégorie « Autre » des dépenses peut être précisée ;
- les catégories de rentrées (Salaire, Prime…) ne se gèrent nulle part : impossible de les renommer ou d'en archiver une ;
- la console Keycloak « Mon compte » : le logo mène à la console du realm `master` au lieu de revenir à l'application, et le survol des entrées du menu latéral reste gris.

## Ce qui change

- **Carnet d'entretien** : quand on choisit « Autre », un champ **« Préciser (facultatif) »** apparaît (40 caractères au plus, exemple : « Essuie-glaces »). La précision s'affiche dans le carnet à la place de « Autre ». Les opérations déjà enregistrées restent « Autre ». Une précision envoyée avec un autre type est refusée (`400`).
- **Catégories de rentrées** : la fenêtre « Catégories » du Budget gagne, quand l'option « Rentrées et bilan » est active, deux pastilles **Dépenses · Rentrées**. Côté Rentrées, mêmes actions que pour les dépenses : ajouter, renommer, archiver, réactiver. Sans l'option, la fenêtre ne change pas. L'API le permet déjà (`/income-categories`).
- **Console « Mon compte »** :
  - le logo ramène à la PWA (https://maison.dorian-grelu.fr ; en dev, http://localhost:5173 grâce à une variable `LM_PWA_URL` du compose) ;
  - le survol d'une entrée du menu prend la teinte indigo légère du menu de la PWA, au lieu du gris.

## Impact

- Backend, domaine `vehicle` : nouvelle colonne facultative `other_label` sur l'opération d'entretien (migration `vehicle/V4`), champ `otherLabel` dans la requête et la réponse. Aucun autre domaine touché.
- PWA : formulaire et carte du carnet, `CategoryManager` (prop `kind`), `BudgetView`.
- Thème Keycloak `infra/keycloak/themes/life-manager/account` + `docker-compose.yml` (dev). En prod, les fichiers du thème sont recopiés dans `~/keycloak/themes` et Keycloak doit être **redémarré : environ 1 minute de coupure de connexion pour tous les sites** du VPS. À faire avec l'accord de Dorian, au moment qu'il choisit.
- Version **v0.6.2**.
- Specs touchées : `carnet-entretien` (précision du type « Autre »), `rentrees` (gestion des catégories de rentrées dans la PWA).
