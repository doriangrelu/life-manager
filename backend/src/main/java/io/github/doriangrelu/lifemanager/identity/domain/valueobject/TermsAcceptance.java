package io.github.doriangrelu.lifemanager.identity.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/** Conditions d'utilisation acceptées : leur version (une date, « 2026-11-19 ») et le moment de l'acceptation. */
public record TermsAcceptance(String version, Instant acceptedAt) {

    public TermsAcceptance {
        Require.notBlank(version, "La version des conditions");
        Require.notNull(acceptedAt, "La date d'acceptation des conditions");
    }
}
