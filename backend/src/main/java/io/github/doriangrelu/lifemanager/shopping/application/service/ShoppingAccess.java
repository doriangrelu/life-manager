package io.github.doriangrelu.lifemanager.shopping.application.service;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingItemNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Cloisonnement de la liste de courses : seuls les membres d'un foyer accèdent à ses articles.
 * Tout use case qui cible un foyer passe par ici.
 */
@Singleton
public class ShoppingAccess {

    private final HouseholdMembers members;
    private final ShoppingItemRepository items;

    ShoppingAccess(HouseholdMembers members, ShoppingItemRepository items) {
        this.members = members;
        this.items = items;
    }

    /** @throws ShoppingHouseholdNotFoundException si le demandeur n'est pas membre du foyer */
    public HouseholdId householdOfMember(UUID householdId, String requesterId) {
        var household = new HouseholdId(householdId);
        if (!members.isMember(household, MemberId.of(requesterId))) {
            throw new ShoppingHouseholdNotFoundException();
        }
        return household;
    }

    /** @throws ShoppingItemNotFoundException si l'article n'existe pas dans la liste de ce foyer */
    public ShoppingItem loadForMember(UUID householdId, UUID itemId, String requesterId) {
        var household = householdOfMember(householdId, requesterId);
        return items.findById(new ShoppingItemId(itemId))
                .filter(item -> item.belongsTo(household))
                .orElseThrow(ShoppingItemNotFoundException::new);
    }
}
