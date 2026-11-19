# Feuille de route

> **Version 2 à valider par Dorian** (2026-11-19) : intègre les idées et retours de la nuit du 18 au 19 novembre.

## Comment on avance (lien avec OpenSpec)

- **Ce document est le carnet de commandes** : chaque idée y est rangée dans un lot, avec son ordre et ses dépendances. Rien n'est codé directement depuis ici.
- **Un lot = un change OpenSpec** au moment où on l'attaque : proposition (pourquoi, quoi, hors périmètre), relecture par Dorian, puis design, tâches, code, version `vX.Y.0`, déploiement, recette et archive.
- **Les petites retouches d'interface** sont regroupées dans un lot de finitions, qui donne un seul change.
- **Les sujets incertains** (IA, connexion à des services externes) commencent par une **étude** : une note dans `docs/etudes/` (options, coûts, risques, recommandation). Le change OpenSpec ne vient qu'après ton accord.
- Une nouvelle idée s'ajoute en bas de la section « Idées à ranger », puis on la place à la revue suivante.

## Où on en est

En production sur https://maison.dorian-grelu.fr (v0.3.1) :

| Domaine | Livré |
|---|---|
| Socle | comptes Keycloak (connexion et console « Mon compte » aux couleurs de l'application), foyers, invitation par code |
| Budget | saisie rapide, catégories libres, vue mensuelle par catégorie et par personne |
| Charges | abonnements et factures récurrents, dépense créée seule ou à confirmer, factures jointes |
| Véhicules | fiches, carnet d'entretien, pièces jointes, échéances CT et révision |
| Courses | listes partagées en temps réel, sous-listes, historique |
| Rappels | échéances des véhicules et des charges, notifications de l'appareil (application ouverte) |
| Fiabilité | événements entre domaines garantis (boîte d'envoi), parcours natif en CI, sauvegarde nocturne |

Lots terminés : lot 0 (finitions de mise en production, v0.2.x), lot 1 (factures et abonnements, v0.3.0), boîte d'envoi (v0.3.1).

## Ordre proposé

### Lot 2 — Finitions d'usage ✅ (livré le 2026-11-19, v0.4.0, en recette)

En plus : cloisonnement des foyers vérifié automatiquement sur toutes les routes (demande de Dorian).

- Budget : bouton **« Aujourd'hui »** pour revenir au mois en cours.
- Mobile : à l'ouverture d'un formulaire (nouvelle dépense…), la page et la fenêtre repartent **en haut**.
- **Affichage des noms** harmonisé partout (majuscules, noms de membres, catégories, listes).
- Courses : **glisser-déposer** pour ordonner les articles et les sous-listes.
- **Navigation par espaces** : les onglets regroupés par thème (Argent, Maison, Foyer…), pour que la barre du bas reste courte quand les fonctions d'argent vont se multiplier (prévisionnel, comptes entre membres, projets…).
- Front : composants communs rangés façon **Atomic Design** (atomes, molécules, organismes), au fil des écrans touchés, pour ne plus dupliquer.

### Lot 3 — Gestion du foyer ✅ (livré le 2026-11-19, v0.5.0, en recette)

En plus : renommage du foyer ; invitations réservées au propriétaire (choix de Dorian).

- **Propriétaire** du foyer (le créateur au départ, transmissible).
- Le propriétaire peut **retirer un membre**.
- **Annuler un code d'invitation** encore valide.
- **Supprimer un foyer**, avec confirmation forte. Toutes les données du foyer sont effacées dans chaque domaine, par événement.

À faire avant d'ouvrir l'application à d'autres foyers.

### Lot 4 — Rentrées d'argent, budget prévisionnel et crédits

Découpé en trois changes : **4a « Rentrées et bilan »** ✅ (livré le 2026-11-19, v0.6.0), **4b1 « Budget prévu et reste à vivre »** ✅ (v0.7.0, en recette), puis **4b2 « Crédits »** ✅ (v0.8.0, en recette).

4b2, tel que livré, en version simple à la demande de Dorian : dans Récurrents, « Ajouter » propose Dépense ou **Crédit** ; un crédit se saisit avec la mensualité du prêt, celle de l'assurance (facultative) et la date de la dernière mensualité (facultative) ; une seule dépense par mois (prêt + assurance) ; avec une date de fin, la ligne affiche les mensualités restantes et le crédit s'arrête seul. Capital restant dû, taux et tableau d'amortissement : non faits, à reprendre si le besoin apparaît.

4b1, tel que livré : budget prévu par catégorie de dépenses, réglé en touchant la catégorie dans le Budget, pour **tous les mois** (à partir du mois affiché, sans toucher aux mois passés) ou pour **ce mois seulement** ; barre dépensé / prévu orange à 80 %, rouge au-delà, avec les dépenses récurrentes à venir en plus clair ; **reste à vivre** (rentrées prévues moins dépenses récurrentes, puis moins les dépenses courantes) dans le bandeau du Budget et dans le Bilan, quand le foyer suit ses rentrées. Notifications de dépassement : lot 6.

4a, tel que livré : réglage du foyer « Suivre les rentrées » (propriétaire, désactivé par défaut : rien ne change sans lui) ; rentrées saisies comme les dépenses (catégories libres, suggestions Salaire, Prime, Remboursement, Aides, Autre ; qui reçoit) ; bandeau du mois Rentré / Dépensé / Résultat ; Charges en deux panneaux Dépenses fixes / Rentrées fixes ; écran **Bilan** (mois, trimestre, année, 12 derniers mois ; réalisé et prévu en fin de période ; épargne = résultat positif). Comptes, livrets et solde de départ abandonnés à la demande de Dorian.

- **Rentrées d'argent** (idée du frère de Dorian, à mûrir à l'implémentation) : salaires, primes, remboursements, aides… saisis à la main ou **récurrents** (comme les charges), par membre ou pour le foyer. Dans l'espace Argent. Le mois affiche alors rentrées, dépenses et **solde**.
- **Épargne mensuelle** : une case dédiée chaque mois, avec le montant mis de côté (fixe et récurrent, ou saisi). Elle est comptée à part des dépenses, pour suivre l'épargne cumulée et, au lot 5, son évolution.
- Budget prévisionnel du mois par catégorie : rentrées prévues, charges récurrentes (lot 1), mensualités, donc **reste à vivre**.
- **Suivi du prévisionnel** : prévu et réel par catégorie, alerte en cas de dépassement.
- Crédits en cours : capital, taux, mensualité, échéancier, capital restant dû.

Placé avant les graphiques (choix de Dorian) : ils pourront ainsi montrer à la fois le réalisé et le prévu.

### Lot 5 — Budget : vision annuelle et graphiques

- Dépenses de l'**année** : total et répartition **par catégorie**.
- **Graphiques par mois** sur le réalisé et le prévisionnel (lot 4) : évolution du total, des principales catégories, écart au prévu.
- **Comparatif rentrées / dépenses** : par mois et sur l'année, épargne dégagée.
- ✅ v0.10.0 : onglet **« Évolution »** du Bilan (rentrées, dépenses et épargne cumulée mois par mois ; dépenses face au prévu, toutes catégories ou une seule). L'année et la répartition par catégorie existaient déjà.
- **En attente** (choix de Dorian, 2026-11-19) : coût réel de chaque véhicule sur l'année. Piste : un champ « Véhicule » facultatif sur une dépense, additionné au carnet d'entretien.

### Lot 6 — Rappels libres et e-mails

Découpé en deux à la demande de Dorian (2026-11-19) :

- **6a « Rappels libres »** (en cours de cadrage) : rappels saisis à la main, avec une date, une récurrence et une note (impôts, ramonage, mutuelle…), dans le même bandeau et les mêmes notifications que les rappels automatiques.
- **6b « E-mails » — planifié plus tard** :
  - **rappels par e-mail via Brevo**, au choix de chaque membre ;
  - **e-mails de Keycloak aux couleurs de l'application** (mot de passe oublié, vérification), envoyés par Brevo ;
  - prérequis technique : réparer le HTTPS sortant de l'exécutable natif (voir dette).

### Lot 6c — Données personnelles et historique des rappels ✅ (livré le 2026-11-19, v0.12.0 et v0.13.0, en recette)

Demandé par Dorian le 2026-11-19. Deux changes OpenSpec :

- ✅ **`historique-rappels`** (v0.12.0, livré le 2026-11-19, en recette) : trace de chaque « C'est fait » (titre, date prévue, date faite, qui), section « Historique » dans l'écran Rappels, ligne retirable par tout membre, effacée après un an ; **version déployée** dans le pied de page.
- ✅ **`donnees-personnelles`** (v0.13.0, livré le 2026-11-19, en recette) :
  - **CGU** et **politique de confidentialité (RGPD)**, lisibles sans être connecté, à accepter dans l'application à chaque nouvelle version ;
  - **télécharger mes données** en JSON : seulement ce que l'utilisateur a saisi (« créé par » noté à partir de la v0.13.0), rien de secret ni de technique, rien des autres membres, fichiers décrits sans leur contenu ;
  - **supprimer mon compte** : foyers où il est seul supprimés, foyers partagés transmis (au choix, sinon au membre le plus ancien) puis quittés, profil effacé, compte Keycloak supprimé ;
  - liens CGU / confidentialité dans le pied de page.
  - À faire à part, avec l'accord de Dorian (redémarrage de Keycloak) : liens CGU / confidentialité sur la page de connexion.

### Lot 6d — Stabilisation ✅ (livré le 2026-11-19, v0.13.1, en recette)

15 bugs corrigés (10 dans la PWA, 5 dans le backend, chacun avec un test quand c'était possible) ; parcours natif indépendant des comptes de dev. Laissés pour plus tard : liens `charge_expense` / `charge_income` orphelins après suppression d'un foyer (changement de schéma, à l'accord de Dorian), requêtes en boucle dans `bills` (sans effet visible au volume actuel), découpage des gros écrans.

Demandé par Dorian le 2026-11-19 : chasse aux bugs, corrections, et améliorations ou optimisations du code existant si c'est utile. Aucune nouvelle fonctionnalité. Change OpenSpec `stabilisation`.

### Lot 7 — Comptes entre membres

- **Qui doit quoi** : dépenses et abonnements partagés, part de chacun, solde entre membres, remboursement enregistré.
- S'appuie sur « payé par / pour » déjà présent dans les dépenses et les charges.

### Lot 8 — Projets

- Projets du foyer (travaux, vacances, mariage…) avec un **budget**, des **dépenses rattachées** et une **liste de tâches**.

### Lot 9 — Documents et démarches (données les plus sensibles)

- Coffre-fort de documents du foyer, classés par type et par personne.
- Dates d'expiration (CNI, passeport, permis), qui deviennent des rappels (lot 6).
- **À trancher avant** : chiffrement des fichiers au repos, qui peut y accéder dans le foyer.

### Lot 10 — Relevés bancaires et IA (précédé d'une étude)

- **Étude A, connexions externes** : quels services peuvent alimenter l'application tout seuls (agrégation bancaire DSP2, fournisseurs d'énergie, assurances), avec coût, sécurité et faisabilité en auto-hébergé. La vision excluait l'agrégation automatique : à rediscuter avec l'étude.
- **Étude B, IA** : quel modèle et où (API externe ou modèle local), quelles données envoyées, coût, confidentialité.
- Puis :
  - **import de relevés** (CSV/OFX) avec rapprochement automatique des dépenses ;
  - **catégorisation par IA** ;
  - **analyse des dépenses avec propositions d'optimisation** (abonnements doublons, postes qui dérivent…).

### Lot 11 — Offre ouverte : limites et VIP (si l'application s'ouvre au-delà de la famille)

- **Limites** sur le nombre d'objets créés (foyers, invités, dépenses, listes, catégories…) pour les comptes standards.
- **VIP sans limite**, repérés par un **rôle Keycloak**.
- Dépend du lot 3.

### Ensuite

- Notifications push (application fermée), avec choix par membre.
- Courses hors connexion (magasin sans réseau).
- **Simulateur fiscal** (idée du frère de Dorian, **pas prioritaire**) : estimation de l'impôt sur le revenu à partir des rentrées de l'année (lot 4), selon le barème en vigueur. À cadrer par une étude, car les règles changent chaque année et ce ne peut être qu'une estimation.

## Exploitation et documentation (au fil de l'eau, hors lots)

| Sujet | État |
|---|---|
| Mise à jour automatique du VPS chaque nuit, redémarrage si nécessaire, vérification de vivacité et de disponibilité des services | ✅ installé le 2026-11-19 (dépôt `vps-socle`, dossier `systeme/`, chaque nuit à 5 h UTC, journal `/var/log/vps-maintenance.log`) |
| Documentation technique : domaines, modèles de données et flux d'événements en **diagrammes** (Mermaid dans `docs/`) | à faire, mise à jour à chaque lot |

## Dette technique

| Sujet | Risque aujourd'hui | Quand |
|---|---|---|
| ~~Événements entre domaines sans « outbox »~~ | ✅ soldé le 2026-11-19 (v0.3.1) | — |
| HTTPS sortant depuis l'exécutable natif | échoue (`decrypt_error`) ; contourné par le réseau interne | **avant le lot 6** (Brevo), puis IA |
| Test de restauration complète | jamais fait sur une machine vierge | dès qu'un VPS de test est disponible |

## Idées à ranger

### Petites corrections à traiter

Aucune pour l'instant. Les corrections signalées le 2026-11-19 sont livrées avec la **v0.6.2** : le logo et le survol du menu de la console « Mon compte », la précision du type « Autre » dans le carnet d'entretien, et la gestion des catégories de rentrées.

## Questions pour Dorian

1. ~~L'ordre te convient-il ?~~ Validé le 2026-11-19, avec le prévisionnel (lot 4) avant les graphiques (lot 5).
2. Lot 7 : « dettes et abonnements partagés », c'est bien « qui doit combien à qui » entre membres du foyer ?
3. Lot 11 : comptes-tu ouvrir l'application à d'autres foyers que le tien ? Sinon ce lot peut attendre.
4. Lot 10 : ta préférence pour l'IA, service externe (simple, payant, données qui sortent) ou modèle local (gratuit, mais le VPS actuel est léger) ?
