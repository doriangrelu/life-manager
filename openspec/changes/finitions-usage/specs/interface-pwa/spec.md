## MODIFIED Requirements

### Requirement: Navigation principale

La PWA SHALL regrouper les écrans du foyer sélectionné en quatre espaces : Argent (Budget, Charges), Courses, Maison (Véhicules) et Foyer. Sur un écran de moins de 900 px de large, les espaces SHALL être présentés dans une barre d'onglets fixée en bas de l'écran, et les écrans d'un espace qui en compte plusieurs dans une rangée de sous-onglets sous l'en-tête ; ouvrir un espace SHALL afficher le dernier écran consulté dans cet espace sur l'appareil. Au-delà de 900 px, un menu latéral SHALL présenter chaque espace avec tous ses écrans. L'écran actif est signalé visuellement et pour les lecteurs d'écran. Un nouvel écran SHALL rejoindre un espace existant plutôt qu'ajouter un onglet.

#### Scenario: Mobile

- **WHEN** un membre ouvre la PWA sur un écran de 390 px de large
- **THEN** la barre d'onglets Argent, Courses, Maison, Foyer est visible en bas de l'écran, l'espace affiché étant mis en évidence

#### Scenario: Sous-onglets d'un espace

- **WHEN** un membre est dans l'espace Argent sur mobile
- **THEN** les sous-onglets Budget et Charges sont visibles sous l'en-tête et l'écran affiché est mis en évidence

#### Scenario: Dernier écran mémorisé

- **WHEN** un membre consulte Charges, passe dans Courses, puis touche Argent
- **THEN** l'écran Charges s'affiche

#### Scenario: Grand écran

- **WHEN** un membre ouvre la PWA sur un écran de 1280 px de large
- **THEN** un menu latéral présente les espaces et tous leurs écrans, et le contenu occupe le reste de la largeur

### Requirement: Noms enregistrés normalisés, mis en forme à l'affichage

Les articles de courses (liste et historique), noms de sous-liste, noms de catégorie, libellés de dépense et noms de foyer SHALL être enregistrés sans espaces autour, avec les espaces intérieurs réduits à un seul, et en minuscules. Les noms de véhicule, plaques, notes d'entretien, noms d'affichage, courriels et noms de fichier SHALL garder leur casse. La PWA SHALL afficher les noms normalisés avec une majuscule initiale, et les noms de personnes avec une majuscule à chaque mot. Les données déjà enregistrées SHALL être converties.

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

#### Scenario: Nom d'une personne

- **WHEN** le nom d'affichage d'un membre est « jean-pierre martin »
- **THEN** la PWA affiche « Jean-Pierre Martin » partout où ce membre apparaît

## ADDED Requirements

### Requirement: Retour en haut

La PWA SHALL afficher chaque nouvel écran à partir du haut, et SHALL remonter en haut d'un formulaire resté ouvert pour une saisie enchaînée.

#### Scenario: Changement d'écran

- **WHEN** un membre fait défiler la liste des dépenses puis ouvre l'écran Courses
- **THEN** l'écran Courses s'affiche depuis son début

#### Scenario: Saisie enchaînée

- **WHEN** un membre enregistre une dépense avec « Saisir une autre dépense ensuite » coché
- **THEN** le formulaire vidé est affiché depuis son début, le champ du montant prêt
