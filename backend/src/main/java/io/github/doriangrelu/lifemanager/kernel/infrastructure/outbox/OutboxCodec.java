package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.core.beans.BeanIntrospector;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Met un événement de contrat en JSON et le relit.
 * Le type est retrouvé par les introspections Micronaut (événements {@code @Serdeable}) : pas de réflexion, compatible natif.
 */
@Singleton
public class OutboxCodec {

    private final JsonMapper json;
    private final Map<String, Class<?>> types = new ConcurrentHashMap<>();

    OutboxCodec(JsonMapper json) {
        this.json = json;
    }

    String typeOf(Object event) {
        return event.getClass().getName();
    }

    String encode(Object event) {
        try {
            return json.writeValueAsString(event);
        } catch (IOException e) {
            throw new UncheckedIOException("Événement impossible à enregistrer : " + typeOf(event), e);
        }
    }

    Object decode(String type, String payload) {
        try {
            return json.readValue(payload, types.computeIfAbsent(type, OutboxCodec::find));
        } catch (IOException e) {
            throw new UncheckedIOException("Événement illisible : " + type, e);
        }
    }

    private static Class<?> find(String type) {
        return BeanIntrospector.SHARED.findIntrospections(ref -> ref.getBeanType().getName().equals(type)).stream()
                .findFirst()
                .<Class<?>>map(introspection -> introspection.getBeanType())
                .orElseThrow(() -> new IllegalStateException("Type d'événement inconnu : " + type));
    }
}
