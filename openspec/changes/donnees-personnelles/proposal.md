## Pourquoi

Avant d'aller plus loin (lot 7 « Comptes entre membres », puis peut-être d'autres foyers), l'application doit être en règle et transparente : des **conditions d'utilisation (CGU)**, une **politique de confidentialité (RGPD)**, et pour chaque utilisateur la possibilité de **télécharger ses données** (JSON) et de **supprimer son compte**. Dorian souhaite aussi voir la **version déployée** dans un pied de page.

C'est le **lot 6c « Données personnelles »**, placé avant le lot 7 à la demande de Dorian (le lot 6b « E-mails » reste planifié plus tard).

## Ce qui change (proposition, à valider par Dorian)

### 1. CGU et politique de confidentialité

- Deux pages lisibles **sans être connecté** : `#/cgu` et `#/confidentialite`, avec un lien depuis la page de connexion et d'inscription Keycloak, et depuis l'application.
- **Acceptation des CGU** : à la première connexion (et à chaque nouvelle version des CGU), un écran « J'accepte » avant d'entrer dans l'application. La version acceptée et la date sont gardées dans le profil (domaine `identity`).
- Je rédige un premier texte, que Dorian relit. Contenu prévu de la politique de confidentialité :
  - responsable du traitement : Dorian (coordonnées à fournir) ;
  - données traitées : compte (e-mail, nom, mot de passe chez Keycloak), données saisies dans les foyers (dépenses, rentrées, récurrents, véhicules, courses, rappels, factures et pièces jointes) ;
  - finalité : faire fonctionner l'application, rien d'autre (aucune publicité, aucune revente, aucun outil de mesure d'audience) ;
  - hébergement : OVH en France ; sauvegardes : sur le serveur et sur OneDrive, **chiffrées** (Microsoft ne peut pas les lire), gardées 30 jours ;
  - durée : tant que le compte existe ; après suppression, les données disparaissent des sauvegardes au bout de 30 jours au plus ;
  - cookies et stockage du navigateur : uniquement ce qui est nécessaire à la connexion (pas de bandeau de consentement à prévoir) ;
  - droits : accès, rectification, export, suppression (dans l'application), contact, réclamation auprès de la CNIL.
- ⚠ Ce sont des textes de bonne foi, pas un avis juridique.

### 2. Télécharger mes données (JSON)

- Bouton « Télécharger mes données » dans l'écran Foyer (section « Mon compte »). Le fichier est produit à la demande et téléchargé directement (aucun lien public, rien de stocké sur le serveur).
- Chaque domaine fournit sa part via son `contract` ; un nouveau petit domaine (ou `identity`) assemble le tout.
- **Proposition de contenu** :

| Inclus | Exclu, et pourquoi |
|---|---|
| Mon profil : e-mail, nom, date d'inscription, CGU acceptées | Mot de passe, sessions, jetons : chez Keycloak, jamais dans l'application ; la console « Mon compte » les montre déjà |
| Mes foyers : nom, mon rôle (propriétaire ou membre), date d'arrivée | **Codes d'invitation** : secret encore utilisable par un tiers |
| Pour chaque foyer, les données que je peux déjà voir dans l'application : dépenses, rentrées, catégories, budgets prévus, récurrents et crédits, véhicules et carnet, listes de courses, rappels | **E-mails des autres membres** : donnée personnelle d'un tiers ; seul leur nom apparaît (comme dans l'application) |
| Factures et pièces jointes : **nom, type, taille et date** seulement | **Contenu des fichiers** : trop lourd pour un JSON ; à prévoir en ZIP plus tard si besoin |
| | Identifiants techniques internes, boîte d'envoi, journaux : ils n'apprennent rien et décrivent l'intérieur du système |

- Garde-fous : export limité à **un par minute** par utilisateur ; contrôle d'accès habituel par foyer ; fichier nommé `life-manager-mes-donnees-AAAA-MM-JJ.json`, avec un champ `version` du format.

### 3. Supprimer mon compte

- Bouton « Supprimer mon compte » dans la zone sensible de l'écran Foyer, avec confirmation forte (retaper son adresse e-mail).
- Pour chacun de mes foyers :
  - **seul membre** → le foyer est supprimé entièrement (mécanisme du lot 3) ;
  - **membre** → je quitte le foyer (mécanisme du lot 3 : les données communes restent au foyer, mon nom devient « Ancien membre », mes récurrents passent au propriétaire) ;
  - **propriétaire avec d'autres membres** → voir question 3.
- Puis le profil local est effacé et le **compte Keycloak supprimé** (voir question 4). Session terminée, retour à la page de connexion.
- Nouvel événement de contrat `UserDeletedV1` relayé par la boîte d'envoi, comme `MemberLeftV1`.

### 4. Version dans un pied de page

- Un pied de page discret (petit texte gris) avec la **version déployée** (`v0.12.0`) et les liens CGU / Confidentialité. Sur mobile, en bas de l'écran Foyer (la barre de navigation occupe déjà le bas) ; sur grand écran, sous le menu latéral.
- La version est injectée dans la PWA au moment de la construction de l'image (tag Git), et l'API l'expose aussi (`GET /api/version`) : le pied de page affiche les deux si elles diffèrent (utile juste après une livraison).

## Impact

- Backend : `identity` (acceptation des CGU, suppression du profil, assemblage de l'export, appel Keycloak), un fournisseur d'export dans chaque domaine (`budget`, `bills`, `vehicle`, `shopping`, `reminder`, `household`), événement `UserDeletedV1`, route de version.
- Keycloak : liens vers CGU et confidentialité dans le thème de connexion ; selon la question 4, un compte de service ou l'action « supprimer le compte ».
- PWA : pages publiques CGU et confidentialité, écran d'acceptation, section « Mon compte » (export, suppression), pied de page.
- CI / livraison : version passée à la construction de l'image PWA et de l'API.
- Version **v0.13.0** (après le petit change `historique-rappels`, voir sa proposition), ou une seule version selon la question 6.

## Questions pour Dorian

1. **Coordonnées** dans la politique de confidentialité : quelle adresse de contact (une adresse dédiée, par exemple sur dorian-grelu.fr ?) ; ton nom complet comme responsable du traitement, ça te va ?
2. **Contenu de l'export** : d'accord avec le tableau ci-dessus (toutes les données des foyers que je vois, sans e-mails des autres ni codes d'invitation ni contenu des fichiers) ? Ou seulement ce que **j'ai moi-même saisi** (plus strict, mais moins utile) ?
3. **Propriétaire qui supprime son compte** alors qu'il reste des membres : (a) l'application l'oblige d'abord à transmettre le foyer (ma recommandation, simple et sans surprise) ; ou (b) le foyer passe tout seul au membre le plus ancien ?
4. **Suppression du compte Keycloak** : (a) l'API le supprime elle-même avec un compte de service limité au realm `life-manager` (ma recommandation : tout se fait en un clic, rien ne reste à moitié) ; ou (b) l'application efface ses données puis envoie vers l'écran de suppression de Keycloak (pas de compte de service, mais l'utilisateur peut s'arrêter en route et garder un compte vide). Dans les deux cas, c'est toi qui crées les réglages dans la console Keycloak de prod.
5. **Acceptation des CGU** : un écran « J'accepte » dans l'application à la 1re connexion et à chaque nouvelle version (ma recommandation), ou une simple case à l'inscription chez Keycloak ?
6. **Découpage** : d'abord le petit change « historique des rappels » + le pied de page avec la version (v0.12.0, rapide), puis les données personnelles (v0.13.0) ? Ou tout ensemble ?
