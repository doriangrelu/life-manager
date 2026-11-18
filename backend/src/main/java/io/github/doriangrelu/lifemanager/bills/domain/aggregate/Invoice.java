package io.github.doriangrelu.lifemanager.bills.domain.aggregate;

import io.github.doriangrelu.lifemanager.bills.domain.event.InvoiceRemoved;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.InvoiceId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.bills.domain.valueobject.OccurrenceId;
import io.github.doriangrelu.lifemanager.kernel.domain.aggregate.AggregateRoot;
import io.github.doriangrelu.lifemanager.kernel.domain.file.AttachedFile;
import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.time.Clock;
import java.time.Instant;

/**
 * Facture (PDF, photo) jointe à une échéance. Agrégat à part : ajouter un fichier ne touche pas à l'échéance.
 * Le contenu vit dans le stockage de fichiers, sous la clé de {@link AttachedFile}.
 */
public final class Invoice extends AggregateRoot<InvoiceId> {

    /** Nombre maximal de factures par échéance. */
    public static final int MAX_PER_OCCURRENCE = 10;

    private final OccurrenceId occurrenceId;
    private final AttachedFile file;
    private final MemberId addedBy;
    private final Instant addedAt;

    private Invoice(InvoiceId id, OccurrenceId occurrenceId, AttachedFile file, MemberId addedBy, Instant addedAt) {
        super(id);
        this.occurrenceId = Require.notNull(occurrenceId, "L'échéance");
        this.file = Require.notNull(file, "Le fichier");
        this.addedBy = Require.notNull(addedBy, "L'auteur de l'ajout");
        this.addedAt = Require.notNull(addedAt, "La date d'ajout");
    }

    public static Invoice attach(InvoiceId id, OccurrenceId occurrenceId, AttachedFile file, MemberId addedBy,
                                 Clock clock) {
        return new Invoice(id, occurrenceId, file, addedBy, clock.instant());
    }

    /** Recharge une facture depuis le stockage. */
    public static Invoice restore(InvoiceId id, OccurrenceId occurrenceId, AttachedFile file, MemberId addedBy,
                                  Instant addedAt) {
        return new Invoice(id, occurrenceId, file, addedBy, addedAt);
    }

    /** La facture va être supprimée : son fichier devra l'être aussi, après validation. */
    public void markRemoved(Clock clock) {
        recordEvent(new InvoiceRemoved(file.storageKey(), clock.instant()));
    }

    public boolean belongsTo(OccurrenceId occurrence) {
        return occurrenceId.equals(occurrence);
    }

    public OccurrenceId occurrenceId() {
        return occurrenceId;
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
