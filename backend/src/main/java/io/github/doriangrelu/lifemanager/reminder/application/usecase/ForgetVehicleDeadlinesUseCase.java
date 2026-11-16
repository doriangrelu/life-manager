package io.github.doriangrelu.lifemanager.reminder.application.usecase;

import io.github.doriangrelu.lifemanager.reminder.application.port.in.ForgetVehicleDeadlines;
import io.github.doriangrelu.lifemanager.reminder.application.port.in.command.ForgetVehicleDeadlinesCommand;
import io.github.doriangrelu.lifemanager.reminder.application.port.out.DeadlineRepository;
import io.github.doriangrelu.lifemanager.reminder.domain.valueobject.SourceId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

/** Ne suit plus les échéances d'un véhicule supprimé. */
@Singleton
class ForgetVehicleDeadlinesUseCase implements ForgetVehicleDeadlines {

    private final DeadlineRepository deadlines;

    ForgetVehicleDeadlinesUseCase(DeadlineRepository deadlines) {
        this.deadlines = deadlines;
    }

    @Override
    @Transactional(transactionManager = "reminder")
    public void forget(ForgetVehicleDeadlinesCommand command) {
        deadlines.replaceForSource(new SourceId(command.vehicleId()), List.of());
    }
}
