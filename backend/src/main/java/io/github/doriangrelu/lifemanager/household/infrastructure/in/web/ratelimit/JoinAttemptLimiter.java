package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.doriangrelu.lifemanager.platform.infrastructure.web.exception.TooManyRequestsException;
import jakarta.inject.Singleton;

import java.time.Duration;

/**
 * Limite les essais de code d'invitation à {@value #MAX_ATTEMPTS} par heure et par utilisateur,
 * pour empêcher de deviner un code à force d'essais.
 */
@Singleton
public class JoinAttemptLimiter {

    static final int MAX_ATTEMPTS = 10;
    private static final Duration PERIOD = Duration.ofHours(1);

    // Un « seau » de jetons par utilisateur, oublié après une heure sans tentative.
    private final Cache<String, Bucket> bucketsByUser = Caffeine.newBuilder()
            .expireAfterAccess(PERIOD)
            .maximumSize(10_000)
            .build();

    /** @throws TooManyRequestsException si l'utilisateur a épuisé ses essais */
    public void consumeAttempt(String userId) {
        var bucket = bucketsByUser.get(userId, ignored -> newBucket());
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException("Trop d'essais de code d'invitation, réessayez plus tard");
        }
    }

    private static Bucket newBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(MAX_ATTEMPTS).refillIntervally(MAX_ATTEMPTS, PERIOD))
                .build();
    }
}
