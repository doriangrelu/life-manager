package io.github.doriangrelu.lifemanager.platform.infrastructure.security.user;

import io.github.doriangrelu.lifemanager.platform.infrastructure.security.introspection.Introspection;
import io.micronaut.security.authentication.Authentication;

import java.util.HashMap;

/**
 * Utilisateur à l'origine de la requête, tel que Keycloak l'a confirmé.
 * Les contrôleurs le construisent à partir de l'{@link Authentication} Micronaut.
 *
 * @param subject     identifiant Keycloak ({@code sub}), commun à tous les domaines
 * @param email       email
 * @param displayName nom d'affichage
 */
public record AuthenticatedUser(String subject, String email, String displayName) {

    private static final String EMAIL = "email";
    private static final String DISPLAY_NAME = "displayName";

    /** Relit l'utilisateur depuis l'authentification de la requête. */
    public static AuthenticatedUser from(Authentication authentication) {
        var attributes = authentication.getAttributes();
        return new AuthenticatedUser(authentication.getName(),
                (String) attributes.get(EMAIL),
                (String) attributes.get(DISPLAY_NAME));
    }

    /** Convertit une introspection réussie en authentification Micronaut. */
    public static Authentication toAuthentication(Introspection introspection) {
        // Map modifiable : les attributs absents (null) ne sont pas acceptés par Map.of.
        var attributes = new HashMap<String, Object>();
        attributes.put(EMAIL, introspection.email());
        attributes.put(DISPLAY_NAME, introspection.displayName());
        return Authentication.build(introspection.subject(), attributes);
    }
}
