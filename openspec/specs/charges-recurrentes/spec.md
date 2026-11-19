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

### Requirement: Sens d'un mouvement récurrent

Une charge récurrente SHALL avoir un sens : dépense (par défaut, toutes les charges existantes) ou rentrée. Une échéance payée SHALL produire dans le budget, selon son sens, une dépense ou une rentrée, une seule fois par échéance. Pour un foyer qui suit ses rentrées, la PWA SHALL présenter deux panneaux, « Dépenses fixes » et « Rentrées fixes », avec le même fonctionnement.

#### Scenario: Charge existante

- **WHEN** la nouvelle version démarre avec la charge « Internet »
- **THEN** son sens est « dépense » et son comportement ne change pas

#### Scenario: Rentrée récurrente payée

- **WHEN** l'échéance d'un salaire récurrent de 2 450 € est payée
- **THEN** une rentrée de 2 450 € apparaît dans le mois, et aucune dépense

### Requirement: Échéances à venir d'une période

Le système SHALL fournir, pour une période, les échéances récurrentes encore ouvertes (à venir ou à confirmer) du foyer, avec leur montant prévu et leur sens, pour calculer le prévu du bilan.

#### Scenario: Prévisions de novembre

- **WHEN** au 15 novembre un membre demande les échéances ouvertes de novembre
- **THEN** il obtient le salaire prévu le 28 et les charges restant à payer en novembre, sans celles déjà payées

### Requirement: Mise en avant de l'option dans Récurrents

Pour un foyer qui ne suit pas ses rentrées, l'écran Récurrents SHALL proposer une seule ligne discrète présentant l'option « Rentrées et bilan », avec les mêmes règles que la carte du Budget (activation par le propriétaire, fermeture mémorisée sur l'appareil, disparition une fois l'option active). Avec l'option active, les deux panneaux SHALL s'appeler « Dépenses » et « Rentrées ».

#### Scenario: Ligne de mise en avant

- **WHEN** un foyer sans suivi des rentrées ouvre l'écran Récurrents
- **THEN** une ligne en bas de l'écran propose d'activer « Rentrées et bilan »

### Requirement: Crédit

Le système SHALL permettre de marquer une dépense récurrente comme un crédit, avec la mensualité de son assurance (0 si aucune), comprise dans le montant payé chaque mois. Un crédit est une dépense mensuelle à montant fixe, et chaque échéance produit une seule dépense du budget, égale au montant total.

#### Scenario: Crédit avec assurance

- **WHEN** un membre ajoute le crédit « Prêt immobilier » de 612,40 € par mois, dont 24 € d'assurance
- **THEN** la charge est enregistrée comme un crédit avec 24 € d'assurance, et chaque échéance produit une dépense de 612,40 €

#### Scenario: Crédit incohérent

- **WHEN** un membre ajoute un crédit à montant variable, non mensuel, en rentrée, ou dont l'assurance dépasse le montant total
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request`

### Requirement: Dernière échéance

Le système SHALL accepter, pour une charge récurrente, une dernière échéance facultative, jamais antérieure à la première. Aucune échéance n'est prévue ni annoncée après elle, et la charge passe à l'état terminé une fois la dernière échéance arrivée.

#### Scenario: Fin du crédit

- **WHEN** la dernière échéance d'un crédit arrive
- **THEN** elle produit sa dépense, aucune échéance suivante n'est prévue, et le crédit n'est plus actif

#### Scenario: Prévisions bornées

- **WHEN** un membre demande les échéances attendues d'une période qui dépasse la dernière échéance d'un crédit
- **THEN** il n'obtient aucune échéance de ce crédit après cette date

#### Scenario: Dates incohérentes

- **WHEN** un membre saisit une dernière échéance antérieure à la première
- **THEN** la saisie est refusée avec une erreur `400 Bad Request`

### Requirement: Crédits dans la PWA

La PWA SHALL proposer, à l'ajout d'une dépense récurrente, le choix entre « Dépense » et « Crédit ». Pour un crédit, elle demande la mensualité du prêt, la mensualité de l'assurance (facultative) et la date de la dernière mensualité (facultative), puis affiche sur sa ligne le total mensuel, le détail prêt et assurance, et, avec une date de fin, le nombre de mensualités restantes et le mois de fin.

#### Scenario: Saisie d'un crédit

- **WHEN** un membre choisit « Crédit » et saisit 588,40 € de prêt, 24 € d'assurance et une dernière mensualité en mars 2049
- **THEN** le crédit apparaît dans Récurrents à 612,40 € par mois, avec le détail « prêt 588,40 € + assurance 24,00 € », le nombre de mensualités restantes et « fin en mars 2049 »

#### Scenario: Crédit terminé

- **WHEN** la dernière mensualité d'un crédit est passée
- **THEN** le crédit apparaît parmi les charges terminées
