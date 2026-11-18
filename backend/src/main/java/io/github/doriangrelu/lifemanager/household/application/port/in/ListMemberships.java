package io.github.doriangrelu.lifemanager.household.application.port.in;

import io.github.doriangrelu.lifemanager.household.application.port.in.result.MembershipView;

import java.util.List;

/** Toutes les appartenances (foyer, membre), pour les rappeler aux autres domaines. */
public interface ListMemberships {

    List<MembershipView> listAll();
}
