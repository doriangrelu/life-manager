package io.github.doriangrelu.lifemanager.vehicle.domain.aggregate;

import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;
import io.github.doriangrelu.lifemanager.vehicle.domain.event.MaintenanceAttachmentRemoved;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.AttachmentId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MaintenanceEntryId;
import io.github.doriangrelu.lifemanager.vehicle.domain.valueobject.MemberId;

import java.time.Clock;
import java.time.Instant;

/**
 * Fichier joint à une opération du carnet (facture, photo). Agrégat à part : ajouter un fichier ne touche pas
 * à l'opération. Le contenu vit dans le stockage de fichiers, sous la clé de {@link AttachedFile}.
 */
public final class MaintenanceAttachment extends AggregateRoot<AttachmentId> {

    /** Nombre maximal de pièces jointes par opération. */
    public static final int MAX_PER_ENTRY = 10;

    private final MaintenanceEntryId entryId;
    private final AttachedFile file;
    private final MemberId addedBy;
    private final Instant addedAt;

    private MaintenanceAttachment(AttachmentId id, MaintenanceEntryId entryId, AttachedFile file,
                                  MemberId addedBy, Instant addedAt) {
        super(id);
        this.entryId = Require.notNull(entryId, "L'opération du carnet");
        this.file = Require.notNull(file, "Le fichier");
        this.addedBy = Require.notNull(addedBy, "L'auteur de l'ajout");
        this.addedAt = Require.notNull(addedAt, "La date d'ajout");
    }

    public static MaintenanceAttachment attach(AttachmentId id, MaintenanceEntryId entryId, AttachedFile file,
                                               MemberId addedBy, Clock clock) {
        return new MaintenanceAttachment(id, entryId, file, addedBy, clock.instant());
    }

    /** Recharge une pièce jointe depuis le stockage. */
    public static MaintenanceAttachment restore(AttachmentId id, MaintenanceEntryId entryId, AttachedFile file,
                                                MemberId addedBy, Instant addedAt) {
        return new MaintenanceAttachment(id, entryId, file, addedBy, addedAt);
    }

    /** La pièce jointe va être supprimée : son fichier devra l'être aussi, après validation. */
    public void markRemoved(Clock clock) {
        recordEvent(new MaintenanceAttachmentRemoved(file.storageKey(), clock.instant()));
    }

    public boolean belongsTo(MaintenanceEntryId entry) {
        return entryId.equals(entry);
    }

    public MaintenanceEntryId entryId() {
        return entryId;
    }

    public AttachedFile file() {
        return file;
    }

    public MemberId addedBy() {
        return addedBy;
    }

    public Instant addedAt() {
        return addedAt;
    }
}
