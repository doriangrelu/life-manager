package io.github.doriangrelu.lifemanager.household.domain.valueobject;

/** Cycle de vie d'une invitation : elle n'est utilisable que tant qu'elle est en attente. */
public enum InvitationStatus {
    /** Pas encore utilisée. */
    PENDING,
    /** Utilisée par quelqu'un pour rejoindre le foyer. */
    ACCEPTED,
    /** Remplacée par un nouveau code avant d'avoir servi. */
    REVOKED
}
