## 1. Chasse aux bugs

- [ ] 1.1 [frontend] Tour au navigateur de chaque écran (mobile et bureau, clair et sombre) ; noter les anomalies dans ce fichier (section 4)
- [ ] 1.2 [backend] Revue des cas limites : dates (mois, années, 29 février, UTC/France), montants, listes vides (`budget`, `bills`, `reminder`)
- [ ] 1.3 [backend] Revue des droits et des foyers disparus (ancien membre, foyer supprimé, compte supprimé) dans tous les domaines
- [ ] 1.4 [frontend] Revue des chargements, doubles envois, messages d'erreur, retour arrière, mise à jour de la PWA
- [ ] 1.5 [backend+frontend] Chaque bug : test qui échoue, correction, commit

## 2. Améliorations du code (si utile)

- [ ] 2.1 [frontend] Découper `ChargesView`, `BudgetView`, `ShoppingView` et `ChargeDetail` s'ils mélangent plusieurs rôles ; factoriser ce qui est commun aux formulaires
- [ ] 2.2 [backend] Requêtes dans des boucles, doublons entre domaines (sans casser la séparation des domaines)
- [ ] 2.3 [frontend] Appels API en double au chargement ; arrêt du rafraîchissement des courses quand l'écran est quitté
- [ ] 2.4 [infra/ci] `native-smoke.sh` indépendant de l'état du compte de dev

## 3. Livraison

- [ ] 3.1 Suite complète (`./mvnw -B -q verify`, `bun run check`, `bun run test`), parcours natif
- [ ] 3.2 Livraison v0.13.1 (`release-chain.sh`), vérification en prod
- [ ] 3.3 Message de recette à Dorian : liste des bugs corrigés et des améliorations ; archive du change

## 4. Anomalies trouvées

(à remplir pendant le lot)
