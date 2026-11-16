package io.github.doriangrelu.lifemanager.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Faux endpoint d'introspection Keycloak pour les tests, basé sur le serveur HTTP du JDK.
 * Un jeton enregistré via {@link #activeToken} est déclaré actif, tout autre jeton inactif.
 */
public final class FakeKeycloak {

    static final String INTROSPECTION_PATH = "/realms/life-manager/protocol/openid-connect/token/introspect";
    private static final String EXPECTED_AUTH = "Basic " + Base64.getEncoder()
            .encodeToString("life-manager-api:dev-api-secret-change-me".getBytes(StandardCharsets.UTF_8));

    private final HttpServer server;
    private final Map<String, String> responses = new ConcurrentHashMap<>();
    private final AtomicInteger calls = new AtomicInteger();
    private volatile boolean available = true;

    FakeKeycloak() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext(INTROSPECTION_PATH, this::introspect);
        server.start();
    }

    public String url() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    /** Enregistre un jeton valide pour l'API et retourne le {@code sub} de son utilisateur. */
    public String activeToken(String token, String email, String name) {
        var subject = UUID.randomUUID().toString();
        respond(token, subject, email, name, "life-manager-api");
        return subject;
    }

    /** Enregistre un jeton actif avec une réponse entièrement choisie (audience, nom…). */
    public void respond(String token, String subject, String email, String name, String audience) {
        responses.put(token, """
                {"active":true,"sub":"%s","email":"%s","name":"%s","aud":["%s","account"]}"""
                .formatted(subject, email, name, audience));
    }

    public int calls() {
        return calls.get();
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private void introspect(HttpExchange exchange) throws IOException {
        calls.incrementAndGet();
        if (!available) {
            send(exchange, 500, "{}");
            return;
        }
        if (!EXPECTED_AUTH.equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
            send(exchange, 401, "{\"error\":\"invalid_client\"}");
            return;
        }
        send(exchange, 200, responses.getOrDefault(tokenOf(exchange), "{\"active\":false}"));
    }

    private static String tokenOf(HttpExchange exchange) throws IOException {
        var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return URLDecoder.decode(body.replaceFirst("^token=", ""), StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange exchange, int status, String json) throws IOException {
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
