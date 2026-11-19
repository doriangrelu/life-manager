## 1. CGU

- [x] 1.1 [backend/identity] `TermsAcceptance`, migration `identity/V3`, `whoami` (+ `termsAccepted`, `termsVersion`), `PUT /api/me/terms` (+ tests)
- [x] 1.2 [frontend] Pages publiques CGU et confidentialité, écran d'acceptation, liens du pied de page

## 2. Export

- [x] 2.1 [backend] Tables `authorship` (budget, bills, vehicle, shopping, reminder) remplies à la création
- [x] 2.2 [backend] `PersonalDataSource` + `SqlRows` (kernel) ; une source par domaine ; `GET /api/me/export`, limite 1/min (+ tests : contenu, rien des autres)
- [x] 2.3 [frontend] « Télécharger mes données » (écran Foyer, section Mon compte)

## 3. Suppression du compte

- [x] 3.1 [backend/identity] `DELETE /api/me`, `UserDeletedV1`, suppression Keycloak (+ faux Keycloak de test)
- [x] 3.2 [backend/household] Réaction à `UserDeletedV1` (foyer supprimé, transmis ou quitté ; fiche effacée) (+ tests bout en bout)
- [x] 3.3 [frontend] Parcours de suppression (transferts proposés, e-mail retapé, déconnexion)
- [x] 3.4 [infra/keycloak] Compte de service du client `life-manager-api` en dev ; procédure prod dans la doc

## 4. Livraison

- [x] 4.1 [frontend] Vérification au navigateur (mobile, bureau, clair, sombre)
- [x] 4.2 [infra] Parcours natif étendu (CGU, export, suppression)
- [ ] 4.3 [infra/vps] Réglage Keycloak de prod par Dorian, puis v0.13.0 publiée et déployée

Note : les catégories créées à la volée depuis une dépense (nouveau nom saisi) n'ont pas d'auteur ; seules celles créées depuis l'écran Catégories sont exportées.
