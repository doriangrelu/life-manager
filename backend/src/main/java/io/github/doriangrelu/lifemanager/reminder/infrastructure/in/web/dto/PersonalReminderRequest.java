package io.github.doriangrelu.lifemanager.reminder.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.PersonalReminderInput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Corps d'ajout ou de modification d'un rappel saisi.
 *
 * @param recurrence NONE, MONTHLY, QUARTERLY ou YEARLY
 * @param notice     SAME_DAY, ONE_WEEK ou ONE_MONTH
 */
@Serdeable
public record PersonalReminderRequest(@NotBlank @Size(max = 60) String title,
                                      @Nullable @Size(max = 280) String note,
                                      @NotNull LocalDate dueOn,
                                      @NotBlank String recurrence,
                                      @NotBlank String notice) {

    public PersonalReminderInput toInput() {
        return new PersonalReminderInput(title, note, dueOn, recurrence, notice);
    }
}
