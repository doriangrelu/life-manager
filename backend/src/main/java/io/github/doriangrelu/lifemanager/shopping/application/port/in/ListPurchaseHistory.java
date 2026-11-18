package io.github.doriangrelu.lifemanager.shopping.application.port.in;

import io.github.doriangrelu.lifemanager.shopping.application.port.in.query.ListPurchaseHistoryQuery;
import io.github.doriangrelu.lifemanager.shopping.application.port.in.result.PurchaseHistoryEntryView;

import java.util.List;

/** Historique des achats d'une sous-liste, du plus récemment acheté au plus ancien. */
public interface ListPurchaseHistory {

    List<PurchaseHistoryEntryView> list(ListPurchaseHistoryQuery query);
}
