package io.github.doriangrelu.lifemanager.identity.application.port.in.command;

/**
 * @param subject      {@code sub} Keycloak de l'utilisateur
 * @param confirmEmail e-mail retapé pour confirmer
 */
public record DeleteMyAccountCommand(String subject, String confirmEmail) {
}
