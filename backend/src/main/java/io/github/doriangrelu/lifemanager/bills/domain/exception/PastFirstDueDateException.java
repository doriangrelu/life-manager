package io.github.doriangrelu.lifemanager.bills.domain.exception;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;

/** Une charge commence aujourd'hui ou plus tard : on ne recrée pas les échéances passées. */
public class PastFirstDueDateException extends InvalidValueException {

    public PastFirstDueDateException() {
        super("La première échéance ne peut pas être passée");
    }
}
