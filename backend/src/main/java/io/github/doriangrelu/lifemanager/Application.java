package io.github.doriangrelu.lifemanager;

import io.micronaut.runtime.Micronaut;

/**
 * Point d'entrée de l'application Life Manager.
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
