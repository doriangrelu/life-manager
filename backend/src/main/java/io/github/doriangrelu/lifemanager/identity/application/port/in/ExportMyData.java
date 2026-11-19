package io.github.doriangrelu.lifemanager.identity.application.port.in;

import io.github.doriangrelu.lifemanager.identity.application.port.in.query.GetCurrentUserQuery;

import java.util.Map;

/** Données personnelles de l'utilisateur connecté : son profil et ce qu'il a saisi dans chaque domaine. */
public interface ExportMyData {

    Map<String, Object> export(GetCurrentUserQuery query);
}
