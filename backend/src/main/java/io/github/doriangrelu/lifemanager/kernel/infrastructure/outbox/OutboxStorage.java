package io.github.doriangrelu.lifemanager.kernel.infrastructure.outbox;

import io.micronaut.transaction.TransactionOperations;

import java.sql.Connection;

/**
 * Où vit la boîte d'envoi d'un domaine : sa table et les transactions de sa base.
 *
 * @param domain       nom du domaine (journaux)
 * @param table        dépôt de la table {@code outbox} du domaine
 * @param transactions transactions de la datasource du domaine
 */
public record OutboxStorage(String domain, OutboxJdbcRepository table, TransactionOperations<Connection> transactions) {
}
