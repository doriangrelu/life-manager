package io.github.doriangrelu.lifemanager.identity.application.port.in;

import io.github.doriangrelu.lifemanager.identity.application.port.in.command.AcceptTermsCommand;

/** L'utilisateur accepte les conditions d'utilisation qu'il vient de lire. */
public interface AcceptTerms {

    void accept(AcceptTermsCommand command);
}
