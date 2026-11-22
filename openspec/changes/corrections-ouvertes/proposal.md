## Pourquoi

Trois points laissés ouverts par la stabilisation (v0.13.1) ; Dorian les juge importants et a demandé de les corriger le 22/11 :

1. **Liens orphelins** : quand un membre supprime la dépense (ou la rentrée) produite par une échéance récurrente, le lien « échéance → dépense » reste, volontairement, pour qu'elle ne revienne pas. Mais l'effacement d'un foyer ne retrouvait ce lien qu'à travers la dépense, déjà disparue : il restait en base pour toujours.
2. **Requêtes en cascade (N+1) dans les Récurrents** : la liste lisait le nombre de factures échéance par échéance, et les prévisions du Bilan relisaient les échéances charge par charge.
3. **Liens vers les CGU et la confidentialité** absents de la page de connexion Keycloak.

## Ce qui change

- `budget` : les tables `charge_expense` et `charge_income` reçoivent une colonne `household_id` (migration `budget/V7`, remplie depuis les dépenses et rentrées encore présentes). L'effacement d'un foyer supprime ses liens par cette colonne. Les très anciens liens dont la dépense avait déjà disparu restent sans foyer : ce ne sont que des identifiants d'échéance, sans donnée personnelle.
- `bills` : le nombre de factures de toutes les échéances affichées est lu en une requête ; les prévisions lisent toutes les échéances du foyer en une requête. Aucun changement visible. Non traité : la tâche quotidienne `ChargeScheduler.advance`, qui écrit charge par charge de toute façon.
- Thème Keycloak `life-manager` (page de connexion) : pied de page avec les liens « Conditions d'utilisation » et « Confidentialité » vers la PWA, via `footer.ftl` (point d'extension prévu par Keycloak : seul modèle surchargé). Adresse de la PWA réglable (`lmPwaUrl`, variable `LM_PWA_URL` en dev).

## Impact

- Version v0.13.2. Migration `budget/V7` automatique. Le thème doit être recopié sur le VPS avec un redémarrage de Keycloak (coupure d'environ 45 s de tous les sites ; accepté par Dorian en demandant ces corrections).
- Aucune exigence fonctionnelle nouvelle : pas de delta de spec.
