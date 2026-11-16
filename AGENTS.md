# AGENTS.md

## Langue

- Toujours répondre à l'utilisateur en **français**.
- Les artefacts OpenSpec sont rédigés en français (voir `openspec/config.yaml`).
- Code : noms en **anglais**, commentaires en **français**, courts et efficaces. L'utilisateur n'est pas à l'aise en anglais : les commentaires doivent rendre le code compréhensible sans traduire les noms.

## Architecture backend

Référence complète : [`docs/architecture.md`](docs/architecture.md). À respecter impérativement :

- Clean architecture par domaine : `domain` (agrégats, entités, value objects, événements), `application` (ports in/out, use cases, services), `infrastructure` (adaptateurs in/out).
- Un package par type d'objet, un package de premier niveau par domaine métier.
- Domaines strictement séparés : code, schéma de base, événements. Un domaine ne voit d'un autre que son package `contract`.
- Agrégats petits, porteurs d'invariants ; objets métier impossibles à construire invalides.
- Un use case = une action ; code commun dans un service applicatif.
- Java moderne, petites classes et méthodes, complexité faible, librairies connues plutôt que du code maison.

## Méthode de travail

- **En cas de doute, demander** à l'utilisateur avant de décider.
- Les changements passent par OpenSpec (`openspec/changes/`) : proposer, faire relire, appliquer, archiver.
- Commiter dès qu'un morceau de travail cohérent est prêt et pousser sur `main` (approuvé par l'utilisateur) ; pas de force-push.
