package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceResponse;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleResponse;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;

import java.util.List;
import java.util.UUID;

/** Petit client de test pour l'API des véhicules : un appel = une méthode lisible. */
record VehicleApi(BlockingHttpClient http) {

    List<VehicleResponse> list(String token, UUID household) {
        return http.retrieve(HttpRequest.GET(base(household)).bearerAuth(token),
                Argument.listOf(VehicleResponse.class));
    }

    VehicleResponse register(String token, UUID household, VehicleRequest vehicle) {
        return http.retrieve(HttpRequest.POST(base(household), vehicle).bearerAuth(token), VehicleResponse.class);
    }

    VehicleResponse update(String token, UUID household, UUID vehicle, VehicleRequest changes) {
        return http.retrieve(HttpRequest.PUT(base(household) + "/" + vehicle, changes).bearerAuth(token),
                VehicleResponse.class);
    }

    void remove(String token, UUID household, UUID vehicle) {
        http.exchange(HttpRequest.DELETE(base(household) + "/" + vehicle).bearerAuth(token));
    }

    List<MaintenanceResponse> logbook(String token, UUID household, UUID vehicle) {
        return http.retrieve(HttpRequest.GET(maintenance(household, vehicle)).bearerAuth(token),
                Argument.listOf(MaintenanceResponse.class));
    }

    MaintenanceResponse record(String token, UUID household, UUID vehicle, MaintenanceRequest entry) {
        return http.retrieve(HttpRequest.POST(maintenance(household, vehicle), entry).bearerAuth(token),
                MaintenanceResponse.class);
    }

    void deleteEntry(String token, UUID household, UUID vehicle, UUID entry) {
        http.exchange(HttpRequest.DELETE(maintenance(household, vehicle) + "/" + entry).bearerAuth(token));
    }

    private static String base(UUID household) {
        return "/api/households/" + household + "/vehicles";
    }

    private static String maintenance(UUID household, UUID vehicle) {
        return base(household) + "/" + vehicle + "/maintenance";
    }
}
