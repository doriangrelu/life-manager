package io.github.doriangrelu.lifemanager.identity.application.usecase;

import io.github.doriangrelu.lifemanager.identity.application.port.in.ExportMyData;
import io.github.doriangrelu.lifemanager.identity.application.port.in.query.GetCurrentUserQuery;
import io.github.doriangrelu.lifemanager.identity.application.port.out.UserAccountRepository;
import io.github.doriangrelu.lifemanager.identity.domain.exception.UserAccountNotFoundException;
import io.github.doriangrelu.lifemanager.identity.domain.valueobject.UserId;
import io.github.doriangrelu.lifemanager.kernel.application.port.out.PersonalDataSource;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assemble l'export : profil (domaine identity), puis la part de chaque domaine, dans un ordre fixe.
 * Chaque domaine ne donne que ce que l'utilisateur a saisi (voir {@link PersonalDataSource}).
 */
@Singleton
class ExportMyDataUseCase implements ExportMyData {

    /** Version du format du fichier, à augmenter si sa structure change. */
    private static final int FORMAT_VERSION = 1;

    private final UserAccountRepository accounts;
    private final List<PersonalDataSource> sources;
    private final Clock clock;

    ExportMyDataUseCase(UserAccountRepository accounts, List<PersonalDataSource> sources, Clock clock) {
        this.accounts = accounts;
        this.sources = sources.stream().sorted(Comparator.comparingInt(PersonalDataSource::order)).toList();
        this.clock = clock;
    }

    @Override
    @Transactional(transactionManager = "identity", readOnly = true)
    public Map<String, Object> export(GetCurrentUserQuery query) {
        var id = UserId.of(query.subject());
        var account = accounts.findById(id).orElseThrow(() -> new UserAccountNotFoundException(id));
        var export = new LinkedHashMap<String, Object>();
        export.put("format", FORMAT_VERSION);
        export.put("exporteLe", clock.instant().toString());
        var profile = new LinkedHashMap<String, Object>();
        profile.put("email", account.email().value());
        profile.put("nom", account.displayName().value());
        profile.put("inscritLe", account.registeredAt().toString());
        var terms = account.terms();
        profile.put("conditionsAcceptees", terms == null ? null
                : Map.of("version", terms.version(), "le", terms.acceptedAt().toString()));
        export.put("profil", profile);
        for (var source : sources) {
            export.put(source.section(), source.exportFor(id.value()));
        }
        return export;
    }
}
