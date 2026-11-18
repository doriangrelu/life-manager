package io.github.doriangrelu.lifemanager.vehicle;

import io.github.doriangrelu.lifemanager.household.HouseholdApi;
import io.github.doriangrelu.lifemanager.support.IntegrationTestSupport;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.AttachmentResponse;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.MaintenanceRequest;
import io.github.doriangrelu.lifemanager.vehicle.infrastructure.in.web.dto.VehicleRequest;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.github.doriangrelu.lifemanager.household.HouseholdApi.failure;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaintenanceAttachmentIntegrationTest extends IntegrationTestSupport {

    private static final byte[] PDF = "%PDF-1.4\n1 0 obj<<>>endobj\ntrailer<<>>\n%%EOF\n"
            .getBytes(StandardCharsets.ISO_8859_1);
    // Signature d'un exécutable Windows : refusé même nommé « facture.pdf ».
    private static final byte[] EXECUTABLE = {'M', 'Z', (byte) 0x90, 0, 3, 0, 0, 0, 4, 0, 0, 0, (byte) 0xFF, (byte) 0xFF};

    @Inject
    @Client("/")
    HttpClient client;

    BlockingHttpClient http;
    HouseholdApi households;
    VehicleApi vehicles;

    @BeforeAll
    void setUp() {
        http = client.toBlocking();
        households = new HouseholdApi(http);
        vehicles = new VehicleApi(http);
    }

    @Test
    void a_pdf_is_attached_listed_and_downloaded_unchanged() {
        var logbook = newLogbook();

        var invoice = attach(logbook, "facture garage é.pdf", PDF);
        HttpResponse<byte[]> download = http.exchange(
                HttpRequest.GET(attachments(logbook) + "/" + invoice.id() + "/content").bearerAuth(logbook.owner),
                byte[].class);

        assertThat(invoice.fileName()).isEqualTo("facture garage é.pdf");
        assertThat(invoice.mediaType()).isEqualTo("application/pdf");
        assertThat(invoice.sizeBytes()).isEqualTo(PDF.length);
        assertThat(list(logbook)).extracting(AttachmentResponse::id).containsExactly(invoice.id());
        assertThat(vehicles.logbook(logbook.owner, logbook.household, logbook.vehicle).getFirst().attachmentCount())
                .isEqualTo(1);
        assertThat(download.body()).isEqualTo(PDF);
        assertThat(download.getContentType()).contains(MediaType.APPLICATION_PDF_TYPE);
        assertThat(download.header("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(download.header("Cache-Control")).isEqualTo("private, no-store");
        assertThat(download.header("Content-Disposition"))
                .isEqualTo("attachment; filename=\"facture garage _.pdf\"; filename*=UTF-8''facture%20garage%20%C3%A9.pdf");
    }

    @Test
    void the_real_type_is_checked_whatever_the_name() {
        var logbook = newLogbook();

        assertThat(failure(() -> attach(logbook, "facture.pdf", EXECUTABLE)).code()).isEqualTo(400);
        assertThat(failure(() -> attach(logbook, "vide.pdf", new byte[0])).code()).isEqualTo(400);
        assertThat(list(logbook)).isEmpty();
    }

    @Test
    void a_file_over_10_megabytes_is_refused() {
        var logbook = newLogbook();
        var big = Arrays.copyOf(PDF, 10 * 1024 * 1024 + 1024);

        assertThat(failure(() -> attach(logbook, "gros.pdf", big)).code()).isIn(400, 413);
        assertThat(list(logbook)).isEmpty();
    }

    @Test
    void an_entry_has_at_most_10_attachments() throws IOException {
        var logbook = newLogbook();
        for (int i = 1; i <= 10; i++) {
            attach(logbook, "photo-" + i + ".pdf", PDF);
        }
        long filesBefore = storedFiles();

        assertThat(failure(() -> attach(logbook, "photo-11.pdf", PDF)).code()).isEqualTo(409);
        // Le fichier écrit avant le refus a été supprimé.
        assertThat(storedFiles()).isEqualTo(filesBefore);
    }

    @Test
    void strangers_never_see_attachments() {
        var logbook = newLogbook();
        var stranger = newUser();
        var invoice = attach(logbook, "facture.pdf", PDF);

        assertThat(failure(() -> http.exchange(HttpRequest.GET(attachments(logbook) + "/" + invoice.id() + "/content")
                .bearerAuth(stranger), byte[].class)).code()).isEqualTo(404);
        assertThat(failure(() -> http.exchange(HttpRequest.GET(attachments(logbook)).bearerAuth(stranger)))
                .code()).isEqualTo(404);
    }

    @Test
    void removing_an_attachment_an_entry_or_a_vehicle_deletes_the_files() throws IOException {
        var logbook = newLogbook();
        var invoice = attach(logbook, "facture.pdf", PDF);
        attach(logbook, "photo.pdf", PDF);
        var other = vehicles.record(logbook.owner, logbook.household, logbook.vehicle, oilChange()).id();
        attach(new Logbook(logbook.owner, logbook.household, logbook.vehicle, other), "ticket.pdf", PDF);
        long files = storedFiles();

        http.exchange(HttpRequest.DELETE(attachments(logbook) + "/" + invoice.id()).bearerAuth(logbook.owner));
        assertThat(storedFiles()).isEqualTo(files - 1);
        assertThat(list(logbook)).extracting(AttachmentResponse::fileName).containsExactly("photo.pdf");

        vehicles.deleteEntry(logbook.owner, logbook.household, logbook.vehicle, logbook.entry);
        assertThat(storedFiles()).isEqualTo(files - 2);

        vehicles.remove(logbook.owner, logbook.household, logbook.vehicle);
        assertThat(storedFiles()).isEqualTo(files - 3);
    }

    /** Un foyer, un véhicule et une opération du carnet. */
    private record Logbook(String owner, UUID household, UUID vehicle, UUID entry) {
    }

    private Logbook newLogbook() {
        var owner = newUser();
        var household = households.create(owner, "Maison").id();
        var vehicle = vehicles.register(owner, household, new VehicleRequest("Clio", null, null, null)).id();
        var entry = vehicles.record(owner, household, vehicle, oilChange()).id();
        return new Logbook(owner, household, vehicle, entry);
    }

    private AttachmentResponse attach(Logbook logbook, String fileName, byte[] content) {
        var body = MultipartBody.builder().addPart("file", fileName, MediaType.APPLICATION_PDF_TYPE, content).build();
        var request = HttpRequest.POST(attachments(logbook), body)
                .contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
                .bearerAuth(logbook.owner);
        return http.retrieve(request, AttachmentResponse.class);
    }

    private List<AttachmentResponse> list(Logbook logbook) {
        return http.retrieve(HttpRequest.GET(attachments(logbook)).bearerAuth(logbook.owner),
                Argument.listOf(AttachmentResponse.class));
    }

    private static String attachments(Logbook logbook) {
        return "/api/households/" + logbook.household + "/vehicles/" + logbook.vehicle + "/maintenance/"
                + logbook.entry + "/attachments";
    }

    private static long storedFiles() throws IOException {
        var root = STORAGE_ROOT.resolve("vehicle");
        if (Files.notExists(root)) {
            return 0;
        }
        try (var files = Files.walk(root)) {
            return files.filter(Files::isRegularFile).count();
        }
    }

    private static MaintenanceRequest oilChange() {
        return new MaintenanceRequest(LocalDate.of(2026, 5, 1), "OIL_CHANGE", null, null, null, null);
    }

    private static String newUser() {
        var token = "token-" + UUID.randomUUID();
        KEYCLOAK.activeToken(token, UUID.randomUUID() + "@example.org", "Membre");
        return token;
    }
}
