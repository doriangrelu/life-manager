package io.github.doriangrelu.lifemanager.household.application.service;

import io.github.doriangrelu.lifemanager.household.application.port.out.HouseholdRepository;
import io.github.doriangrelu.lifemanager.household.domain.exception.MembershipLimitReachedException;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import jakarta.inject.Singleton;

/**
 * Plafond du nombre de foyers par utilisateur, partagé par la création et l'adhésion.
 * Règle sur plusieurs foyers à la fois : elle ne peut pas vivre dans l'agrégat Household.
 */
@Singleton
public class MembershipQuota {

    /** Nombre maximal de foyers auxquels un utilisateur peut appartenir. */
    public static final int MAX_HOUSEHOLDS_PER_MEMBER = 10;

    private final HouseholdRepository households;

    MembershipQuota(HouseholdRepository households) {
        this.households = households;
    }

    /** @throws MembershipLimitReachedException si l'utilisateur a déjà atteint le plafond */
    public void ensureCanJoinAnother(MemberId member) {
        if (households.countByMember(member) >= MAX_HOUSEHOLDS_PER_MEMBER) {
            throw new MembershipLimitReachedException(MAX_HOUSEHOLDS_PER_MEMBER);
        }
    }
}
