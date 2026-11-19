## Contexte

Faits établis dans le code (2026-11-19) :

- Une charge récurrente (`bills`) a un montant, fixe ou variable, une fréquence, une première échéance qui **ne peut pas être dans le passé**, une catégorie de budget, un payeur. Elle n'a pas de fin : on la résilie à la main.
- Ses échéances payées deviennent des dépenses du Budget (`ChargePaidV1`), et ses échéances à venir alimentent le prévu et le reste à vivre (`/charges/forecast`).
- Aucune notion de crédit n'existe.

## Choix proposés

- **Conditions du prêt portées par la charge** : value object `LoanTerms` (capital en centimes, taux annuel en points de base, 3,45 % = 345, nombre de mensualités, date de la première mensualité du prêt, assurance mensuelle en centimes). Facultatif : une charge sans `LoanTerms` reste une charge ordinaire. Jamais de nombre à virgule stocké.
- **La charge d'un crédit** est mensuelle, à montant fixe (mensualité + assurance). Son calendrier commence à la prochaine mensualité à venir : la règle « pas de première échéance dans le passé » reste vraie, et rien n'est créé rétroactivement.
- **Échéancier calculé, jamais stocké** : amortissement à mensualités constantes. Intérêts du mois = capital restant × taux mensuel, arrondis au centime ; la dernière mensualité solde le capital restant (elle absorbe les écarts d'arrondi). Le calcul se fait en entiers (centimes et points de base).
- **Mensualité saisie** : si l'utilisateur la corrige, c'est elle qui est payée chaque mois ; l'échéancier l'utilise et ajuste la dernière mensualité.
- **Fin** : la tâche quotidienne qui avance les échéances n'en crée plus après la dernière et marque la charge terminée.
- API : `LoanTerms` facultatif dans l'ajout et la modification d'une charge ; `GET /charges/{id}/loan-schedule` renvoie l'échéancier et le résumé (capital restant dû, mensualités payées, date de fin).

## Questions pour Dorian

1. **Mensualité** : calculée puis modifiable avant d'enregistrer (ma proposition), ou toujours recopiée depuis l'offre de prêt sans calcul ?
2. **Crédit déjà en cours** : d'accord pour ne rien créer dans le passé (les anciennes mensualités ne servent qu'au capital restant dû) ?
3. **Assurance emprunteur** : un montant mensuel fixe ajouté à la mensualité, ça suffit ?
4. **Cas particuliers** : as-tu un prêt avec différé, taux variable, paliers, ou un remboursement anticipé prévu ? Sinon je les laisse hors de cette version.
5. **Affichage** : le capital restant dû sur la ligne du crédit et l'échéancier dans une fenêtre, comme sur la maquette du 19/11 ?
