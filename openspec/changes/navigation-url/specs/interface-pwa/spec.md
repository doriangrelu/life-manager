## ADDED Requirements

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
