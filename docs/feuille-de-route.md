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

Découpé en deux changes : **4a « Rentrées et épargne »** (proposition `openspec/changes/rentrees-epargne`, en relecture), puis **4b « Prévisionnel et crédits »**.

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
- Coût réel de chaque véhicule sur l'année (entretien + charges liées).

### Lot 6 — Rappels libres et e-mails

- Rappels saisis à la main : date, récurrence, note (impôts, ramonage, mutuelle…).
- **Rappels par e-mail via Brevo**, au choix de chaque membre.
- **E-mails de Keycloak aux couleurs de l'application** (mot de passe oublié, vérification), envoyés par Brevo.
- Prérequis technique : réparer le HTTPS sortant de l'exécutable natif (voir dette).

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
| Mise à jour automatique du VPS chaque nuit, redémarrage si nécessaire, vérification de vivacité et de disponibilité des services | ✅ installé le 2026-11-19 (`infra/vps`, chaque nuit à 5 h UTC, journal `/var/log/vps-maintenance.log`) |
| Documentation technique : domaines, modèles de données et flux d'événements en **diagrammes** (Mermaid dans `docs/`) | à faire, mise à jour à chaque lot |

## Dette technique

| Sujet | Risque aujourd'hui | Quand |
|---|---|---|
| ~~Événements entre domaines sans « outbox »~~ | ✅ soldé le 2026-11-19 (v0.3.1) | — |
| HTTPS sortant depuis l'exécutable natif | échoue (`decrypt_error`) ; contourné par le réseau interne | **avant le lot 6** (Brevo), puis IA |
| Test de restauration complète | jamais fait sur une machine vierge | dès qu'un VPS de test est disponible |

## Idées à ranger

(vide)

## Questions pour Dorian

1. ~~L'ordre te convient-il ?~~ Validé le 2026-11-19, avec le prévisionnel (lot 4) avant les graphiques (lot 5).
2. Lot 7 : « dettes et abonnements partagés », c'est bien « qui doit combien à qui » entre membres du foyer ?
3. Lot 11 : comptes-tu ouvrir l'application à d'autres foyers que le tien ? Sinon ce lot peut attendre.
4. Lot 10 : ta préférence pour l'IA, service externe (simple, payant, données qui sortent) ou modèle local (gratuit, mais le VPS actuel est léger) ?
