package io.github.doriangrelu.lifemanager.shopping.application.service;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.HouseholdData;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

/** Effacement en base des données d'un foyer, dans sa propre transaction (les fichiers sont supprimés après). */
@Singleton
public class HouseholdEraser {

    private final HouseholdData data;

    HouseholdEraser(HouseholdData data) {
        this.data = data;
    }

    /** @return les clés des fichiers du foyer, à supprimer maintenant que la base est validée */
    @Transactional(transactionManager = "shopping")
    public List<UUID> erase(HouseholdId householdId) {
        return data.erase(householdId);
    }
}
