package io.github.doriangrelu.lifemanager.shopping.application.service;

import io.github.doriangrelu.lifemanager.shopping.application.port.out.HouseholdMembers;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingListRepository;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingList;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingHouseholdNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingItemNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.exception.ShoppingListNotFoundException;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.HouseholdId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import jakarta.inject.Singleton;

import java.util.UUID;

/**
 * Cloisonnement des courses : seuls les membres d'un foyer accèdent à ses sous-listes et à leurs articles.
 * Tout use case qui cible un foyer passe par ici.
 */
@Singleton
public class ShoppingListAccess {

    private final HouseholdMembers members;
    private final ShoppingListRepository lists;
    private final ShoppingItemRepository items;

    ShoppingListAccess(HouseholdMembers members, ShoppingListRepository lists, ShoppingItemRepository items) {
        this.members = members;
        this.lists = lists;
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

    /** @throws ShoppingListNotFoundException si la sous-liste n'existe pas dans ce foyer */
    public ShoppingList listForMember(UUID householdId, UUID listId, String requesterId) {
        var household = householdOfMember(householdId, requesterId);
        return lists.findById(new ShoppingListId(listId))
                .filter(list -> list.belongsTo(household))
                .orElseThrow(ShoppingListNotFoundException::new);
    }

    /** @throws ShoppingItemNotFoundException si l'article n'existe pas dans cette sous-liste */
    public ShoppingItem itemForMember(UUID householdId, UUID listId, UUID itemId, String requesterId) {
        var list = listForMember(householdId, listId, requesterId);
        return items.findById(new ShoppingItemId(itemId))
                .filter(item -> item.belongsTo(list.id()))
                .orElseThrow(ShoppingItemNotFoundException::new);
    }
}
