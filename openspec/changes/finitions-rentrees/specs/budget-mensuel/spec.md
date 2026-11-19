## ADDED Requirements

### Requirement: Opérations du mois en une liste

Pour un foyer qui suit ses rentrées, la vue mensuelle SHALL présenter dépenses et rentrées dans une seule liste « Opérations du mois », triée par date (la plus récente en premier), avec des filtres Tout, Dépenses et Rentrées. Une rentrée SHALL se distinguer d'une dépense par une pastille et un montant verts précédés de « + ». Sans suivi des rentrées, la liste SHALL rester celle des dépenses du mois.

#### Scenario: Liste mêlée

- **WHEN** un foyer qui suit ses rentrées a un salaire le 28 et des courses le 27
- **THEN** la liste montre d'abord le salaire en vert « + », puis les courses

#### Scenario: Filtre

- **WHEN** le membre choisit le filtre Rentrées
- **THEN** seules les rentrées du mois restent affichées

### Requirement: Mise en avant de l'option dans le Budget

Pour un foyer qui ne suit pas ses rentrées, l'écran Budget SHALL proposer une carte discrète présentant l'option « Rentrées et bilan ». Le propriétaire SHALL pouvoir l'activer depuis cette carte ; les autres membres SHALL être invités à le demander au propriétaire. La carte SHALL pouvoir être fermée, définitivement sur l'appareil, et SHALL disparaître une fois l'option active.

#### Scenario: Activation depuis le Budget

- **WHEN** le propriétaire touche « Activer » sur la carte
- **THEN** l'option est activée, la carte disparaît et l'écran Bilan apparaît

#### Scenario: Carte fermée

- **WHEN** un membre ferme la carte
- **THEN** elle ne réapparaît plus sur cet appareil
