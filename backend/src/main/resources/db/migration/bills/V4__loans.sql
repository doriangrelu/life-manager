-- Crédits (lot 4b2) : une charge peut avoir une dernière échéance, et être un crédit.
-- loan_insurance_cents absent = ce n'est pas un crédit ; 0 = crédit sans assurance.
ALTER TABLE recurring_charge
    ADD COLUMN last_due_on          DATE,
    ADD COLUMN loan_insurance_cents BIGINT CHECK (loan_insurance_cents >= 0);
