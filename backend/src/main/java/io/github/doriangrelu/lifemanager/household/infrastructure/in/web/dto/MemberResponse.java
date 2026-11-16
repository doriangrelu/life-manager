package io.github.doriangrelu.lifemanager.household.infrastructure.in.web.dto;

import io.github.doriangrelu.lifemanager.household.application.port.in.result.MemberDetails;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.UUID;
/** Un membre dans le détail d'un foyer. */
@Serdeable
public record MemberResponse(UUID memberId, String displayName, Instant joinedAt) {

    public static MemberResponse from(MemberDetails member) {
        return new MemberResponse(member.memberId(), member.displayName(), member.joinedAt());
    }
}
