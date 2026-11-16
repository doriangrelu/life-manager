package io.github.doriangrelu.lifemanager.budget.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Le payeur ou le bénéficiaire indiqué n'est pas membre du foyer. */
public class NotAHouseholdMemberException extends InvalidValueException {

    public NotAHouseholdMemberException() {
        super("Le payeur et le bénéficiaire doivent être membres du foyer");
    }
}
