package io.github.doriangrelu.lifemanager.budget.domain.valueobject;

import io.github.doriangrelu.lifemanager.kernel.domain.validation.Require;

import java.util.Optional;
import java.util.UUID;

/**
 * Pour qui la dépense a été faite : le foyer dans son ensemble, ou un membre précis.
 * Hiérarchie fermée (« sealed ») : il n'existe que ces deux cas.
 */
public sealed interface Beneficiary {

    /** Dépense commune à tout le foyer. */
    record Shared() implements Beneficiary {
    }

    /** Dépense faite pour un membre en particulier. */
    record Member(MemberId memberId) implements Beneficiary {

        public Member {
            Require.notNull(memberId, "Le bénéficiaire");
        }
    }

    Beneficiary SHARED = new Shared();

    /** Convention d'échange : pas d'identifiant = dépense commune. */
    static Beneficiary of(UUID memberIdOrNull) {
        return memberIdOrNull == null ? SHARED : new Member(new MemberId(memberIdOrNull));
    }

    /** Le membre concerné, ou vide pour une dépense commune. */
    default Optional<MemberId> member() {
        return this instanceof Member(MemberId memberId) ? Optional.of(memberId) : Optional.empty();
    }
}
