## Pourquoi

Un crédit pèse chaque mois sur le budget et il a une fin. Aujourd'hui on peut seulement le saisir comme une dépense récurrente ordinaire, sans distinguer le prêt de son assurance et sans date de fin.

C'est le lot **4b2 « Crédits »** (v0.8.0), dernier morceau du lot 4.

## Ce qui change

Version volontairement simple (choix de Dorian le 2026-11-19) : **pas de capital, pas de taux, pas d'échéancier d'amortissement**. On saisit ce qu'on paie chaque mois.

- Dans Récurrents, panneau Dépenses, « Ajouter » propose **Dépense** ou **Crédit**.
- Un crédit se saisit avec : son nom, la **mensualité du prêt**, la **mensualité de l'assurance** (facultative), la prochaine échéance, la **date de la dernière mensualité** (facultative), la catégorie et qui paie.
- Chaque mois, **une seule dépense** arrive dans le Budget, égale à prêt + assurance. Le détail prêt / assurance reste visible dans Récurrents.
- Avec une date de fin : la ligne du crédit affiche « encore N mensualités, fin en mars 2049 », et le crédit **s'arrête seul** après la dernière mensualité.
- Sans date de fin : le crédit se comporte comme une dépense récurrente normale, à résilier à la main.
- Un crédit est toujours mensuel, à montant fixe. Il compte déjà dans le prévu du Bilan et dans le reste à vivre, comme tout récurrent.

## Hors périmètre

Capital restant dû, taux, tableau d'amortissement, remboursement anticipé, renégociation. À reprendre plus tard si le besoin apparaît.

## Impact

- Backend `bills` : assurance du crédit et dernière échéance sur la charge récurrente (migration `bills/V4`) ; le calendrier s'arrête après la dernière échéance et la charge passe alors à « terminée ». Aucun autre domaine touché.
- PWA : Récurrents (choix Dépense / Crédit, formulaire du crédit, ligne et détail d'un crédit).
- Version **v0.8.0**.
- Specs : `charges-recurrentes` complété.
