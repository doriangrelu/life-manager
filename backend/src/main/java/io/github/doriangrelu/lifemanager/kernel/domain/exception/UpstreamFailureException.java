package io.github.doriangrelu.lifemanager.kernel.domain.exception;

/** Un service extérieur (Keycloak…) a refusé ou n'a pas répondu ; le message dit à l'utilisateur quoi faire. */
public class UpstreamFailureException extends DomainException {

    public UpstreamFailureException(String message) {
        super(message);
    }
}
