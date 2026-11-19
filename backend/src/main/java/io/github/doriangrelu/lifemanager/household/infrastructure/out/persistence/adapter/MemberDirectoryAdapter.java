package io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.adapter;

import io.github.doriangrelu.lifemanager.household.application.port.out.MemberDirectory;
import io.github.doriangrelu.lifemanager.household.domain.valueobject.MemberId;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.entity.MemberProfileEntity;
import io.github.doriangrelu.lifemanager.household.infrastructure.out.persistence.repository.MemberProfileJdbcRepository;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/** Implémente l'annuaire local des membres avec PostgreSQL (table member_profile). */
@Singleton
class MemberDirectoryAdapter implements MemberDirectory {

    private final MemberProfileJdbcRepository profiles;
    private final Clock clock;

    MemberDirectoryAdapter(MemberProfileJdbcRepository profiles, Clock clock) {
        this.profiles = profiles;
        this.clock = clock;
    }

    @Override
    public Map<MemberId, String> displayNamesOf(Collection<MemberId> members) {
        var ids = members.stream().map(MemberId::value).toList();
        return profiles.findByMemberIdIn(ids).stream()
                .collect(Collectors.toMap(row -> new MemberId(row.memberId()), MemberProfileEntity::displayName));
    }

    @Override
    public void rename(MemberId member, String displayName) {
        var row = new MemberProfileEntity(member.value(), displayName, clock.instant());
        if (profiles.existsById(row.memberId())) {
            profiles.update(row);
        } else {
            profiles.save(row);
        }
    }

    @Override
    public void forget(MemberId member) {
        profiles.deleteById(member.value());
    }
}
