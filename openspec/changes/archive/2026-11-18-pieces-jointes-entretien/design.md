## Context

Domaine `vehicle` (voir `openspec/specs/carnet-entretien`). Agrégats `Vehicle` et `MaintenanceEntry` ; use cases `RecordMaintenance`, `DeleteMaintenanceEntry`, `RemoveVehicle` (qui supprime aussi le carnet) ; schéma `vehicle`. Dorian a choisi un stockage sur le disque du serveur.

## Goals / Non-Goals

**Goals:**
- Ne jamais servir un fichier à quelqu'un qui n'est pas membre du foyer, ni exposer de chemin disque.
- Ne jamais laisser un fichier sans ligne en base (ou l'inverse) après une erreur.
- Rester découpable : le domaine véhicule a son propre dossier de fichiers.

**Non-Goals:**
- Diffusion par un serveur de fichiers statiques ou des URL signées.

## Decisions

### 1. Agrégat `MaintenanceAttachment`

Agrégat séparé de `MaintenanceEntry` (une opération ne protège aucune règle de ses fichiers, et un ajout de fichier ne doit pas verrouiller l'opération).
- Value objects : `AttachmentId`, `AttachmentFileName` (nom d'origine nettoyé : pas de chemin, pas de caractères de contrôle, 120 caractères au plus), `AttachmentMediaType` (énumération fermée : PDF, JPEG, PNG, WebP), `FileSize` (octets, de 1 à 10 Mo).
- Champs : opération, véhicule, foyer, auteur, date, clé de stockage (UUID choisi par le serveur).
- La limite de 10 fichiers par opération porte sur plusieurs agrégats : vérifiée par le use case (`countByEntry`), `409` au-delà.

### 2. Port de stockage

`AttachmentStorage` (port out du domaine) : `store(key, InputStream)`, `open(key)`, `delete(key)`. Adaptateur `LocalDiskAttachmentStorage` : fichiers sous `${life-manager.storage.root}/vehicle/<2 premiers caractères de la clé>/<clé>`, sans extension ni nom d'origine (aucune interprétation possible du fichier par le serveur). Écriture dans un fichier temporaire puis déplacement atomique.

### 3. Vérification du contenu

Apache Tika (`tika-core`, détection seulement) lit les premiers octets pour déterminer le vrai type ; seuls les quatre types autorisés passent. Alternative écartée : contrôle de l'en-tête `Content-Type` ou de l'extension, fournis par le client et donc falsifiables. Le support natif GraalVM de `tika-core` est à vérifier lors du build natif (sinon : métadonnées de réflexion à ajouter).

### 4. Cohérence fichier / base

- Ajout : le fichier est écrit **avant** la transaction ; si la transaction échoue, le fichier est supprimé (bloc `finally` du use case).
- Suppression (pièce jointe, opération, véhicule) : la ligne est supprimée dans la transaction, le fichier **après le commit** (`@TransactionalEventListener(AFTER_COMMIT)` sur un événement de domaine `MaintenanceAttachmentRemoved`). Un échec de suppression du fichier est journalisé : un fichier orphelin vaut mieux qu'une ligne qui pointe dans le vide.
- `DeleteMaintenanceEntry` et `RemoveVehicle` suppriment aussi les pièces jointes concernées.

### 5. API et HTTP

| Route | Rôle |
|---|---|
| `POST …/maintenance/{entryId}/attachments` (multipart, champ `file`) | ajouter |
| `GET …/maintenance/{entryId}/attachments` | lister |
| `GET …/attachments/{attachmentId}/content` | télécharger |
| `DELETE …/attachments/{attachmentId}` | supprimer |

La liste du carnet inclut le nombre de pièces jointes de chaque opération. Le téléchargement renvoie `Content-Type` (le type vérifié), `Content-Disposition` avec le nom d'origine encodé, `X-Content-Type-Options: nosniff` et `Cache-Control: private, no-store`. Micronaut : `micronaut.server.multipart.max-file-size: 10MB`, `micronaut.server.max-request-size: 11MB`.

### 6. PWA

`<input type="file" multiple accept="application/pdf,image/jpeg,image/png,image/webp">` (sur mobile, le navigateur propose l'appareil photo). Envoi d'un fichier par requête, avec la progression. Ouverture : `fetch` avec le jeton, puis `URL.createObjectURL` dans un nouvel onglet : le jeton ne va jamais dans une URL. Les photos HEIC des iPhone sont converties en JPEG par Safari à l'envoi.

### 7. Configuration et sauvegarde

`life-manager.storage.root` (dev : `./data/files`, ignoré par Git ; tests : dossier temporaire). En production, volume Docker monté et inclus dans les sauvegardes restic : à ajouter au change `deploiement-vps`.

## Risks / Trade-offs

- [Disque plein] → limites par fichier et par opération ; surveillance de l'espace dans `deploiement-vps`.
- [Fichiers orphelins après un échec de suppression] → journalisés ; nettoyage manuel possible plus tard.
- [Compatibilité native de Tika] → vérifiée par le job de build natif de la CI.

## Migration Plan

Migration `V2` du schéma `vehicle` (table `maintenance_attachment`). Aucune donnée existante à reprendre.
