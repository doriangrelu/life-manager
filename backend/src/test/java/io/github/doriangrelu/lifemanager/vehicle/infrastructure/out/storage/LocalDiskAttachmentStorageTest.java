package io.github.doriangrelu.lifemanager.vehicle.infrastructure.out.storage;

import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.StorageKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDiskAttachmentStorageTest {

    @TempDir
    Path root;

    @Test
    void a_file_is_stored_under_its_key_and_read_back() throws IOException {
        var storage = new LocalDiskAttachmentStorage(root.toString());
        var key = new StorageKey(UUID.fromString("ab12cd34-0000-4000-8000-000000000000"));

        storage.store(key, new byte[] {1, 2, 3});

        var expected = root.resolve("vehicle/ab/ab12cd34-0000-4000-8000-000000000000");
        assertThat(expected).exists();
        try (var content = storage.open(key)) {
            assertThat(content.readAllBytes()).containsExactly(1, 2, 3);
        }
        // Aucun fichier temporaire ne traîne après l'écriture.
        try (var files = Files.list(expected.getParent())) {
            assertThat(files).containsExactly(expected);
        }
    }

    @Test
    void deleting_removes_the_file_and_tolerates_a_missing_one() {
        var storage = new LocalDiskAttachmentStorage(root.toString());
        var key = StorageKey.newKey();
        storage.store(key, new byte[] {1});

        storage.delete(key);
        storage.delete(key);

        assertThat(storage.pathOf(key)).doesNotExist();
    }
}
