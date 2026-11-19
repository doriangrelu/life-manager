## 1. Backend

- [x] 1.1 [backend/reminder] Agrégat `ReminderDone` + tests domaine ; migration `reminder/V4`, dépôt
- [x] 1.2 [backend/reminder] « C'est fait » enregistre la ligne ; routes `GET /reminder-history` et `DELETE /reminder-history/{id}` (+ tests d'intégration, cloisonnement)
- [x] 1.3 [backend/reminder] Purge quotidienne au-delà d'un an ; effacement avec le foyer
- [x] 1.4 [backend/platform] `GET /api/version` public, version lue dans `LM_VERSION`

## 2. PWA

- [x] 2.1 [frontend] Section « Historique » de l'écran Rappels (20 lignes, « Voir plus », retrait avec confirmation)
- [x] 2.2 [frontend] Pied de page avec la version (mobile et bureau, clair et sombre)
- [x] 2.3 [frontend] Vérification au navigateur (en plus : le bandeau des rappels se recharge aussitôt après un changement)

## 3. Livraison

- [x] 3.1 [infra] `VITE_APP_VERSION` dans `release.yml` ; `LM_VERSION` dans le déploiement ; parcours natif étendu
- [x] 3.2 [infra/vps] Version v0.12.0 publiée et déployée
