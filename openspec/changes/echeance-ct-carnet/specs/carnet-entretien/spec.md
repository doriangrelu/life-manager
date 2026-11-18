## ADDED Requirements

### Requirement: Échéance après un contrôle technique

Quand un membre ajoute au carnet une opération de type contrôle technique, le système SHALL fixer la prochaine échéance de contrôle technique du véhicule : soit automatiquement à la date du contrôle plus 2 ans, soit à une date précise donnée par le membre, postérieure à la date du contrôle. Le système SHALL laisser l'échéance du véhicule inchangée si le carnet contient déjà un contrôle technique plus récent. Une date précise n'est acceptée que pour un contrôle technique.

#### Scenario: Calcul automatique

- **WHEN** un membre ajoute un contrôle technique du 2026-11-18 sans préciser de date
- **THEN** l'opération est enregistrée et le prochain contrôle technique du véhicule est fixé au 2028-11-18

#### Scenario: Date précise

- **WHEN** un membre ajoute un contrôle technique du 2026-11-18 en précisant une contre-visite le 2027-01-15
- **THEN** le prochain contrôle technique du véhicule est fixé au 2027-01-15

#### Scenario: Ancien contrôle saisi après coup

- **WHEN** le carnet contient un contrôle technique du 2026-11-18 et qu'un membre ajoute celui du 2024-11-10
- **THEN** l'opération est enregistrée et l'échéance du véhicule reste celle issue du contrôle de 2026

#### Scenario: Date précise invalide

- **WHEN** un membre précise un prochain contrôle antérieur ou égal à la date du contrôle, ou en précise un pour une vidange
- **THEN** l'ajout est refusé avec une erreur `400 Bad Request` et rien n'est enregistré

#### Scenario: Rappels à jour

- **WHEN** un contrôle technique ajouté repousse l'échéance du véhicule
- **THEN** les rappels du foyer tiennent compte de la nouvelle échéance, comme après une modification du véhicule
