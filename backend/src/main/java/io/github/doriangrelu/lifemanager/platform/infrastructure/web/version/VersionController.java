package io.github.doriangrelu.lifemanager.platform.infrastructure.web.version;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

/** Version déployée de l'API (tag Git, fourni au déploiement), lisible sans être connecté. Rien d'autre. */
@Controller("/api/version")
@Secured(SecurityRule.IS_ANONYMOUS)
class VersionController {

    private final VersionResponse version;

    VersionController(@Value("${life-manager.version:dev}") String version) {
        this.version = new VersionResponse(version);
    }

    @Get
    VersionResponse version() {
        return version;
    }
}
