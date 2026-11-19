package io.github.doriangrelu.lifemanager.identity.domain.aggregate;

import io.github.doriangrelu.lifemanager.identity.domain.event.UserDeleted;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserProfileChanged;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserRegistered;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.TermsAcceptance;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/**
 * Profil local d'un utilisateur, copie de ce que Keycloak sait de lui, avec les conditions qu'il a acceptées.
 * Invariant : un seul profil par compte Keycloak, toujours avec un email et un nom valides.
 */
public final class UserAccount extends AggregateRoot<UserId> {

    private EmailAddress email;
    private DisplayName displayName;
    private final Instant registeredAt;
    /** Dernières conditions acceptées ; null s'il n'en a jamais accepté. */
    private TermsAcceptance terms;

    private UserAccount(UserId id, EmailAddress email, DisplayName displayName, Instant registeredAt,
                        TermsAcceptance terms) {
        super(id);
        this.email = Require.notNull(email, "L'email");
        this.displayName = Require.notNull(displayName, "Le nom d'affichage");
        this.registeredAt = Require.notNull(registeredAt, "La date d'inscription");
        this.terms = terms;
    }

    /** Crée le profil d'un utilisateur qui arrive pour la première fois. */
    public static UserAccount register(UserId id, EmailAddress email, DisplayName displayName, Instant now) {
        var account = new UserAccount(id, email, displayName, now, null);
        account.recordEvent(new UserRegistered(id, email, displayName, now));
        return account;
    }

    /** Recharge un profil qui n'a jamais accepté de conditions (aucun événement produit). */
    public static UserAccount restore(UserId id, EmailAddress email, DisplayName displayName, Instant registeredAt) {
        return new UserAccount(id, email, displayName, registeredAt, null);
    }

    /** Recharge un profil existant depuis le stockage (aucun événement produit). */
    public static UserAccount restore(UserId id, EmailAddress email, DisplayName displayName, Instant registeredAt,
                                      TermsAcceptance terms) {
        return new UserAccount(id, email, displayName, registeredAt, terms);
    }

    /** Aligne le profil sur Keycloak ; ne produit un événement que si quelque chose change. */
    public void updateProfile(EmailAddress newEmail, DisplayName newDisplayName, Instant now) {
        Require.notNull(newEmail, "L'email");
        Require.notNull(newDisplayName, "Le nom d'affichage");
        if (email.equals(newEmail) && displayName.equals(newDisplayName)) {
            return;
        }
        email = newEmail;
        displayName = newDisplayName;
        recordEvent(new UserProfileChanged(id(), newEmail, newDisplayName, now));
    }

    /**
     * Accepte les conditions lues par l'utilisateur, qui doivent être celles en vigueur.
     *
     * @throws InvalidValueException si l'utilisateur a lu une autre version
     */
    public void acceptTerms(String currentVersion, String readVersion, Instant now) {
        if (!Require.notBlank(currentVersion, "La version des conditions").equals(readVersion)) {
            throw new InvalidValueException("Ces conditions ne sont plus en vigueur : rechargez la page");
        }
        terms = new TermsAcceptance(currentVersion, now);
    }

    /** Vrai si l'utilisateur a accepté exactement cette version des conditions. */
    public boolean hasAccepted(String version) {
        return terms != null && terms.version().equals(version);
    }

    /**
     * Suppression demandée par l'utilisateur, qui retape son e-mail pour confirmer.
     *
     * @throws InvalidValueException si l'e-mail retapé ne correspond pas
     */
    public void delete(String confirmEmail, Instant now) {
        if (confirmEmail == null || confirmEmail.isBlank() || !email.equals(new EmailAddress(confirmEmail.trim()))) {
            throw new InvalidValueException("L'adresse e-mail ne correspond pas à votre compte");
        }
        recordEvent(new UserDeleted(id(), now));
    }

    public EmailAddress email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    /** Dernières conditions acceptées, ou null. */
    public TermsAcceptance terms() {
        return terms;
    }
}
