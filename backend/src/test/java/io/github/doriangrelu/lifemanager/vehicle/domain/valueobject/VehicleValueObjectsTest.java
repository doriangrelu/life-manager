package io.github.doriangrelu.lifemanager.vehicle.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VehicleValueObjectsTest {

    @Test
    void a_vehicle_name_has_1_to_40_characters() {
        assertThat(new VehicleName("  Clio ").value()).isEqualTo("Clio");
        assertThat(new VehicleName("a".repeat(40)).value()).hasSize(40);
        assertThatThrownBy(() -> new VehicleName(" ")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new VehicleName("a".repeat(41))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_license_plate_is_optional_short_and_upper_case() {
        assertThat(LicensePlate.ofNullable("ab-123-cd").value()).isEqualTo("AB-123-CD");
        assertThat(LicensePlate.ofNullable(" ")).isNull();
        assertThat(LicensePlate.ofNullable(null)).isNull();
        assertThatThrownBy(() -> new LicensePlate("A".repeat(13))).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void mileage_and_cost_stay_within_bounds() {
        assertThat(new Mileage(0).kilometers()).isZero();
        assertThat(new Mileage(2_000_000).kilometers()).isEqualTo(2_000_000);
        assertThat(Mileage.ofNullable(null)).isNull();
        assertThatThrownBy(() -> new Mileage(-1)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new Mileage(2_000_001)).isInstanceOf(InvalidValueException.class);
        assertThat(new Cost(10_000_000L).cents()).isEqualTo(10_000_000L);
        assertThatThrownBy(() -> new Cost(-1)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new Cost(10_000_001L)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_maintenance_kind_must_be_known() {
        assertThat(MaintenanceKind.of("TECHNICAL_INSPECTION")).isEqualTo(MaintenanceKind.TECHNICAL_INSPECTION);
        assertThatThrownBy(() -> MaintenanceKind.of("WASH")).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> MaintenanceKind.of(null)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void deadlines_are_optional() {
        var deadlines = new Deadlines(LocalDate.of(2027, 3, 1), null);

        assertThat(deadlines.technicalInspection()).contains(LocalDate.of(2027, 3, 1));
        assertThat(deadlines.service()).isEmpty();
        assertThat(MaintenanceNote.ofNullable("")).isNull();
    }
}
