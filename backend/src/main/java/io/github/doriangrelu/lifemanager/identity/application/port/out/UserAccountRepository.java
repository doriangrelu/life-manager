package io.github.doriangrelu.lifemanager.identity.application.port.out;

import io.github.doriangrelu.lifemanager.identity.domain.aggregate.UserAccount;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;

import java.util.Optional;

/** Stockage des profils utilisateurs. */
public interface UserAccountRepository {

    Optional<UserAccount> findById(UserId id);

    /** Crée le profil ou met à jour un profil existant. */
    void save(UserAccount account);

    /** Efface le profil (suppression du compte). */
    void delete(UserId id);
}
