package io.github.doriangrelu.lifemanager.platform.infrastructure.security.exception;

/**
 * Keycloak n'a pas pu vérifier le jeton (injoignable, erreur serveur, mauvaise configuration).
 * La requête est refusée avec un 503 : on ne sait pas si l'utilisateur est authentifié.
 */
public class IdentityProviderUnavailableException extends RuntimeException {

    public IdentityProviderUnavailableException(Throwable cause) {
        super("Le fournisseur d'identité est indisponible", cause);
    }
}
