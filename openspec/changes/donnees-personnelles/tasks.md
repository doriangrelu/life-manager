## 1. CGU

- [ ] 1.1 [backend/identity] `TermsAcceptance`, migration `identity/V3`, `whoami` (+ `termsAccepted`, `termsVersion`), `PUT /api/me/terms` (+ tests)
- [ ] 1.2 [frontend] Pages publiques CGU et confidentialité, écran d'acceptation, liens du pied de page

## 2. Export

- [ ] 2.1 [backend] Tables `authorship` (budget, bills, vehicle, shopping, reminder) remplies à la création
- [ ] 2.2 [backend] `PersonalDataSource` + `SqlRows` (kernel) ; une source par domaine ; `GET /api/me/export`, limite 1/min (+ tests : contenu, rien des autres)
- [ ] 2.3 [frontend] « Télécharger mes données » (écran Foyer, section Mon compte)

## 3. Suppression du compte

- [ ] 3.1 [backend/identity] `DELETE /api/me`, `UserDeletedV1`, suppression Keycloak (+ faux Keycloak de test)
- [ ] 3.2 [backend/household] Réaction à `UserDeletedV1` (foyer supprimé, transmis ou quitté ; fiche effacée) (+ tests bout en bout)
- [ ] 3.3 [frontend] Parcours de suppression (transferts proposés, e-mail retapé, déconnexion)
- [ ] 3.4 [infra/keycloak] Compte de service du client `life-manager-api` en dev ; procédure prod dans la doc

## 4. Livraison

- [ ] 4.1 [frontend] Vérification au navigateur (mobile, bureau, clair, sombre)
- [ ] 4.2 [infra] Parcours natif étendu (CGU, export, suppression)
- [ ] 4.3 [infra/vps] Réglage Keycloak de prod par Dorian, puis v0.13.0 publiée et déployée
