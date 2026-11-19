## Contexte

- Une charge récurrente (`bills`) a un montant fixe ou variable, une fréquence, une première échéance, une catégorie de budget, un payeur. Elle n'a pas de fin : on la résilie à la main.
- Ses échéances payées deviennent des dépenses du Budget (`ChargePaidV1`) ; ses échéances à venir alimentent le prévu et le reste à vivre (`/charges/forecast`).

## Réponses de Dorian (2026-11-19)

- Pas d'amortissement dans cette version : il donne la mensualité du prêt et celle de l'assurance.
- Date de la dernière mensualité : oui, facultative.
- Une seule dépense par mois (prêt + assurance).

## Décisions

- **Crédit = charge récurrente avec un `Loan`** : value object facultatif dans `ChargeDetails`, qui porte la mensualité d'assurance (0 si aucune). Le montant de la charge reste le total payé chaque mois ; la part du prêt est le total moins l'assurance.
  - Invariants : un crédit est une dépense, à montant fixe ; l'assurance ne dépasse pas le total. Le calendrier d'un crédit est mensuel (vérifié à la saisie).
- **Dernière échéance dans le calendrier** : `ChargeSchedule.lastDueOn`, facultative, jamais avant la première échéance. Les dates calculées s'arrêtent à elle ; les prévisions (`/charges/forecast`) aussi. Elle est acceptée pour toute charge par l'API, mais la PWA ne la propose que pour un crédit.
- **Fin automatique** : quand le planificateur ne peut plus prévoir d'échéance (la suivante dépasserait la dernière), la charge passe inactive, comme une charge résiliée. Idempotent.
- **API** : `lastDueOn` et `loan: { insuranceCents }` facultatifs dans l'ajout et la modification d'une charge, renvoyés dans sa lecture. Les charges existantes ne changent pas.
- **PWA** : le nombre de mensualités restantes est calculé à l'affichage (de la prochaine échéance à la dernière).
- Migration `bills/V4` : colonnes `last_due_on` et `loan_insurance_cents` (absente = ce n'est pas un crédit), toutes deux facultatives.
