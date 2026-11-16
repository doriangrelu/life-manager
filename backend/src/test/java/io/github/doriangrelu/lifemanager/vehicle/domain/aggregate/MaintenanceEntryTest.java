package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.vehicle.domain.exception.FutureMaintenanceDateException;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceKind;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenanceEntryTest {

    // 23h30 à Paris le 16 novembre : déjà le 17 en UTC, mais toujours le 16 pour l'utilisateur.
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-16T22:30:00Z"), ZoneId.of("Europe/Paris"));
    private static final VehicleId VEHICLE = VehicleId.newId();
    private static final MemberId MEMBER = new MemberId(UUID.randomUUID());

    @Test
    void an_operation_of_today_is_recorded() {
        var entry = MaintenanceEntry.record(MaintenanceEntryId.newId(), VEHICLE, on(LocalDate.of(2026, 11, 16)),
                MEMBER, CLOCK);

        assertThat(entry.belongsTo(VEHICLE)).isTrue();
        assertThat(entry.recordedAt()).isEqualTo(CLOCK.instant());
    }

    @Test
    void an_operation_in_the_future_is_refused() {
        assertThatThrownBy(() -> MaintenanceEntry.record(MaintenanceEntryId.newId(), VEHICLE,
                on(LocalDate.of(2026, 11, 17)), MEMBER, CLOCK))
                .isInstanceOf(FutureMaintenanceDateException.class);
    }

    private static MaintenanceDetails on(LocalDate day) {
        return new MaintenanceDetails(day, MaintenanceKind.SERVICE, null, null, null);
    }
}
