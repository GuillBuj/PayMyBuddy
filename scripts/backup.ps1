# Configurer les paramètres de base
$DB_NAME = "paymybuddy"
$DB_USER = "root"
$DB_PASSWORD = "root"
$BACKUP_DIR = "./backups"
$DATE = Get-Date -Format "yyyyMMdd_HHmmss"

# Créer le répertoire de sauvegarde s'il n'existe pas
If (!(Test-Path -Path $BACKUP_DIR)) {
    New-Item -ItemType Directory -Path $BACKUP_DIR | Out-Null
}

# Construire le chemin de sauvegarde
$BACKUP_FILE = "$BACKUP_DIR/${DB_NAME}_backup_$DATE.sql"

# Exécuter mysqldump pour sauvegarder la base de données
Invoke-Expression "mysqldump -u $DB_USER --password=`"$DB_PASSWORD`" $DB_NAME" | Out-File -Encoding utf8 $BACKUP_FILE

# Vérification et affichage du résultat
If (Test-Path -Path $BACKUP_FILE) {
    Write-Host "Sauvegarde terminée : $BACKUP_FILE"
} Else {
    Write-Host "Erreur : La sauvegarde a échoué."
}
