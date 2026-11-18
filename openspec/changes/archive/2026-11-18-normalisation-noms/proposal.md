## Why

Les noms saisis gardent la casse et les espaces tapés : « Lait », « lait » et « LAIT  » coexistent, les comparaisons et l'affichage sont irréguliers. Décision de Dorian (2026-11-18) : stocker la donnée brute normalisée, et laisser la PWA la mettre en forme à l'affichage.

## What Changes

- **Backend** : les noms courts sont enregistrés **sans espaces autour, espaces multiples réduits à un seul, en minuscules** : article de courses, nom de sous-liste, article de l'historique, nom de catégorie, libellé de dépense, nom de foyer.
- **Non concernés** (la casse a un sens) : nom de véhicule (« Clio RS »), plaque, note d'entretien, nom d'affichage, courriel, nom de fichier. Ils restent seulement débarrassés des espaces autour, comme aujourd'hui.
- **Migration** des données existantes (dev et production), sans jamais créer de doublon interdit.
- **PWA** : ces noms sont affichés avec une majuscule initiale (« lait demi-écrémé » → « Lait demi-écrémé »), y compris dans les champs de renommage.

## Capabilities

### Modified Capabilities

- `interface-pwa` : une exigence transverse couvre la forme enregistrée des noms (courses, historique, catégories, dépenses, foyer) et leur mise en forme à l'affichage.

## Impact

- Domaines `shopping`, `budget`, `household` (value objects + une migration Flyway chacun).
- PWA : une fonction de mise en forme appliquée aux affichages concernés.

## Hors périmètre

- Mise en forme plus fine (noms propres, sigles comme « EDF ») : une majuscule initiale seulement.
- Recherche ou tri insensibles aux accents.
