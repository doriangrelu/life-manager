package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Destinataire de test : note ce qu'il reçoit et peut refuser un événement un certain nombre de fois. */
@Singleton
class ProbeListener {

    private final List<Integer> received = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> failures = new ConcurrentHashMap<>();

    @EventListener
    void on(ProbeEventV1 event) {
        if (failures.getOrDefault(event.number(), 0) > 0) {
            failures.merge(event.number(), -1, Integer::sum);
            throw new IllegalStateException("destinataire indisponible");
        }
        received.add(event.number());
    }

    /** L'événement {@code number} sera refusé {@code times} fois. */
    void failFor(int number, int times) {
        failures.put(number, times);
    }

    List<Integer> received() {
        return received;
    }
}
