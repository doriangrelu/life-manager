## Why

Retour de Dorian : une opération d'entretien s'accompagne souvent d'une facture, d'un procès-verbal de contrôle technique ou d'une photo. Les garder avec l'opération évite de les chercher le jour d'une revente ou d'un litige avec un garage.

## What Changes

- **Pièces jointes** : un membre peut joindre un ou plusieurs fichiers (PDF, JPEG, PNG, WebP ; 10 Mo maximum par fichier, 10 fichiers maximum par opération) à une opération du carnet d'entretien, les lister, les ouvrir et les supprimer.
- **Sécurité** : le type de fichier est vérifié d'après son contenu, pas seulement son extension ; seuls les membres du foyer peuvent lire les fichiers ; aucun fichier n'est accessible par une URL publique.
- **Stockage** : les fichiers sont enregistrés sur le disque du serveur (un dossier propre au domaine véhicule, configurable), leurs informations (nom d'origine, type, taille) en base. Ils seront compris dans les sauvegardes chiffrées du change `deploiement-vps`.
- **Suppression** : supprimer une opération ou un véhicule supprime aussi ses fichiers.
- **PWA** : dans le carnet, bouton d'ajout de fichiers (appareil photo possible sur mobile), liste des fichiers de chaque opération, ouverture et suppression.

## Capabilities

### New Capabilities

_Aucune._

### Modified Capabilities

- `carnet-entretien` : pièces jointes des opérations ; la suppression d'une opération emporte ses fichiers.

## Impact

- Backend, domaine `vehicle` : nouvel agrégat de pièce jointe, port de stockage de fichiers et son adaptateur disque, migration `V2` du schéma `vehicle`, routes multipart.
- Dépendance : Apache Tika (`tika-core`) pour reconnaître le type d'un fichier d'après son contenu.
- Configuration : dossier de stockage (`life-manager.storage.root`), taille maximale des requêtes.
- `deploiement-vps` (proposé, non implémenté) : volume de fichiers à monter et à inclure dans les sauvegardes, à intégrer à ce change.

## Hors périmètre

- Pièces jointes ailleurs que sur le carnet d'entretien (factures, documents : feature « Documents » de la V2).
- Aperçu ou miniature générés par le serveur, compression d'images.
- Stockage objet (S3) : Dorian a choisi le disque du serveur.
- Antivirus.
