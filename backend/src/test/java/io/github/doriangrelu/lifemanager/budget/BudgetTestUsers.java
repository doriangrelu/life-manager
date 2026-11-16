package io.github.doriangrelu.lifemanager.budget;

import io.github.doriangrelu.lifemanager.support.FakeKeycloak;

import java.util.UUID;

/** Utilisateurs de test : un jeton accepté par le faux Keycloak et l'identifiant ({@code sub}) associé. */
final class BudgetTestUsers {

    record TestUser(String token, UUID id) {
    }

    private BudgetTestUsers() {
    }

    static TestUser newUser(FakeKeycloak keycloak, String name) {
        var token = "token-" + UUID.randomUUID();
        var id = keycloak.activeToken(token, UUID.randomUUID() + "@example.org", name);
        return new TestUser(token, UUID.fromString(id));
    }
}
