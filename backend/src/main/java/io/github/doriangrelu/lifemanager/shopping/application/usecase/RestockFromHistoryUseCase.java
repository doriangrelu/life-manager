package io.github.doriangrelu.lifemanager.shopping.application.usecase;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.RestockFromHistory;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.command.RestockFromHistoryCommand;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.ShoppingItemView;
import io.github.doriangrelu.lifemanager.shopping.application.port.out.ShoppingItemRepository;
import io.github.doriangrelu.lifemanager.shopping.application.service.PurchaseHistoryAccess;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.PurchaseHistoryEntry;
import io.github.doriangrelu.lifemanager.shopping.domain.aggregate.ShoppingItem;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.shopping.domain.valueobject.ShoppingItemId;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Remet en liste, non cochés, des articles de l'historique. Tout ou rien : un seul élément inconnu et rien n'est
 * ajouté. Un article déjà à acheter dans la sous-liste n'est pas ajouté une seconde fois.
 */
@Singleton
class RestockFromHistoryUseCase implements RestockFromHistory {

    private final PurchaseHistoryAccess access;
    private final ShoppingItemRepository items;
    private final Clock clock;

    RestockFromHistoryUseCase(PurchaseHistoryAccess access, ShoppingItemRepository items, Clock clock) {
        this.access = access;
        this.items = items;
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "shopping")
    public List<ShoppingItemView> restock(RestockFromHistoryCommand command) {
        // Chargement complet avant tout ajout : une erreur 404 n'ajoute rien.
        var entries = command.entryIds().stream()
                .distinct()
                .map(entryId -> access.entryForMember(command.householdId(), command.listId(), entryId,
                        command.requesterId()))
                .toList();
        var pending = pendingLabels(entries);
        var addedBy = MemberId.of(command.requesterId());
        var added = entries.stream()
                .filter(entry -> !pending.contains(key(entry)))
                .map(entry -> ShoppingItem.add(ShoppingItemId.newId(), entry.listId(), entry.label(), addedBy, clock))
                .toList();
        added.forEach(items::save);
        return added.stream().map(ShoppingItemView::from).toList();
    }

    private Set<String> pendingLabels(List<PurchaseHistoryEntry> entries) {
        if (entries.isEmpty()) {
            return Set.of();
        }
        return items.findAllByList(entries.getFirst().listId()).stream()
                .filter(item -> !item.isChecked())
                .map(item -> item.label().value().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    private static String key(PurchaseHistoryEntry entry) {
        return entry.label().value().toLowerCase(Locale.ROOT);
    }
}
