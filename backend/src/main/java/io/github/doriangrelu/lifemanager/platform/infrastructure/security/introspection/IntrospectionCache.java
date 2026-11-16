package io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.doriangrelu.lifemanager.platform.infrastructure.security.config.IntrospectionSettings;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Garde en mémoire les jetons valides pendant quelques secondes pour éviter d'appeler Keycloak à chaque requête.
 * Les jetons ne sont jamais stockés en clair : la clé est leur empreinte SHA-256.
 */
@Singleton
public class IntrospectionCache {

    private static final int MAX_ENTRIES = 10_000;

    private final Cache<String, Introspection> entries;

    IntrospectionCache(IntrospectionSettings settings) {
        this.entries = Caffeine.newBuilder()
                .expireAfterWrite(settings.cacheTtl())
                .maximumSize(MAX_ENTRIES)
                .build();
    }

    public Optional<Introspection> find(String token) {
        return Optional.ofNullable(entries.getIfPresent(fingerprint(token)));
    }

    public void remember(String token, Introspection introspection) {
        entries.put(fingerprint(token), introspection);
    }

    private static String fingerprint(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est garanti par toute JVM : ne peut pas arriver.
            throw new IllegalStateException(e);
        }
    }
}
