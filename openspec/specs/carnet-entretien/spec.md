# carnet-entretien Specification

## Purpose
Garder l'historique des opérations d'entretien de chaque véhicule du foyer (date, type, kilométrage, coût, note, factures et photos jointes), consultable par tous ses membres.

## Requirements

### Requirement: Ajout d'une opération d'entretien

Le système SHALL permettre à un membre d'ajouter une opération au carnet d'un véhicule de son foyer, avec : une date qui n'est pas dans le futur, un type parmi révision, vidange, contrôle technique, pneus, réparation et autre, et facultativement un kilométrage (0 à 2 000 000 km), un coût en centimes d'euro (0 à 100 000 €) et une note de 140 caractères au plus.

#### Scenario: Révision

- **WHEN** un membre ajoute une révision du 2026-10-12 à 45 000 km pour 189,90 € (18990 centimes)
- **THEN** l'opération est enregistrée avec ces valeurs

#### Scenario: Date future

- **WHEN** un membre ajoute une opération datée de demain
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Valeur hors bornes

- **WHEN** un membre ajoute une opération avec un kilométrage négatif, un coût négatif ou un type inconnu
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Véhicule d'un autre foyer

- **WHEN** un membre ajoute une opération à un véhicule inconnu ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Consultation du carnet

Le système SHALL permettre à un membre de consulter le carnet d'un véhicule de son foyer, l'opération la plus récente en premier.

#### Scenario: Historique

- **WHEN** un membre consulte le carnet d'un véhicule ayant une opération de 2025 et une de 2026
- **THEN** il obtient les deux opérations, celle de 2026 en premier

### Requirement: Suppression d'une opération

Le système SHALL permettre à un membre de supprimer définitivement une opération du carnet d'un véhicule de son foyer. Les pièces jointes de l'opération SHALL être supprimées avec elle, y compris lorsque c'est le véhicule entier qui est supprimé.

#### Scenario: Erreur de saisie

- **WHEN** un membre supprime une opération du carnet
- **THEN** elle n'apparaît plus dans le carnet

#### Scenario: Opération inconnue

- **WHEN** un membre supprime une opération inconnue ou d'un autre véhicule
- **THEN** l'API répond `404 Not Found`

#### Scenario: Fichiers supprimés avec l'opération

- **WHEN** un membre supprime une opération qui a deux pièces jointes, ou le véhicule qui la porte
- **THEN** les deux pièces jointes ne sont plus téléchargeables et leurs fichiers ne sont plus conservés

### Requirement: Ajout de pièces jointes à une opération

Le système SHALL permettre à un membre de joindre des fichiers à une opération du carnet d'un véhicule de son foyer : PDF, JPEG, PNG ou WebP, 10 Mo au plus par fichier, 10 fichiers au plus par opération. Le type SHALL être vérifié d'après le contenu du fichier, quelle que soit son extension.

#### Scenario: Facture jointe

- **WHEN** un membre joint `facture-revision.pdf` (300 Ko) à une révision
- **THEN** le fichier est enregistré avec son nom d'origine, son type et sa taille, et figure parmi les pièces jointes de l'opération

#### Scenario: Fichier trop lourd

- **WHEN** un membre joint un fichier de plus de 10 Mo
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request` (ou `413 Payload Too Large`) et rien n'est enregistré

#### Scenario: Type non accepté

- **WHEN** un membre joint un exécutable renommé `facture.pdf`
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Limite atteinte

- **WHEN** un membre joint un onzième fichier à une opération
- **THEN** l'ajout est refusé avec une erreur `409 Conflict`

#### Scenario: Opération d'un autre foyer

- **WHEN** un membre joint un fichier à une opération inconnue ou d'un autre foyer
- **THEN** l'API répond `404 Not Found`

### Requirement: Consultation des pièces jointes

Le système SHALL indiquer, pour chaque opération du carnet, ses pièces jointes (nom, type, taille), et SHALL permettre à un membre du foyer de télécharger le contenu d'une pièce jointe. Le contenu n'est jamais accessible sans être authentifié comme membre du foyer.

#### Scenario: Ouverture d'une facture

- **WHEN** un membre télécharge une pièce jointe d'une opération de son foyer
- **THEN** il reçoit le fichier d'origine avec son type et son nom

#### Scenario: Accès refusé

- **WHEN** un utilisateur qui n'est pas membre du foyer demande une pièce jointe
- **THEN** l'API répond `404 Not Found`

### Requirement: Suppression d'une pièce jointe

Le système SHALL permettre à un membre de supprimer définitivement une pièce jointe d'une opération de son foyer.

#### Scenario: Mauvais fichier

- **WHEN** un membre supprime une pièce jointe
- **THEN** elle n'apparaît plus dans l'opération et son contenu n'est plus téléchargeable

### Requirement: Pièces jointes dans la PWA

La PWA SHALL permettre, depuis une opération du carnet, de joindre un ou plusieurs fichiers à la fois (y compris une photo prise avec l'appareil sur mobile), de voir la liste des pièces jointes, d'en ouvrir une et d'en supprimer une après confirmation.

#### Scenario: Photo du procès-verbal

- **WHEN** un membre, sur mobile, joint une photo prise avec l'appareil à un contrôle technique
- **THEN** la photo apparaît dans les pièces jointes de l'opération et peut être ouverte

### Requirement: Échéance après un contrôle technique ou une révision

Quand un membre ajoute au carnet un contrôle technique ou une révision, le système SHALL fixer la prochaine échéance correspondante du véhicule : soit automatiquement à la date de l'opération plus 2 ans (contrôle technique) ou plus 1 an (révision), soit à une date précise donnée par le membre, postérieure à la date de l'opération. Le système SHALL laisser l'échéance du véhicule inchangée si le carnet contient déjà une opération du même type plus récente. Une date précise n'est acceptée que pour ces deux types.

#### Scenario: Contrôle technique automatique

- **WHEN** un membre ajoute un contrôle technique du 2026-11-18 sans préciser de date
- **THEN** l'opération est enregistrée et le prochain contrôle technique du véhicule est fixé au 2028-11-18

#### Scenario: Révision automatique

- **WHEN** un membre ajoute une révision du 2026-11-18 sans préciser de date
- **THEN** la prochaine révision du véhicule est fixée au 2027-11-18, le contrôle technique restant inchangé

#### Scenario: Date précise

- **WHEN** un membre ajoute un contrôle technique du 2026-11-18 en précisant une contre-visite le 2027-01-15
- **THEN** le prochain contrôle technique du véhicule est fixé au 2027-01-15

#### Scenario: Ancienne opération saisie après coup

- **WHEN** le carnet contient un contrôle technique du 2026-11-18 et qu'un membre ajoute celui du 2024-11-10
- **THEN** l'opération est enregistrée et l'échéance du véhicule reste celle issue du contrôle de 2026

#### Scenario: Date précise invalide

- **WHEN** un membre précise une prochaine échéance antérieure ou égale à la date de l'opération, ou en précise une pour une vidange
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request` et rien n'est enregistré

#### Scenario: Rappels à jour

- **WHEN** un contrôle technique ajouté repousse l'échéance du véhicule
- **THEN** les rappels du foyer tiennent compte de la nouvelle échéance, comme après une modification du véhicule

### Requirement: Précision du type « Autre »

Le système SHALL permettre de préciser une opération de type « Autre » par un libellé facultatif de 40 caractères au plus, affiché dans le carnet à la place de « Autre ». Une précision n'est acceptée que pour le type « Autre ».

#### Scenario: Opération précisée

- **WHEN** un membre ajoute une opération de type « Autre » précisée « Essuie-glaces »
- **THEN** l'opération est enregistrée avec cette précision et le carnet l'affiche « Essuie-glaces »

#### Scenario: Sans précision

- **WHEN** un membre ajoute une opération de type « Autre » sans précision
- **THEN** l'opération est enregistrée et le carnet l'affiche « Autre »

#### Scenario: Précision sur un autre type

- **WHEN** un membre ajoute une révision avec une précision
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

#### Scenario: Précision trop longue

- **WHEN** un membre ajoute une opération « Autre » avec une précision de plus de 40 caractères
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`
