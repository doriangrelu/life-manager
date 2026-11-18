package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.exception.InvalidValueException;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.MaintenanceAttachmentRemoved;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentFileName;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachmentMediaType;
import io.github.doriangrelu.lifemanager.kernel.domain.file.FileSize;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.kernel.domain.file.StorageKey;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MaintenanceAttachmentTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-16T10:00:00Z"), ZoneId.of("Europe/Paris"));

    @Test
    void a_file_name_loses_its_path_and_control_characters() {
        assertThat(AttachmentFileName.clean("C:\\scans\\facture.pdf").value()).isEqualTo("facture.pdf");
        assertThat(AttachmentFileName.clean("../../etc/passwd").value()).isEqualTo("passwd");
        assertThat(AttachmentFileName.clean("fac\u0000ture\n.pdf").value()).isEqualTo("facture.pdf");
        assertThatThrownBy(() -> AttachmentFileName.clean("dossier/")).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void a_long_file_name_is_shortened_but_keeps_its_extension() {
        var name = AttachmentFileName.clean("a".repeat(200) + ".pdf").value();

        assertThat(name).hasSize(120).endsWith("a.pdf");
    }

    @Test
    void a_file_weighs_1_byte_to_10_megabytes() {
        assertThat(new FileSize(10L * 1024 * 1024).bytes()).isEqualTo(10_485_760L);
        assertThatThrownBy(() -> new FileSize(0)).isInstanceOf(InvalidValueException.class);
        assertThatThrownBy(() -> new FileSize(10L * 1024 * 1024 + 1)).isInstanceOf(InvalidValueException.class);
    }

    @Test
    void only_pdf_and_pictures_are_accepted() {
        assertThat(AttachmentMediaType.ofMimeType("application/pdf")).isEqualTo(AttachmentMediaType.PDF);
        assertThat(AttachmentMediaType.ofMimeType("image/webp")).isEqualTo(AttachmentMediaType.WEBP);
        assertThatThrownBy(() -> AttachmentMediaType.ofMimeType("application/x-msdownload"))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void removing_an_attachment_asks_to_delete_its_file() {
        var key = StorageKey.newKey();
        var file = new AttachedFile(new AttachmentFileName("facture.pdf"), AttachmentMediaType.PDF, new FileSize(42),
                key);
        var attachment = MaintenanceAttachment.attach(AttachmentId.newId(), MaintenanceEntryId.newId(), file,
                new MemberId(UUID.randomUUID()), CLOCK);

        attachment.markRemoved(CLOCK);

        assertThat(attachment.pullEvents()).containsExactly(new MaintenanceAttachmentRemoved(key, CLOCK.instant()));
    }
}
