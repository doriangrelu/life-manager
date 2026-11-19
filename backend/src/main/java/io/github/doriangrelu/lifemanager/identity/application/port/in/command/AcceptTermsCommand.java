package io.github.doriangrelu.lifemanager.identity.application.port.in.command;

/**
 * @param subject {@code sub} Keycloak de l'utilisateur
 * @param version version des conditions qu'il a lues
 */
public record AcceptTermsCommand(String subject, String version) {
}
