package io.github.doriangrelu.lifemanager.identity.infrastructure.in.web.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.doriangrelu.lifemanager.platform.infrastructure.web.exception.TooManyRequestsException;
import jakarta.inject.Singleton;

import java.time.Duration;

/** Un export des données par minute et par utilisateur : l'export lit toutes les tables, il ne doit pas être répété en boucle. */
@Singleton
public class ExportLimiter {

    private static final Duration PERIOD = Duration.ofMinutes(1);

    // Présence dans le cache = un export fait il y a moins d'une minute.
    private final Cache<String, Boolean> recentByUser = Caffeine.newBuilder()
            .expireAfterWrite(PERIOD)
            .maximumSize(10_000)
            .build();

    /** @throws TooManyRequestsException si l'utilisateur a déjà exporté dans la minute */
    public void consume(String userId) {
        if (recentByUser.asMap().putIfAbsent(userId, Boolean.TRUE) != null) {
            throw new TooManyRequestsException("Un export par minute au plus : réessayez dans un instant");
        }
    }
}
