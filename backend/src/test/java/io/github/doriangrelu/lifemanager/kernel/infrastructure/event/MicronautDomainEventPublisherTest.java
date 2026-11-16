package io.github.doriangrelu.lifemanager.kernel.infrastructure.event;

import io.github.doriangrelu.lifemanager.kernel.application.port.out.DomainEventPublisher;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import io.github.doriangrelu.lifemanager.support.PostgresTestSupport;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautDomainEventPublisherTest extends PostgresTestSupport {

    record TestEvent(Instant occurredAt) implements DomainEvent {
    }

    static final class TestAggregate extends AggregateRoot<String> {

        TestAggregate() {
            super("test");
            recordEvent(new TestEvent(Instant.EPOCH));
        }
    }

    /** Écouteur de test qui mémorise les événements reçus. */
    @Singleton
    static class TestEventListener {

        final List<TestEvent> received = new ArrayList<>();

        @EventListener
        void on(TestEvent event) {
            received.add(event);
        }
    }

    @Inject
    DomainEventPublisher publisher;

    @Inject
    TestEventListener listener;

    @Test
    void events_reach_listeners_on_the_micronaut_bus() {
        var aggregate = new TestAggregate();

        publisher.publishEventsOf(aggregate);

        assertThat(listener.received).containsExactly(new TestEvent(Instant.EPOCH));
        assertThat(aggregate.pullEvents()).isEmpty();
    }
}
