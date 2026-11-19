## Pourquoi

Avant d'aller plus loin (lot 7 « Comptes entre membres », puis peut-être d'autres foyers), l'application doit être en règle et transparente : des **conditions d'utilisation (CGU)**, une **politique de confidentialité (RGPD)**, et pour chaque utilisateur la possibilité de **télécharger ses données** (JSON) et de **supprimer son compte**.

Seconde moitié du **lot 6c** (v0.13.0), après `historique-rappels` (v0.12.0, qui apporte aussi le pied de page avec la version). Découpage validé par Dorian le 2026-11-19.

## Ce qui change (réponses de Dorian du 2026-11-19 intégrées)

### 1. CGU et politique de confidentialité

- Deux pages lisibles **sans être connecté** : `#/cgu` et `#/confidentialite`, avec un lien depuis la page de connexion et d'inscription Keycloak, et dans le pied de page de l'application.
- **Acceptation des CGU dans l'application** : écran « J'accepte » à la première connexion et **à chaque nouvelle version** des CGU. Version acceptée et date gardées dans le profil (domaine `identity`).
- **Dorian est un particulier, le service est gratuit** : les textes restent courts et n'exposent que le strict nécessaire.
  - Éditeur : un particulier qui publie à titre non professionnel peut ne pas afficher son identité, à condition que l'hébergeur la connaisse ; on indique alors seulement l'**hébergeur** (OVH, son adresse et son téléphone, publics) et l'adresse de contact. **Pas d'adresse postale ni de téléphone personnels.**
  - Responsable du traitement (RGPD) : « Dorian Grelu », contact **doriangrelu@gmail.com**. Le RGPD s'applique dès que l'application sert à d'autres personnes que soi (famille, amis) ; il demande l'identité et un moyen de contact, pas plus.
  - Contenu de la politique : données traitées (compte chez Keycloak, données saisies dans les foyers), seule finalité = faire fonctionner l'application (aucune publicité, revente ni mesure d'audience), hébergement OVH en France, sauvegardes chiffrées (serveur et OneDrive, 30 jours), durée (tant que le compte existe, puis 30 jours au plus dans les sauvegardes), stockage du navigateur limité à la connexion (pas de bandeau de cookies), droits (accès, rectification, export, suppression dans l'application, contact, réclamation à la CNIL).
  - CGU : service gratuit, fourni « en l'état », sans garantie de disponibilité ; usage personnel et familial ; l'utilisateur est responsable de ce qu'il saisit ; le service peut s'arrêter (prévenu à l'avance si possible, avec possibilité d'exporter ses données) ; droit français.
  - ⚠ Textes de bonne foi, pas un avis juridique.

### 2. Télécharger mes données (JSON)

- Bouton « Télécharger mes données » dans l'écran Foyer (section « Mon compte ») ; fichier produit à la demande, téléchargé directement, rien de stocké ; un export par minute au plus.
- **Seulement ce que l'utilisateur a saisi lui-même**, sans données techniques (identifiants internes, boîte d'envoi, journaux), sans secret (codes d'invitation, mot de passe, sessions), sans données des autres membres (ni leur e-mail ni leurs saisies), sans le contenu des fichiers (nom, type, taille, date seulement).
- Contenu : profil (e-mail, nom, date d'inscription, CGU acceptées) ; foyers dont il est membre (nom, rôle, date d'arrivée) ; dans chaque foyer, ce qu'il a saisi (voir question 2).

### 3. Supprimer mon compte

- Zone sensible de l'écran Foyer, confirmation forte (retaper son e-mail) dans tous les cas.
- Foyer où il est **seul** → supprimé avec ses données (mécanisme du lot 3), après la même confirmation.
- Foyer avec d'autres membres où il est **membre** → il le quitte (mécanisme du lot 3).
- Foyer avec d'autres membres où il est **propriétaire** → l'application **propose** de transmettre le foyer à un membre ; il peut **refuser** et supprimer quand même (voir question 1).
- Puis le profil local est effacé et le **compte Keycloak supprimé par l'API**, via l'API d'administration de Keycloak (`DELETE /admin/realms/life-manager/users/{id}`), avec un **compte de service** limité au realm `life-manager` (rôle `manage-users` seulement). Session terminée, retour à la page de connexion. Événement de contrat `UserDeletedV1` relayé par la boîte d'envoi.
- Technique : appel HTTP direct depuis Micronaut (jeton par `client_credentials`), par le réseau interne comme l'introspection, plutôt que la librairie Java d'administration de Keycloak (lourde pour l'exécutable natif). Dorian crée le client de service dans la console Keycloak de prod ; Claude prépare la procédure.

## Impact

- Backend : `identity` (CGU acceptées, suppression du profil, assemblage de l'export, appel à Keycloak), un fournisseur d'export dans chaque domaine, événement `UserDeletedV1`.
- Keycloak : liens CGU / confidentialité dans le thème de connexion ; client de service dans les realms de dev et de prod.
- PWA : pages publiques, écran d'acceptation, section « Mon compte » (export, suppression), liens dans le pied de page.
- Version **v0.13.0**.

## Questions encore ouvertes

1. **Propriétaire qui refuse de transmettre** alors qu'il reste des membres : que devient le foyer ?
   - (a) il passe tout seul au membre arrivé le plus tôt, les données restent pour les autres (ma recommandation : personne ne perd rien) ;
   - (b) il est supprimé pour tout le monde, avec toutes ses données (avertissement en rouge).
2. **« Ce qu'il a saisi »** : l'auteur est déjà connu pour les dépenses, les rentrées, les articles de courses, l'entretien, les factures et pièces jointes. Il **ne l'est pas** pour les catégories, budgets prévus, récurrents et crédits, véhicules, listes de courses et rappels.
   - (a) ajouter « créé par » à ces objets à partir de maintenant (les anciens, sans auteur, ne sont pas exportés), et inclure les récurrents dont il est le payeur (ma recommandation) ;
   - (b) se limiter aux objets dont l'auteur est déjà connu.
