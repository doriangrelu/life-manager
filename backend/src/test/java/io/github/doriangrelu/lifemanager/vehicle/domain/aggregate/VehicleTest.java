package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRegistered;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleRemoved;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.VehicleUpdated;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Deadlines;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneId.of("Europe/Paris"));
    private static final HouseholdId HOUSEHOLD = new HouseholdId(UUID.randomUUID());

    @Test
    void registering_a_vehicle_announces_it() {
        var vehicle = Vehicle.register(VehicleId.newId(), HOUSEHOLD, clio(Deadlines.NONE), CLOCK);

        assertThat(vehicle.belongsTo(HOUSEHOLD)).isTrue();
        assertThat(vehicle.pullEvents()).singleElement().isInstanceOf(VehicleRegistered.class);
    }

    @Test
    void updating_replaces_the_details_and_announces_the_new_deadlines() {
        var vehicle = Vehicle.register(VehicleId.newId(), HOUSEHOLD, clio(Deadlines.NONE), CLOCK);
        vehicle.pullEvents();
        var deadlines = new Deadlines(LocalDate.of(2029, 3, 1), null);

        vehicle.update(clio(deadlines), CLOCK);

        assertThat(vehicle.details().deadlines()).isEqualTo(deadlines);
        assertThat(vehicle.pullEvents()).singleElement()
                .isInstanceOfSatisfying(VehicleUpdated.class,
                        event -> assertThat(event.details().deadlines()).isEqualTo(deadlines));
    }

    @Test
    void removing_is_announced() {
        var vehicle = Vehicle.restore(VehicleId.newId(), HOUSEHOLD, clio(Deadlines.NONE));

        vehicle.markRemoved(CLOCK);

        assertThat(vehicle.pullEvents()).singleElement().isInstanceOf(VehicleRemoved.class);
    }

    private static VehicleDetails clio(Deadlines deadlines) {
        return new VehicleDetails(new VehicleName("Clio"), null, deadlines);
    }
}
