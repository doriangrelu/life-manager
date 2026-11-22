package io.github.doriangrelu.lifemanager.vehicle.application.service;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.MaintenanceInput;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.command.VehicleInput;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Cost;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Deadlines;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.LicensePlate;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceKind;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceNote;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceOtherLabel;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.Mileage;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.TireSize;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleDetails;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.VehicleName;

/** Transforme les saisies (types simples) en value objects vérifiés, pour les use cases du domaine véhicule. */
public final class VehicleInputs {

    private VehicleInputs() {
    }

    public static VehicleDetails details(VehicleInput input) {
        return new VehicleDetails(
                new VehicleName(input.name()),
                LicensePlate.ofNullable(input.licensePlate()),
                new Deadlines(input.technicalInspectionDue(), input.serviceDue()),
                Mileage.ofNullable(input.mileageKm()),
                TireSize.ofNullable(input.tireSize()));
    }

    public static MaintenanceDetails details(MaintenanceInput input) {
        return new MaintenanceDetails(
                input.performedOn(),
                MaintenanceKind.of(input.kind()),
                Mileage.ofNullable(input.mileageKm()),
                Cost.ofNullable(input.costCents()),
                MaintenanceNote.ofNullable(input.note()),
                MaintenanceOtherLabel.ofNullable(input.otherLabel()));
    }
}
