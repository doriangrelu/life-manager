package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.command.IssueInvitationCommand;
import io.github.doriangrelu.lifemanager.household.application.port.in.result.InvitationDetails;
/** Génère un nouveau code d'invitation pour un foyer ; l'ancien code en attente ne marche plus. */
public interface IssueInvitation {

    InvitationDetails issue(IssueInvitationCommand command);
}
