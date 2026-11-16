package io.github.doriangrelu.lifemanager.vehicle.application.usecase;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.ListVehicles;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListVehiclesQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;
import io.github.doriangrelu.lifemanager.vehicle.application.port.out.VehicleRepository;
import io.github.doriangrelu.lifemanager.vehicle.application.service.VehicleAccess;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
/** Liste les véhicules d'un foyer pour l'un de ses membres. */
@Singleton
class ListVehiclesUseCase implements ListVehicles {

    private final VehicleAccess access;
    private final VehicleRepository vehicles;

    ListVehiclesUseCase(VehicleAccess access, VehicleRepository vehicles) {
        this.access = access;
        this.vehicles = vehicles;
    }

    @Override
    @Transactional(transactionManager = "vehicle", readOnly = true)
    public List<VehicleView> list(ListVehiclesQuery query) {
        var household = access.householdOfMember(query.householdId(), query.requesterId());
        return vehicles.findAllByHousehold(household).stream().map(VehicleView::from).toList();
    }
}
