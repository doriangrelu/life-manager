# interface-pwa Specification

## Purpose
Fixer les règles communes de l'interface de la PWA (navigation, fenêtres modales, thème, accessibilité) pour que chaque écran, présent ou à venir, reste cohérent sur mobile comme sur grand écran.

## Requirements

### Requirement: Navigation principale

La PWA SHALL proposer, pour le foyer sélectionné, quatre sections : Budget, Courses, Véhicules et Foyer. Sur un écran de moins de 900 px de large, elles SHALL être présentées dans une barre d'onglets fixée en bas de l'écran ; au-delà, dans un menu latéral. La section active est signalée visuellement et pour les lecteurs d'écran.

#### Scenario: Mobile

- **WHEN** un membre ouvre la PWA sur un écran de 390 px de large
- **THEN** la barre d'onglets Budget, Courses, Véhicules, Foyer est visible en bas de l'écran, la section affichée étant mise en évidence

#### Scenario: Grand écran

- **WHEN** un membre ouvre la PWA sur un écran de 1280 px de large
- **THEN** les quatre sections sont présentées dans un menu latéral et le contenu occupe le reste de la largeur

### Requirement: Saisie dans une fenêtre modale

La PWA SHALL ouvrir chaque formulaire de saisie ou de modification dans une fenêtre modale : un panneau qui monte du bas de l'écran sur mobile, une fenêtre centrée sur grand écran. La modale SHALL garder le focus clavier à l'intérieur tant qu'elle est ouverte, se fermer avec la touche Échap ou un bouton « Fermer », et rendre le focus à l'élément qui l'a ouverte.

#### Scenario: Ajout d'une dépense

- **WHEN** un membre touche le bouton d'ajout de l'écran Budget
- **THEN** le formulaire de dépense s'ouvre dans un panneau qui recouvre le bas de l'écran, l'écran du mois restant visible, assombri, derrière

#### Scenario: Fermeture au clavier

- **WHEN** une modale est ouverte et que le membre appuie sur Échap
- **THEN** la modale se ferme sans rien enregistrer et le focus revient sur le bouton qui l'avait ouverte

### Requirement: Confirmation des suppressions

La PWA SHALL demander confirmation, dans une fenêtre modale indiquant clairement ce qui sera supprimé, avant toute suppression définitive (dépense, catégorie, véhicule, opération, article, sous-liste).

#### Scenario: Suppression d'une dépense

- **WHEN** un membre demande la suppression d'une dépense de 42,50 €
- **THEN** une modale « Supprimer la dépense de 42,50 € ? » propose « Supprimer » et « Annuler », et rien n'est supprimé avant le choix

### Requirement: Thème et accessibilité

La PWA SHALL appliquer le thème de la maquette validée (couleurs, police, arrondis), en clair et en sombre selon le réglage de l'appareil. Tout texte SHALL respecter un contraste d'au moins 4,5:1 (3:1 pour un texte de 24 px et plus), et toute cible tactile SHALL mesurer au moins 44 × 44 px.

#### Scenario: Thème sombre

- **WHEN** l'appareil du membre est réglé en mode sombre
- **THEN** la PWA s'affiche en thème sombre, avec des contrastes conformes

#### Scenario: Cibles tactiles

- **WHEN** un membre utilise la PWA sur mobile
- **THEN** chaque bouton, onglet, puce et case à cocher offre une zone tactile d'au moins 44 × 44 px

### Requirement: Affichage sans débordement

La PWA SHALL s'afficher sans défilement horizontal sur un écran de 360 px de large et plus, sur tous ses écrans et dans toutes ses modales.

#### Scenario: Petit téléphone

- **WHEN** un membre parcourt chaque section et ouvre chaque modale sur un écran de 360 px de large
- **THEN** aucun contenu ne déborde horizontalement

### Requirement: Écran conservé au rechargement

La PWA SHALL refléter dans l'adresse de la page l'écran affiché : la section, le mois consulté dans Budget et le véhicule ouvert. Au rechargement de la page, la PWA SHALL réafficher cet écran ; si l'élément désigné n'existe plus, elle SHALL afficher la liste de la section. Les boutons Précédent et Suivant du navigateur, et le bouton Retour d'Android, SHALL naviguer entre les écrans visités. Une fenêtre modale ouverte n'est pas conservée.

#### Scenario: Rechargement sur un véhicule

- **WHEN** un membre consulte la fiche du véhicule « Clio » puis recharge la page
- **THEN** la fiche de « Clio » s'affiche de nouveau

#### Scenario: Rechargement sur un mois passé

- **WHEN** un membre consulte le budget d'octobre 2026 puis recharge la page
- **THEN** le budget d'octobre 2026 s'affiche de nouveau

#### Scenario: Retour arrière

- **WHEN** un membre passe de Courses à Véhicules, ouvre « Clio », puis appuie deux fois sur Précédent
- **THEN** il revient à la liste des véhicules, puis à Courses

#### Scenario: Véhicule supprimé

- **WHEN** l'adresse désigne un véhicule supprimé ou d'un autre foyer et que la page est chargée
- **THEN** la liste des véhicules du foyer s'affiche

### Requirement: Noms enregistrés normalisés, mis en forme à l'affichage

Les articles de courses (liste et historique), noms de sous-liste, noms de catégorie, libellés de dépense et noms de foyer SHALL être enregistrés sans espaces autour, avec les espaces intérieurs réduits à un seul, et en minuscules. Les noms de véhicule, plaques, notes d'entretien, noms d'affichage, courriels et noms de fichier SHALL garder leur casse. La PWA SHALL afficher les noms normalisés avec une majuscule initiale. Les données déjà enregistrées SHALL être converties.

#### Scenario: Saisie d'un article

- **WHEN** un membre ajoute l'article «  LAIT   demi-écrémé »
- **THEN** l'article est enregistré « lait demi-écrémé » et affiché « Lait demi-écrémé »

#### Scenario: Doublon de casse

- **WHEN** une catégorie « courses » existe et un membre crée la catégorie « Courses »
- **THEN** la création est refusée comme doublon

#### Scenario: Véhicule

- **WHEN** un membre nomme un véhicule « Clio RS »
- **THEN** il est enregistré et affiché « Clio RS »

#### Scenario: Données existantes

- **WHEN** la nouvelle version démarre sur une base contenant la catégorie « Loisirs »
- **THEN** la catégorie est enregistrée « loisirs » et toujours affichée « Loisirs »

### Requirement: Retours visuels de chargement et d'attente

La PWA SHALL afficher des formes de chargement à la place d'un contenu en cours de premier chargement, et SHALL n'afficher un message de liste vide qu'une fois les données reçues. Un bouton dont l'action est en cours SHALL l'indiquer par une animation et rester inactif jusqu'à la fin. Les éléments cliquables SHALL réagir au survol de la souris.

#### Scenario: Ouverture d'un onglet

- **WHEN** un membre ouvre l'onglet Véhicules et que la liste n'est pas encore reçue
- **THEN** des formes de chargement s'affichent, et non le message « Aucun véhicule »

#### Scenario: Enregistrement en cours

- **WHEN** un membre enregistre une dépense
- **THEN** le bouton affiche une animation d'attente et ne peut pas être actionné une seconde fois

### Requirement: Saisie du montant

La fenêtre de saisie d'une dépense SHALL centrer le montant et sa devise, et SHALL placer le curseur dans le champ du montant à l'ouverture.

#### Scenario: Nouvelle dépense

- **WHEN** un membre ouvre la saisie d'une dépense
- **THEN** il peut taper le montant immédiatement, affiché centré avec « € »
