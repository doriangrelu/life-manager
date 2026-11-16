package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.query.ListMyHouseholdsQuery;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.HouseholdSummary;

import java.util.List;

/** Liste les foyers dont le demandeur est membre. */
public interface ListMyHouseholds {

    List<HouseholdSummary> list(ListMyHouseholdsQuery query);
}
