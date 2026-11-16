package io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Résultat d'une introspection Keycloak, réduit à ce dont l'application a besoin.
 *
 * @param active      le jeton est-il encore valide (non expiré, non révoqué)
 * @param subject     identifiant Keycloak de l'utilisateur ({@code sub})
 * @param email       email de l'utilisateur
 * @param displayName nom à afficher
 * @param audiences   destinataires autorisés du jeton ({@code aud})
 */
public record Introspection(boolean active, String subject, String email, String displayName, Set<String> audiences) {

    static final Introspection INACTIVE = new Introspection(false, null, null, null, Set.of());

    /** Construit le résultat à partir de la réponse JSON brute de Keycloak. */
    static Introspection fromResponse(Map<String, Object> response) {
        if (!Boolean.TRUE.equals(response.get("active"))) {
            return INACTIVE;
        }
        return new Introspection(true,
                text(response, "sub"),
                text(response, "email"),
                displayNameOf(response),
                audiencesOf(response.get("aud")));
    }

    /** Vrai si le jeton est actif et destiné à l'audience attendue. */
    public boolean isValidFor(String expectedAudience) {
        return active && subject != null && audiences.contains(expectedAudience);
    }

    // Keycloak ne renseigne "name" que si prénom ou nom existent : on se rabat sur l'identifiant de connexion.
    private static String displayNameOf(Map<String, Object> response) {
        var name = text(response, "name");
        if (name != null && !name.isBlank()) {
            return name;
        }
        var username = text(response, "preferred_username");
        return username != null ? username : text(response, "email");
    }

    // "aud" est une chaîne s'il n'y a qu'une audience, une liste sinon.
    private static Set<String> audiencesOf(Object aud) {
        return switch (aud) {
            case String single -> Set.of(single);
            case Collection<?> many -> many.stream().map(String::valueOf).collect(Collectors.toUnmodifiableSet());
            case null, default -> Set.of();
        };
    }

    private static String text(Map<String, Object> response, String claim) {
        var value = response.get(claim);
        return value == null ? null : value.toString();
    }
}
