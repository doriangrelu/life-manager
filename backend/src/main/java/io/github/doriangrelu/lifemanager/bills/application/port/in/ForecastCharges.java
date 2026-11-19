package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ForecastQuery;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ForecastItem;

import java.util.List;

/** Dépenses et rentrées récurrentes encore attendues sur une période (pour le « prévu » du bilan). */
public interface ForecastCharges {

    List<ForecastItem> forecast(ForecastQuery query);
}
