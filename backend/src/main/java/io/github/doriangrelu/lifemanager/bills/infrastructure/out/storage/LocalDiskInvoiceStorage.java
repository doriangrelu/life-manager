package io.github.doriangrelu.lifemanager.bills.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.bills.application.port.out.InvoiceStorage;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import io.github.doriangrelu.lifemanager.kernel.infrastructure.storage.DiskFileStore;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.io.InputStream;

/** Factures sur le disque du serveur, dans le dossier {@code bills/} (voir {@link DiskFileStore}). */
@Singleton
class LocalDiskInvoiceStorage implements InvoiceStorage {

    private final DiskFileStore files;

    LocalDiskInvoiceStorage(@Value("${life-manager.storage.root}") String storageRoot) {
        this.files = new DiskFileStore(storageRoot, "bills");
    }

    @Override
    public void store(StorageKey key, byte[] content) {
        files.store(key, content);
    }

    @Override
    public InputStream open(StorageKey key) {
        return files.open(key);
    }

    @Override
    public void delete(StorageKey key) {
        files.delete(key);
    }
}
