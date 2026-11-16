package io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.mapper;

import io.github.doriangrelu.lifemanager.identity.domain.aggregate.UserAccount;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.DisplayName;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.EmailAddress;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.entity.UserAccountEntity;

/** Conversion entre l'agrégat {@link UserAccount} et sa ligne en base. */
public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccountEntity toEntity(UserAccount account) {
        return new UserAccountEntity(
                account.id().value(),
                account.email().value(),
                account.displayName().value(),
                account.registeredAt());
    }

    public static UserAccount toDomain(UserAccountEntity entity) {
        return UserAccount.restore(
                new UserId(entity.id()),
                new EmailAddress(entity.email()),
                new DisplayName(entity.displayName()),
                entity.registeredAt());
    }
}
