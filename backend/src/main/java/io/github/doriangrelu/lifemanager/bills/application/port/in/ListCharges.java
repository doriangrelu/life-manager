package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.query.ListChargesQuery;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.ChargeView;
import java.util.List;

/** Liste les charges du foyer. */
public interface ListCharges {

    List<ChargeView> list(ListChargesQuery query);
}
