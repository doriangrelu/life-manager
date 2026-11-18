package io.github.doriangrelu.lifemanager.kernel.domain.text;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Forme enregistrée des noms courts (article, catégorie, foyer…) : sans espaces autour,
 * espaces intérieurs réduits à un seul, en minuscules. La PWA remet une majuscule à l'affichage.
 */
public final class Names {

    private static final Pattern SPACES = Pattern.compile("\\s+");

    private Names() {
    }

    /** « &nbsp;LAIT   demi-écrémé » devient « lait demi-écrémé ». */
    public static String normalized(String text) {
        return SPACES.matcher(text.strip()).replaceAll(" ").toLowerCase(Locale.ROOT);
    }
}
