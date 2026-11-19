## Décisions de Dorian (2026-11-19)

- Contact : doriangrelu@gmail.com ; responsable du traitement : Dorian Grelu ; particulier, pas d'adresse ni de téléphone personnels (seulement l'hébergeur OVH).
- CGU validées dans l'application à la 1re connexion et à chaque nouvelle version.
- Export : seulement ce que l'utilisateur a saisi, sans données techniques.
- Suppression : transfert proposé seulement s'il y a d'autres membres ; refus possible ; sans transfert choisi, le foyer passe au membre arrivé le plus tôt ; confirmation dans tous les cas.
- Compte Keycloak supprimé par l'API (API d'administration, compte de service).
- « Créé par » ajouté à partir de maintenant aux objets qui n'avaient pas d'auteur ; les récurrents dont il est le payeur sont aussi exportés.

## CGU

- Version des CGU en configuration (`life-manager.terms-version`, une date : `2026-11-19`). Changer les textes = changer la version : tout le monde revalide.
- `UserAccount` + value object `TermsAcceptance(version, acceptedAt)` (facultatif). Migration `identity/V3` : `terms_version`, `terms_accepted_at`.
- `whoami` renvoie en plus `termsAccepted` (version acceptée = version courante) et `termsVersion`. `PUT /api/me/terms {version}` → 204 ; une autre version que la courante → 400.
- PWA : pages publiques `#/cgu` et `#/confidentialite` (lisibles sans connexion) ; écran « J'accepte » tant que `termsAccepted` est faux ; liens dans le pied de page. Les liens depuis la page de connexion Keycloak demandent un redéploiement du thème (Keycloak redémarré, tous les sites coupés environ 45 s) : fait à part, avec l'accord de Dorian.

## « Créé par »

- Dans chaque domaine concerné, une table `authorship(object_id, household_id, member_id)` : qui a créé une catégorie, un budget prévu (dernier à l'avoir réglé), un récurrent, un véhicule, une liste de courses, un rappel. Les agrégats ne changent pas : l'auteur ne porte aucune règle métier.
- Port de sortie `Authorship.record(objectId, householdId, member)` appelé par les use cases de création ; effacé avec le foyer. Les objets créés avant la v0.13.0 n'ont pas d'auteur et ne sont pas exportés.

## Export

- Interface commune `kernel.application.port.out.PersonalDataSource` : `section()` + `exportFor(memberId)` → données simples (texte, nombres, booléens, dates en texte). Chaque domaine l'implémente par des requêtes SQL en lecture (outil commun `SqlRows` du kernel, sur la datasource du domaine) ; `identity` les assemble (`GET /api/me/export`, fichier JSON en pièce jointe).
- Contenu : profil ; foyers (nom, rôle, arrivée) ; ce que l'utilisateur a saisi dans chaque domaine (voir la spec), sans identifiants internes sauf le lien vers le foyer, sans secret, sans les données des autres membres, fichiers décrits sans leur contenu.
- Limite : un export par minute et par utilisateur (même mécanisme que les codes d'invitation) → 429.

## Suppression du compte

1. PWA : liste des foyers où l'utilisateur est propriétaire avec d'autres membres ; pour chacun, choisir le nouveau propriétaire (appel existant `PUT /owner`) ou « Ne pas choisir ». Puis l'e-mail retapé.
2. `DELETE /api/me {confirmEmail}` : e-mail différent → 400. Le profil est effacé et `UserDeletedV1` écrit dans la boîte d'envoi (même transaction).
3. Après validation, l'API supprime le compte Keycloak (`client_credentials` du client `life-manager-api`, puis `DELETE /admin/realms/life-manager/users/{id}`). Échec → 502 avec un message : les données sont déjà effacées ; une nouvelle connexion recrée un profil vide et permet de recommencer.
4. `household` reçoit `UserDeletedV1` : pour chaque foyer, seul membre → foyer supprimé ; propriétaire → foyer transmis au membre arrivé le plus tôt puis quitté ; membre → foyer quitté ; sa fiche (nom) est effacée. Les autres domaines réagissent comme aujourd'hui à `MemberLeftV1` / `HouseholdDeletedV1`. Idempotent.
- Les données saisies dans un foyer qui continue (dépenses…) restent au foyer, affichées « Ancien membre » : c'est écrit dans la politique de confidentialité.

## Keycloak

- Client `life-manager-api` : comptes de service activés, rôle `realm-management / manage-users`. Dev : realm importé mis à jour + `kcadm` sur le Keycloak déjà lancé. Prod : à faire par Dorian dans la console (procédure dans `docs/deploiement.md`).
