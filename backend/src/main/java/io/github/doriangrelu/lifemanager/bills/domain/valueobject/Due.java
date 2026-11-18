package io.github.doriangrelu.lifemanager.bills.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.LocalDate;

/** Ce qui est attendu pour une échéance : sa date et le montant prévu. */
public record Due(LocalDate dueOn, Amount expected) {

    public Due {
        Require.notNull(dueOn, "La date d'échéance");
        Require.notNull(expected, "Le montant prévu");
    }
}
