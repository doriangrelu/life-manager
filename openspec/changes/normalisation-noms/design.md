## Context

Les value objects de texte passent déjà par `Require.notBlank`, qui retire les espaces autour. Les index d'unicité existants comparent déjà en minuscules : `category (household_id, lower(name))`, `shopping_list (household_id, lower(name))`, `purchase_history (list_id, lower(label))`.

## Decisions

1. **Normalisation dans les value objects** (`CategoryName`, `ExpenseLabel`, `HouseholdName`, `ItemLabel`, `ListName`) via un utilitaire du noyau `kernel.domain.text.Names.normalized` : retrait des espaces autour, espaces intérieurs réduits à un, minuscules (`Locale.ROOT`, sans perte des accents). Un nom ne peut donc pas exister non normalisé, quel que soit le chemin (API, valeurs par défaut, relecture en base). La longueur maximale s'applique après normalisation.
2. **Une migration par schéma** (`budget`, `household`, `shopping`) : `lower(regexp_replace(btrim(x), '\s+', ' ', 'g'))`. Pour les colonnes uniques, une ligne n'est mise à jour que si la valeur normalisée n'est pas déjà prise dans le même périmètre : la migration ne peut pas échouer en production. Les rares lignes laissées telles quelles sont de toute façon normalisées en mémoire à la relecture.
3. **Mise en forme à l'affichage** dans la PWA : `displayName(text)` (`lib/text.ts`), majuscule initiale selon la locale française. Les comparaisons existantes (« Autre », « Courses », couleurs des catégories) sont déjà insensibles à la casse.
4. **Aucun événement de contrat modifié** : les noms concernés ne sortent pas de leur domaine.

## Risks / Trade-offs

- Les sigles saisis (« EDF ») s'affichent « Edf » : accepté, hors périmètre.
- Les réponses de l'API renvoient désormais les noms en minuscules : seule la PWA les consomme.
