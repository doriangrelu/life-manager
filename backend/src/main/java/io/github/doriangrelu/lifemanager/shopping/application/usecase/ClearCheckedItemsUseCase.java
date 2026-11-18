package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.ClearCheckedItems;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.ClearCheckedItemsCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.PurchaseHistoryRepository;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.ShoppingListAccess;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ItemLabel;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.PurchaseHistoryEntryId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingListId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fin des courses : retire d'un coup les articles cochés d'une sous-liste et les inscrit à son historique
 * (une ligne par libellé, sans tenir compte des majuscules).
 */
@Singleton
class ClearCheckedItemsUseCase implements ClearCheckedItems {

    private final ShoppingListAccess access;
    private final ShoppingItemRepository items;
    private final PurchaseHistoryRepository history;
    private final Clock clock;

    ClearCheckedItemsUseCase(ShoppingListAccess access, ShoppingItemRepository items,
                             PurchaseHistoryRepository history, Clock clock) {
        this.access = access;
        this.items = items;
        this.history = history;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public void clear(ClearCheckedItemsCommand command) {
        var list = access.listForMember(command.householdId(), command.listId(), command.requesterId());
        var known = history.findAllByList(list.id()).stream()
                .collect(Collectors.toMap(entry -> key(entry.label()), Function.identity()));
        purchasedLabels(list.id()).forEach((key, label) -> history.save(recordPurchase(known.get(key), list.id(),
                label)));
        items.deleteChecked(list.id());
    }

    /** Libellés achetés, un par clé : « Lait » et « lait » cochés ensemble ne comptent qu'une fois. */
    private Map<String, ItemLabel> purchasedLabels(ShoppingListId list) {
        return items.findCheckedByList(list).stream()
                .map(ShoppingItem::label)
                .collect(Collectors.toMap(ClearCheckedItemsUseCase::key, Function.identity(), (first, other) -> first,
                        LinkedHashMap::new));
    }

    private PurchaseHistoryEntry recordPurchase(PurchaseHistoryEntry known, ShoppingListId list, ItemLabel label) {
        if (known == null) {
            return PurchaseHistoryEntry.firstPurchase(PurchaseHistoryEntryId.newId(), list, label, clock);
        }
        known.recordPurchase(clock);
        return known;
    }

    private static String key(ItemLabel label) {
        return label.value().toLowerCase(Locale.ROOT);
    }
}
