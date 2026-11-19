package io.github.doriangrelu.lifemanager.identity.application.service;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

/** Version des conditions d'utilisation en vigueur (une date) : la changer fait revalider tout le monde. */
@Singleton
public class TermsPolicy {

    private final String currentVersion;

    TermsPolicy(@Value("${life-manager.terms-version}") String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String currentVersion() {
        return currentVersion;
    }
}
