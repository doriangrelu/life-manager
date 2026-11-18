package io.github.doriangrelu.lifemanager.bills.application.port.in;

import io.github.doriangrelu.lifemanager.bills.application.port.in.command.ChargeCommand;
import io.github.doriangrelu.lifemanager.bills.application.port.in.result.OccurrenceView;
import java.util.List;

/** Liste les échéances d'une charge, de la plus récente à la plus ancienne. */
public interface ListOccurrences {

    List<OccurrenceView> list(ChargeCommand command);
}
