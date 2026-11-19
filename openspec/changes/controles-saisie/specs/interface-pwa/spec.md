## ADDED Requirements

### Requirement: Contrôles de saisie en direct

La PWA SHALL signaler une saisie invalide sur le champ concerné : le champ passe en rouge et le motif s'affiche juste dessous. Le contrôle d'un champ se montre une fois ce champ quitté, puis suit la saisie ; après une tentative d'envoi, toutes les erreurs du formulaire sont montrées et le curseur va dans le premier champ en erreur. Le champ en erreur est annoncé comme invalide aux lecteurs d'écran, avec son message.

#### Scenario: Montant invalide

- **WHEN** un membre tape « douze » dans le montant d'une dépense puis quitte le champ
- **THEN** le champ passe en rouge et « Montant invalide (exemple : 12,50) » s'affiche dessous

#### Scenario: Correction en direct

- **WHEN** il corrige le montant en « 12,50 »
- **THEN** le rouge et le message disparaissent aussitôt

#### Scenario: Envoi d'un formulaire incomplet

- **WHEN** un membre envoie un récurrent sans nom, sans montant et sans catégorie
- **THEN** rien n'est envoyé, les trois erreurs s'affichent sous leurs champs et le curseur va dans le nom

#### Scenario: Pas d'erreur prématurée

- **WHEN** un membre ouvre un formulaire vide
- **THEN** aucun champ n'est en rouge tant qu'il n'a pas été quitté ou que le formulaire n'a pas été envoyé

### Requirement: Explication d'un champ

La PWA SHALL proposer, sur les champs qui ne s'expliquent pas d'eux-mêmes, une icône « i » qui affiche une courte explication dans une bulle : au survol et au clavier sur ordinateur, au toucher sur téléphone. La bulle se referme en quittant l'icône, avec Échap, ou en touchant ailleurs.

#### Scenario: Bulle au toucher

- **WHEN** un membre touche l'icône « i » à côté de « Fixe / Variable »
- **THEN** une bulle explique la différence entre un montant fixe et un montant variable, et un toucher ailleurs la referme
