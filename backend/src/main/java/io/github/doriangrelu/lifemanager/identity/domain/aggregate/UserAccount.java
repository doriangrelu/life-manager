package io.github.doriangrelu.lifemanager.identity.domain.aggregate;

import io.github.doriangrelu.lifemanager.identity.domain.event.UserProfileChanged;
import io.github.doriangrelu.lifemanager.identity.domain.event.UserRegistered;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Instant;

/**
 * Profil local d'un utilisateur, copie de ce que Keycloak sait de lui.
 * Invariant : un seul profil par compte Keycloak, toujours avec un email et un nom valides.
 */
public final class UserAccount extends AggregateRoot<UserId> {

    private EmailAddress email;
    private DisplayName displayName;
    private final Instant registeredAt;

    private UserAccount(UserId id, EmailAddress email, DisplayName displayName, Instant registeredAt) {
        super(id);
        this.email = Require.notNull(email, "L'email");
        this.displayName = Require.notNull(displayName, "Le nom d'affichage");
        this.registeredAt = Require.notNull(registeredAt, "La date d'inscription");
    }

    /** Crée le profil d'un utilisateur qui arrive pour la première fois. */
    public static UserAccount register(UserId id, EmailAddress email, DisplayName displayName, Instant now) {
        var account = new UserAccount(id, email, displayName, now);
        account.recordEvent(new UserRegistered(id, email, displayName, now));
        return account;
    }

    /** Recharge un profil existant depuis le stockage (aucun événement produit). */
    public static UserAccount restore(UserId id, EmailAddress email, DisplayName displayName, Instant registeredAt) {
        return new UserAccount(id, email, displayName, registeredAt);
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

    public EmailAddress email() {
        return email;
    }

    public DisplayName displayName() {
        return displayName;
    }

    public Instant registeredAt() {
        return registeredAt;
    }
}
