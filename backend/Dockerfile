# Image de production du backend : exécutable natif GraalVM (démarrage rapide, peu de mémoire).
# Construction : docker build -t life-manager-api backend/   (~7 Go de RAM, plusieurs minutes)

FROM ghcr.io/graalvm/native-image-community:25 AS build
WORKDIR /build
COPY .mvn .mvn
COPY mvnw pom.xml checkstyle.xml aot-jar.properties aot-native-image.properties ./
COPY src src
# Le cache Maven survit d'une construction à l'autre (BuildKit).
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q package -Dpackaging=native-image -Dmicronaut.aot.enabled=true -DskipTests \
    && mkdir -p /out/files && cp target/backend /out/

# L'exécutable a besoin de zlib, absente de l'image distroless : on la prend dans Debian de même version.
FROM debian:12-slim AS libs

# Image d'exécution minimale : pas de shell, utilisateur non root (uid 65532).
FROM gcr.io/distroless/cc-debian12:nonroot
COPY --from=libs /usr/lib/x86_64-linux-gnu/libz.so.1 /usr/lib/x86_64-linux-gnu/libz.so.1
COPY --from=build /out/backend /app/backend
# Dossier des pièces jointes, à monter sur un volume sauvegardé.
COPY --from=build --chown=65532:65532 /out/files /data/files
ENV STORAGE_ROOT=/data/files
EXPOSE 8080
ENTRYPOINT ["/app/backend"]
