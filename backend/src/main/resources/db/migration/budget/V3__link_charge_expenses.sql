-- Échéance de charge récurrente → dépense qu'elle a produite (domaine des charges, sans clé étrangère).
-- Une échéance ne produit qu'une dépense ; la ligne reste si la dépense est supprimée (elle ne revient pas).
CREATE TABLE charge_expense
(
    occurrence_id UUID PRIMARY KEY,
    expense_id    UUID NOT NULL
);
