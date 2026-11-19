## Pourquoi

Un crédit pèse chaque mois sur le budget, mais il a une fin et un capital restant dû qu'aucun écran ne montre. Aujourd'hui on peut seulement le saisir comme une dépense récurrente ordinaire, sans échéancier et sans date de fin.

C'est le lot **4b2 « Crédits »** (v0.8.0), dernier morceau du lot 4.

## Ce qui change (proposition, à valider par Dorian)

Un crédit est une **dépense récurrente d'un type particulier**, dans l'écran Récurrents (choix de Dorian). Tout le reste fonctionne déjà pour lui : la mensualité arrive seule dans le Budget, compte dans le reste à vivre et dans le prévu du Bilan.

1. **Saisie** : dans Récurrents, « Ajouter » propose **Dépense** ou **Crédit**. Pour un crédit : nom, capital emprunté, taux annuel, durée en mois, date de la première mensualité (elle peut être passée : la plupart des crédits sont déjà en cours), assurance mensuelle facultative, catégorie, qui paie.
2. **Mensualité** : calculée à partir du capital, du taux et de la durée, puis **affichée et modifiable** avant d'enregistrer, car l'offre de prêt fait foi (arrondis de la banque).
3. **Un crédit déjà en cours ne crée rien dans le passé** : les mensualités d'avant aujourd'hui servent seulement à calculer le capital restant dû. Les dépenses commencent à la prochaine échéance.
4. **Suivi** : sur la ligne du crédit, le capital restant dû, l'avancement (« 42 échéances sur 300 ») et la date de fin. Un bouton ouvre l'**échéancier** : pour chaque mensualité, la part d'intérêts, la part de capital, l'assurance et le capital restant.
5. **Fin automatique** : après la dernière mensualité, le crédit s'arrête seul et reste visible comme « terminé ».

## Hors périmètre de cette version

Remboursement anticipé, renégociation du taux, différé, prêts à taux variable ou à paliers. Si l'un d'eux te concerne, dis-le : cela change le modèle.

## Impact

- Backend `bills` : conditions du prêt attachées à la charge (migration `bills/V4`), calcul de l'échéancier, arrêt après la dernière échéance. Aucun autre domaine touché.
- PWA : Récurrents (type Crédit, ligne de suivi, fenêtre de l'échéancier).
- Version **v0.8.0**.

Les questions sont dans `design.md`. **Rien ne sera implémenté avant les réponses de Dorian.**
