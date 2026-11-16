package io.github.doriangrelu.lifemanager.kernel.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateRootTest {

    record SomethingHappened(Instant occurredAt) implements DomainEvent {
    }

    static final class SampleAggregate extends AggregateRoot<String> {

        SampleAggregate(String id) {
            super(id);
        }

        void doSomething() {
            recordEvent(new SomethingHappened(Instant.EPOCH));
        }
    }

    @Test
    void pulling_events_returns_them_once() {
        var aggregate = new SampleAggregate("a-1");
        aggregate.doSomething();

        assertThat(aggregate.pullEvents()).containsExactly(new SomethingHappened(Instant.EPOCH));
        assertThat(aggregate.pullEvents()).isEmpty();
    }

    @Test
    void aggregates_with_same_id_are_equal() {
        assertThat(new SampleAggregate("a-1")).isEqualTo(new SampleAggregate("a-1"));
        assertThat(new SampleAggregate("a-1")).isNotEqualTo(new SampleAggregate("a-2"));
    }
}
