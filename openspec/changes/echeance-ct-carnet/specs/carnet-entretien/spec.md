## ADDED Requirements

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
