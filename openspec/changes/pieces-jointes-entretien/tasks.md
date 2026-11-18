## 1. Métier et stockage

- [x] 1.1 [vehicle/domain] Value objects `AttachmentId`, `AttachmentFileName`, `AttachmentMediaType`, `FileSize` et agrégat `MaintenanceAttachment` avec son événement de suppression ; vérifié par des tests unitaires (nom avec chemin nettoyé, 0 octet et 10 Mo + 1 refusés)
- [x] 1.2 [vehicle/application/port + infrastructure/out] Port `AttachmentStorage` et adaptateur disque (écriture atomique, arborescence par clé) avec `life-manager.storage.root` ; dossier de dev ignoré par Git ; vérifié par des tests sur un dossier temporaire
- [x] 1.3 [vehicle/infrastructure/out] Migration `V2` (`maintenance_attachment`), repository, mapper, adaptateur ; vérifié par `./mvnw verify`

## 2. Ajout, consultation, suppression

- [x] 2.1 [vehicle/application] Détection du type avec `tika-core` ; use case `AttachFile` (limite de 10, fichier supprimé si la transaction échoue) ; vérifié par des tests d'intégration (PDF accepté, faux PDF refusé, 11e fichier en 409)
- [x] 2.2 [vehicle/application] `ListAttachments`, `DownloadAttachment`, `RemoveAttachment` (fichier supprimé après commit) ; vérifié par des tests d'intégration (contenu identique à l'envoi, 404 hors foyer, fichier absent du disque après suppression)
- [x] 2.3 [vehicle/application] `DeleteMaintenanceEntry` et `RemoveVehicle` suppriment les pièces jointes ; nombre de pièces jointes dans la liste du carnet ; vérifié par des tests d'intégration
- [x] 2.4 [vehicle/infrastructure/in/web] Routes multipart et téléchargement (en-têtes `Content-Disposition`, `nosniff`, `no-store`), limites de taille Micronaut ; vérifié par des tests d'intégration HTTP, dont le refus au-delà de 10 Mo

## 3. PWA

- [x] 3.1 [frontend] Client d'API (envoi multipart, téléchargement en blob) ; vérifié par `bun run check`
- [x] 3.2 [frontend] Pièces jointes dans le carnet : ajout multiple avec progression, liste, ouverture, suppression avec confirmation ; vérifié dans le navigateur, desktop et mobile

## 4. Intégration

- [ ] 4.1 [ci + doc] Build natif vert avec `tika-core` ; `docs/architecture.md` (stockage de fichiers par domaine) ; note ajoutée au design de `deploiement-vps` (volume et sauvegarde des fichiers) ; vérifié par un run CI vert

## Workflow follow-up

- Archiver le change une fois relu et implémenté.
