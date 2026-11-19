## 1. Chasse aux bugs

- [x] 1.1 [frontend] Tour au navigateur de chaque écran (mobile et bureau, clair et sombre) ; noter les anomalies dans ce fichier (section 4)
- [x] 1.2 [backend] Revue des cas limites : dates (mois, années, 29 février, UTC/France), montants, listes vides (`budget`, `bills`, `reminder`)
- [x] 1.3 [backend] Revue des droits et des foyers disparus (ancien membre, foyer supprimé, compte supprimé) dans tous les domaines
- [x] 1.4 [frontend] Revue des chargements, doubles envois, messages d'erreur, retour arrière, mise à jour de la PWA
- [x] 1.5 [backend+frontend] Chaque bug : test qui échoue, correction, commit

## 2. Améliorations du code (si utile)

- [x] 2.1 [frontend] (non fait, volontairement : les corrections ont touché ces écrans, un découpage en plus aurait mêlé refonte et corrections ; à reprendre si un lot les touche) Découper `ChargesView`, `BudgetView`, `ShoppingView` et `ChargeDetail` s'ils mélangent plusieurs rôles ; factoriser ce qui est commun aux formulaires
- [x] 2.2 [backend] (relevé, non fait dans ce patch : N+1 de `ForecastChargesUseCase`, `OpenOccurrences.view`, `ChargeScheduler.advance` et annonce quotidienne de `ChargeDeadlinesChangedV1` ; sans effet visible au volume actuel) Requêtes dans des boucles, doublons entre domaines (sans casser la séparation des domaines)
- [x] 2.3 [frontend] Appels API en double au chargement ; arrêt du rafraîchissement des courses quand l'écran est quitté
- [x] 2.4 [infra/ci] `native-smoke.sh` indépendant de l'état du compte de dev

## 3. Livraison

- [x] 3.1 Suite complète (`./mvnw -B -q verify`, `bun run check`, `bun run test`), parcours natif
- [x] 3.2 Livraison v0.13.1 (`release-chain.sh`), vérification en prod
- [x] 3.3 Message de recette à Dorian : liste des bugs corrigés et des améliorations ; archive du change

## 4. Anomalies trouvées

PWA (corrigé, 0bc211e) : sélection de l'historique des courses effacée toutes les 5 s ; doubles envois (« Remettre en liste », « C'est fait », « Sauter ») ; erreurs du serveur cachées derrière les fenêtres (véhicule, entretien, budget prévu) et entretien en double ; erreur de rafraîchissement des courses jamais effacée ; ancienne sous-liste ramenée par un rafraîchissement ; réponses périmées du Bilan ; mois courant figé dans le Budget ; mensualités restantes fausses en fin de mois ; fiche récurrent non recréée ; promesse rejetée dans App.

Backend (revue du 19/11) : 1 à 5 corrigés avec un test (3618104, 9ed9d79) ; 6 et la course rare laissés (voir ci-dessous).
1. Charge impossible à modifier ou résilier si une facture est jointe à une échéance à venir (clé étrangère `invoice` → 500) — sûr.
2. Changer la dernière mensualité d'une charge commencée → 400 « date passée » (`ChargeSchedule.equals` compare `lastDueOn`) — sûr.
3. Modifier un rappel récurrent recale son ancre (rappel du 31 devient du 28) — sûr.
4. Rentrée récurrente ou échéance de rappel livrée après la suppression du foyer → données orphelines — probable.
5. Budget prévu d'une catégorie archivée encore compté dans l'onglet « Budgets » du Bilan — probable.
6. Liens `charge_expense` / `charge_income` orphelins après suppression du foyer (identifiants seuls) — faible ; la correction propre demande un changement de schéma → à proposer à Dorian, sinon laissé.
Course rare : « Payer maintenant » au moment exact du passage de 6 h.
Améliorations repérées : N+1 dans `ForecastChargesUseCase`, `OpenOccurrences.view`, `ChargeScheduler.advance` ; `ChargeDeadlinesChangedV1` publié chaque jour sans changement ; doublon `ChargeExpenseDetails` / `ChargeIncomeDetails`.
