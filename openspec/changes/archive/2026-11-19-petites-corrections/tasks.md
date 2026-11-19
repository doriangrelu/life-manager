## 1. Carnet d'entretien : précision du type « Autre »

- [x] 1.1 [backend/vehicle] Value object `MaintenanceOtherLabel` (1 à 40 caractères, réservé au type OTHER) dans `MaintenanceDetails` ; migration `vehicle/V4` (colonne `other_label` facultative) ; `otherLabel` dans la requête, la vue et la réponse (+ tests domaine et intégration)
- [x] 1.2 [frontend/vehicle] Champ « Préciser (facultatif) » sous « Autre » dans `MaintenanceForm` ; précision affichée à la place de « Autre » dans `MaintenanceEntryCard` (+ test du libellé)

## 2. Catégories de rentrées

- [x] 2.1 [frontend/budget] `CategoryManager` avec une prop `kind` ; fenêtre « Catégories » avec pastilles Dépenses / Rentrées quand l'option est active ; listes de catégories du Budget rechargées après modification

## 3. Console « Mon compte »

- [x] 3.1 [infra/keycloak] `logoUrl` du thème account (variable `LM_PWA_URL`, prod par défaut) ; `LM_PWA_URL` dans le compose de dev
- [x] 3.2 [infra/keycloak] Survol du menu latéral à la teinte indigo légère de la PWA (clair et sombre)

## 4. Vérification et livraison

- [x] 4.1 [frontend] Vérification au navigateur : mobile et bureau, clair et sombre (carnet, catégories, console « Mon compte » de dev)
- [x] 4.2 [infra/vps] Version v0.6.3 publiée et déployée (le tag v0.6.2, mal placé par le script de livraison, a été abandonné)
- [x] 4.3 [infra/vps] Thème recopié dans `~/keycloak/themes` et Keycloak redémarré, avec l'accord de Dorian (coupure d'environ 1 min pour tous les sites)
