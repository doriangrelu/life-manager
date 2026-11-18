package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NextDeadlineTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 18);

    @Test
    void a_technical_inspection_lasts_2_years_and_a_service_1_year() {
        assertThat(NextDeadline.after(details(MaintenanceKind.TECHNICAL_INSPECTION, DAY), null))
                .contains(new NextDeadline(MaintenanceKind.TECHNICAL_INSPECTION, LocalDate.of(2028, 11, 18)));
        assertThat(NextDeadline.after(details(MaintenanceKind.SERVICE, DAY), null))
                .contains(new NextDeadline(MaintenanceKind.SERVICE, LocalDate.of(2027, 11, 18)));
    }

    @Test
    void a_29th_of_february_gives_the_28th() {
        assertThat(NextDeadline.after(details(MaintenanceKind.TECHNICAL_INSPECTION, LocalDate.of(2024, 2, 29)), null))
                .map(NextDeadline::dueOn).contains(LocalDate.of(2026, 2, 28));
    }

    @Test
    void a_precise_date_must_follow_the_operation() {
        var recheck = LocalDate.of(2027, 1, 15);

        assertThat(NextDeadline.after(details(MaintenanceKind.TECHNICAL_INSPECTION, DAY), recheck))
                .map(NextDeadline::dueOn).contains(recheck);
        assertThatThrownBy(() -> NextDeadline.after(details(MaintenanceKind.TECHNICAL_INSPECTION, DAY), DAY))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void other_kinds_set_no_deadline() {
        assertThat(NextDeadline.after(details(MaintenanceKind.OIL_CHANGE, DAY), null)).isEmpty();
        assertThatThrownBy(() -> NextDeadline.after(details(MaintenanceKind.OIL_CHANGE, DAY), DAY.plusYears(1)))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_vehicle_deadline_is_replaced_by_kind() {
        var deadlines = new Deadlines(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1));

        assertThat(deadlines.with(new NextDeadline(MaintenanceKind.SERVICE, LocalDate.of(2027, 6, 1))))
                .isEqualTo(new Deadlines(LocalDate.of(2026, 1, 1), LocalDate.of(2027, 6, 1)));
    }

    private static MaintenanceDetails details(MaintenanceKind kind, LocalDate day) {
        return new MaintenanceDetails(day, kind, null, null, null);
    }
}
