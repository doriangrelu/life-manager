## 1. Backend (domaine reminder)

- [x] 1.1 [backend/reminder] Agrégat `PersonalReminder` (titre, note, date, récurrence, délai ; date de départ gardée pour les récurrences ; « c'est fait ») et value objects (+ tests domaine)
- [x] 1.2 [backend/reminder] Migration `reminder/V3`, dépôt, effacement avec le foyer
- [x] 1.3 [backend/reminder] Use cases et routes `/personal-reminders` (liste, ajout, modification, suppression, `/done`) ; rappels saisis dans `GET /reminders` (+ tests d'intégration)

## 2. PWA

- [x] 2.1 [frontend] Écran « Rappels » dans l'espace Maison (#/rappels) : « Bientôt » (tous les rappels) et rappels saisis
- [x] 2.2 [frontend] Formulaire avec contrôles de saisie et icônes « i » ; « C'est fait », modifier, supprimer
- [x] 2.3 [frontend] Rappels saisis dans le bandeau et les notifications (libellé « Rappel »)
- [x] 2.4 [frontend] Vérification au navigateur

## 3. Livraison

- [x] 3.1 [infra/vps] Version v0.11.0 publiée et déployée
