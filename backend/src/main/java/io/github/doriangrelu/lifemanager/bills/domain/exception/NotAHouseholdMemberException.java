package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Payeur ou bénéficiaire étranger au foyer. */
public class NotAHouseholdMemberException extends InvalidValueException {

    public NotAHouseholdMemberException() {
        super("Le payeur et le bénéficiaire doivent être membres du foyer");
    }
}
