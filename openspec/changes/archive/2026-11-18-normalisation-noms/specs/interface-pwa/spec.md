## ADDED Requirements

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
