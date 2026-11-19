package io.github.doriangrelu.lifemanager.platform.infrastructure.web.version;

import io.micronaut.serde.annotation.Serdeable;

/** Version de l'API, par exemple « v0.12.0 » (« dev » en développement). */
@Serdeable
public record VersionResponse(String version) {
}
