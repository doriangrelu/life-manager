package io.github.doriangrelu.lifemanager.household.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Locale;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

/**
 * Code d'invitation à communiquer à la personne invitée : 8 caractères faciles à recopier
 * (pas de 0/O ni de 1/I). Saisie insensible à la casse et aux espaces autour.
 */
public record InvitationCode(String value) {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LENGTH = 8;
    private static final Pattern FORMAT = Pattern.compile("^[" + ALPHABET + "]{" + LENGTH + "}$");

    public InvitationCode {
        value = Require.notBlank(value, "Le code d'invitation").toUpperCase(Locale.ROOT);
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidValueException("Le code d'invitation doit faire " + LENGTH + " caractères valides");
        }
    }

    /** Tire un nouveau code au hasard (passer un générateur cryptographique, ex. SecureRandom). */
    public static InvitationCode random(RandomGenerator random) {
        var code = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return new InvitationCode(code.toString());
    }
}
