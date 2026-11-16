## Purpose

Tenir la fiche de chaque véhicule du foyer avec ses prochaines échéances (contrôle technique, révision), pour que chaque membre sache quand agir.

## ADDED Requirements

### Requirement: Ajout d'un véhicule

Le système SHALL permettre à un membre d'ajouter un véhicule à son foyer avec un nom de 1 à 40 caractères, une immatriculation facultative (12 caractères au plus, enregistrée en majuscules) et deux échéances facultatives : prochain contrôle technique et prochaine révision.

#### Scenario: Nouveau véhicule

- **WHEN** un membre ajoute le véhicule « Clio » immatriculé « ab-123-cd », contrôle technique le 2027-03-01
- **THEN** le véhicule est créé avec l'immatriculation « AB-123-CD », l'échéance de contrôle technique au 2027-03-01 et aucune échéance de révision

#### Scenario: Nom invalide

- **WHEN** un membre ajoute un véhicule au nom vide ou de plus de 40 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Non-membre

- **WHEN** un utilisateur qui n'est pas membre du foyer ajoute ou consulte ses véhicules
- **THEN** l'API répond `404 Not Found`

### Requirement: Liste des véhicules

Le système SHALL permettre à un membre de lister les véhicules de son foyer, triés par nom, avec leurs échéances.

#### Scenario: Consultation

- **WHEN** un membre consulte les véhicules d'un foyer qui en compte deux
- **THEN** il obtient les deux véhicules triés par nom, chacun avec son immatriculation et ses échéances

#### Scenario: Partage dans le foyer

- **WHEN** un membre ajoute un véhicule puis un autre membre du même foyer consulte la liste
- **THEN** le second membre voit le véhicule ajouté

### Requirement: Modification d'un véhicule

Le système SHALL permettre à un membre de modifier le nom, l'immatriculation et les échéances d'un véhicule de son foyer, avec les mêmes règles qu'à l'ajout. Une échéance peut être effacée ou être déjà passée.

#### Scenario: Nouvelle échéance

- **WHEN** un membre remplace l'échéance de contrôle technique du 2027-03-01 par le 2029-03-01
- **THEN** le véhicule porte la nouvelle échéance

#### Scenario: Véhicule d'un autre foyer

- **WHEN** un membre modifie un véhicule inconnu ou appartenant à un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Suppression d'un véhicule

Le système SHALL permettre à un membre de supprimer définitivement un véhicule de son foyer, avec tout son carnet d'entretien.

#### Scenario: Véhicule vendu

- **WHEN** un membre supprime un véhicule qui a des opérations d'entretien
- **THEN** le véhicule et ses opérations disparaissent de la liste et du carnet
