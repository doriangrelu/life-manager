package io.github.doriangrelu.lifemanager.kernel.domain.entity;

import java.util.Objects;

/**
 * Objet métier qui a une identité : deux entités sont égales si elles ont le même identifiant,
 * même si leurs autres attributs diffèrent.
 *
 * @param <I> type de l'identifiant (en général un value object)
 */
public abstract class Entity<I> {

    private final I id;

    protected Entity(I id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public I id() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other != null && getClass() == other.getClass() && id.equals(((Entity<?>) other).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
