package io.github.doriangrelu.lifemanager.vehicle.application.port.in;

import io.github.doriangrelu.lifemanager.vehicle.application.port.in.query.ListVehiclesQuery;
import io.github.doriangrelu.lifemanager.vehicle.application.port.in.result.VehicleView;

import java.util.List;
/** Liste les véhicules d'un foyer, triés par nom. */
public interface ListVehicles {

    List<VehicleView> list(ListVehiclesQuery query);
}
