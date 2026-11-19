## Pourquoi

Aujourd'hui, une saisie invalide n'est signalée qu'au moment d'envoyer le formulaire, par un seul message en bas, sans dire quel champ est en cause. Et certains champs ne s'expliquent pas d'eux-mêmes (montant fixe ou variable, budget prévu, assurance d'un crédit…).

Demande de Dorian (2026-11-19) : des contrôles **dynamiques**, avec le champ en rouge et le motif écrit dessous, et une icône « i » avec une bulle d'explication sur certains champs.

## Ce qui change (proposition, à valider par Dorian)

1. **Contrôle en direct, champ par champ**
   - Un champ invalide passe en rouge (bordure et libellé), avec le motif écrit juste dessous : « Montant invalide (exemple : 12,50) », « La dernière mensualité ne peut pas précéder la prochaine »…
   - Le contrôle se déclenche **quand on quitte le champ**, puis se met à jour à chaque frappe : on n'est pas grondé pendant qu'on tape le premier chiffre.
   - À l'envoi, tous les champs sont contrôlés et le premier champ en erreur reçoit le curseur.
   - Les erreurs renvoyées par le serveur (nom déjà pris, par exemple) s'affichent sous le champ concerné quand on sait lequel, sinon en bas comme aujourd'hui.
   - Lecteurs d'écran : le champ est annoncé invalide et son message lui est rattaché.
2. **Icône « i » et bulle d'explication**
   - Une petite icône à côté du libellé ; la bulle s'ouvre au survol et au clavier sur ordinateur, **au toucher sur téléphone**, et se referme en touchant ailleurs.
   - Champs proposés : « Fixe / Variable » d'un récurrent, « Dernière mensualité » et « Assurance » d'un crédit, « Tous les mois / Ce mois seulement » du budget prévu, « Pour » d'une dépense (bénéficiaire), « Prochaine échéance » d'un véhicule (automatique ou date précise), option « Rentrées et bilan ».
3. **Formulaires concernés** : dépense, rentrée, récurrent et crédit, confirmation d'un montant, budget prévu, véhicule, opération d'entretien, catégories, listes de courses, création d'un foyer et code d'invitation, renommages.

## Comment

- Deux composants communs : `Field` (libellé, icône « i », champ, message) et `InfoTip` (la bulle), plus des règles de contrôle réutilisables (`lib/validation.ts` : montant, entier, date après une autre, texte requis, longueur).
- Les formulaires existants les adoptent un par un, sans changer ce qu'ils envoient.

## Impact

- PWA seulement : aucun changement d'API ni de base. Les contrôles du serveur restent la référence.
- Livrable en deux temps si tu préfères voir vite : d'abord les formulaires d'argent (dépense, rentrée, récurrent, crédit, budget prévu), puis les autres.
- Version **v0.8.1** (ou v0.8.1 puis v0.8.2).

## Questions pour Dorian

1. Le déclenchement « quand on quitte le champ, puis en direct » te convient, ou tu veux le rouge dès la première frappe ?
2. La liste des champs avec une icône « i » te va ? D'autres champs à expliquer ?
3. Une seule livraison, ou les formulaires d'argent d'abord ?
