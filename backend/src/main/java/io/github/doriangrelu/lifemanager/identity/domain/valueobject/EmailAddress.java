package io.github.doriangrelu.lifemanager.identity.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Adresse email d'un utilisateur, en minuscules.
 * Keycloak a déjà vérifié l'adresse : on contrôle seulement sa forme générale.
 */
public record EmailAddress(String value) {

    private static final String FIELD = "L'email";
    private static final int MAX_LENGTH = 254;
    // quelque-chose@domaine.extension, sans espace
    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        value = Require.maxLength(Require.notBlank(value, FIELD), MAX_LENGTH, FIELD).toLowerCase(Locale.ROOT);
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidValueException(FIELD + " n'a pas un format valide");
        }
    }
}
