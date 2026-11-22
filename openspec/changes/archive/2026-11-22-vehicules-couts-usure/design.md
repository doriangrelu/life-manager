## Contexte

Le domaine `vehicle` ignore tout du budget, et le budget sait déjà créer une dépense à partir d'un événement d'un autre domaine (échéance de charge payée, table `charge_expense` qui garantit une seule dépense par échéance). On réutilise ce mécanisme.

## Décisions

### A. Coût d'entretien → budget (v0.15.0)

- **Agrégat `MaintenanceExpense`** dans `vehicle` (table `maintenance_expense`, clé = l'opération, `ON DELETE CASCADE`) : catégorie du budget choisie et payeur. Il n'existe que si l'opération a été envoyée au budget. L'agrégat `MaintenanceEntry` ne change pas.
  - `send(entry, vehicle, categoryId, payer, clock)` : refuse une opération sans coût ou à 0 € (400) ; annonce `MaintenanceExpenseSent` avec le libellé « type · véhicule ».
  - `withdraw(clock)` : appelé à la suppression de l'opération, annonce `MaintenanceExpenseWithdrawn`.
- Le payeur doit être membre du foyer (copie locale `household_member`), sinon 400. La catégorie n'est pas vérifiée par `vehicle` (il ne voit pas le budget) : comme pour les charges, le budget prend « Autre » si elle est inconnue ou archivée.
- **Contrats** : `MaintenanceExpenseSentV1(entryId, householdId, label, amountCents, performedOn, budgetCategoryId, payerId, occurredAt)` et `MaintenanceExpenseWithdrawnV1(entryId, householdId, occurredAt)`, écrits dans la boîte d'envoi.
- **Budget** : `VehicleEventsListener` → `RecordChargeExpense` (l'identifiant de l'opération sert de clé dans `charge_expense`, pour tout le foyer) ; retrait → `WithdrawLinkedExpense` supprime la dépense liée si elle existe encore. Pas de migration budget.
- **Routes** : `POST /maintenance` accepte `budget: {categoryId, payerId}` (absent = pas dans le budget) ; `POST /maintenance/{entryId}/budget` pour une opération déjà saisie (409 si elle y est déjà) ; la liste du carnet renvoie `inBudget`.
- La suppression du véhicule ne touche pas aux dépenses (argent réellement dépensé).

### B. Kilométrage et pièces d'usure (v0.16.0)

- `vehicle` : colonnes `current_mileage_km` et `tire_size` (30 caractères). Le kilométrage d'une opération plus élevé que le kilométrage actuel le remplace.
- **Agrégat `WearPart`** (table `wear_part`) : véhicule, type (`TIRES_FRONT`, `TIRES_REAR`, `PADS_FRONT`, `PADS_REAR`, `DISCS_FRONT`, `DISCS_REAR`, `BATTERY`, `TIMING_BELT`), date et km de pose, libellé facultatif (60), intervalle facultatif en km, date et km de dépose (vides = montée).
  - Une seule pièce montée par type : monter une nouvelle pièce démonte l'ancienne (date et km de la nouvelle pose).
  - Km parcourus = (km de dépose, sinon km actuel du véhicule) − km de pose ; calculés à la lecture, jamais stockés.
- Routes `GET/POST /vehicles/{v}/parts`, `PUT/DELETE /vehicles/{v}/parts/{p}`.
- PWA : état « à prévoir » à partir de 90 % de l'intervalle, « à changer » à 100 %, calculé côté PWA.

## Ergonomie

- Le budget se règle dans le formulaire d'opération, sans écran de plus ; la case n'apparaît qu'avec un coût.
- Les pièces d'usure sont un bloc de la fiche du véhicule, vide au départ, avec un bouton « Suivre une pièce ». « Changer » pré-remplit la date du jour et le kilométrage actuel.
