package io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.domain.aggregate.UserAccount;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.mapper.UserAccountMapper;
import io.github.doriangrelu.lifemanager.identity.infrastructure.out.persistence.repository.UserAccountJdbcRepository;
import jakarta.inject.Singleton;

import java.util.Optional;

/** Implémente le stockage des profils avec PostgreSQL (schéma identity). */
@Singleton
class UserAccountRepositoryAdapter implements UserAccountRepository {

    private final UserAccountJdbcRepository table;

    UserAccountRepositoryAdapter(UserAccountJdbcRepository table) {
        this.table = table;
    }

    @Override
    public Optional<UserAccount> findById(UserId id) {
        return table.findById(id.value()).map(UserAccountMapper::toDomain);
    }

    @Override
    public void save(UserAccount account) {
        var entity = UserAccountMapper.toEntity(account);
        // L'identifiant vient de Keycloak : on choisit nous-mêmes entre insertion et mise à jour.
        if (table.existsById(entity.id())) {
            table.update(entity);
        } else {
            table.save(entity);
        }
    }
}
