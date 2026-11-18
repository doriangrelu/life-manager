# charges-recurrentes Specification

## Purpose
Suivre ce qui revient régulièrement dans un foyer (internet, électricité, assurances, abonnements…) : chaque échéance devient seule une dépense du budget (montant fixe) ou attend le montant de la facture (montant variable), avec rappel et factures jointes.

## Requirements

### Requirement: Charges récurrentes

Le système SHALL permettre à un membre d'enregistrer une charge récurrente de son foyer : nom (1 à 60 caractères), montant prévu en centimes (0 à 100 000 €), montant fixe ou variable, fréquence (mensuelle, bimestrielle, trimestrielle, annuelle), première échéance, catégorie du budget, payeur et bénéficiaire (le foyer ou un membre).

#### Scenario: Abonnement internet

- **WHEN** un membre enregistre « Internet », 39,99 € fixe, mensuel, première échéance le 2026-12-05
- **THEN** la charge est enregistrée et sa prochaine échéance est le 2026-12-05

#### Scenario: Jour absent du mois

- **WHEN** une charge mensuelle a sa première échéance le 2027-01-31
- **THEN** l'échéance de février tombe le 2027-02-28

### Requirement: Modification et résiliation d'une charge

Le système SHALL permettre à un membre de modifier ou de résilier une charge de son foyer. Une modification SHALL valoir pour les échéances à venir non payées ; une charge résiliée SHALL ne plus créer d'échéance.

#### Scenario: Nouveau tarif

- **WHEN** un membre passe « Internet » de 39,99 € à 42,99 €
- **THEN** les échéances à venir sont prévues à 42,99 €, les échéances déjà payées restent à 39,99 €

#### Scenario: Résiliation

- **WHEN** un membre résilie une charge
- **THEN** aucune nouvelle échéance n'est créée et les échéances déjà payées restent consultables

#### Scenario: Cloisonnement

- **WHEN** un utilisateur qui n'est pas membre du foyer consulte ou modifie une charge
- **THEN** l'API répond `404 Not Found`

### Requirement: Échéances et paiement

Le système SHALL créer les échéances de chaque charge active à l'avance. À la date d'échéance, une échéance de charge à montant fixe SHALL être marquée payée au montant prévu ; une échéance de charge à montant variable SHALL passer « à confirmer ». Un membre SHALL pouvoir confirmer une échéance avec son montant réel, ou la sauter. Une échéance payée ou sautée SHALL ne plus changer. Un traitement relancé ou rattrapé après un arrêt SHALL ne jamais créer deux fois la même échéance.

#### Scenario: Montant fixe

- **WHEN** le 2026-12-05 arrive pour la charge « Internet » (39,99 € fixe)
- **THEN** l'échéance est payée à 39,99 €

#### Scenario: Montant variable

- **WHEN** le 2026-12-10 arrive pour la charge « Électricité » (prévu 85 €, variable)
- **THEN** l'échéance est « à confirmer » ; quand un membre la confirme à 92,40 €, elle est payée à 92,40 €

#### Scenario: Échéance sautée

- **WHEN** un membre saute l'échéance de décembre d'une charge
- **THEN** elle est marquée sautée et aucune dépense n'est créée

#### Scenario: Rattrapage

- **WHEN** l'application redémarre après trois jours d'arrêt pendant lesquels une échéance fixe est arrivée
- **THEN** l'échéance est payée une seule fois

### Requirement: Factures jointes

Le système SHALL permettre de joindre des fichiers à une échéance (PDF, JPEG, PNG ou WebP, 10 Mo au plus, 10 au plus par échéance, type vérifié d'après le contenu), de les consulter, télécharger et supprimer, dans les mêmes conditions que les pièces jointes du carnet d'entretien.

#### Scenario: Facture du mois

- **WHEN** un membre joint `facture-decembre.pdf` à l'échéance de décembre de « Électricité »
- **THEN** la facture apparaît dans l'échéance et peut être téléchargée par les membres du foyer

### Requirement: Charges dans la PWA

La PWA SHALL proposer un écran des charges : total mensuel estimé (charges actives ramenées au mois), échéances à confirmer en premier, puis les charges triées par prochaine échéance. Elle SHALL permettre de saisir, modifier et résilier une charge, de confirmer ou sauter une échéance et de joindre des factures.

#### Scenario: Échéance à confirmer

- **WHEN** l'échéance d'« Électricité » est à confirmer
- **THEN** elle apparaît en tête de l'écran des charges avec un champ pour le montant réel
