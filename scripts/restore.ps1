# Variables
$DB_NAME = "paymybuddy"
$DB_USER = "root"
$DB_PASSWORD = "root"
$BACKUP_FILE = $args[0]

# Vérifier si le fichier de sauvegarde est fourni
if (-not $BACKUP_FILE) {
    Write-Host "Usage: .\restore.ps1 <backup_file.sql>"
    exit 1
}

# Suppression et recréation de la base de données
Write-Host "Suppression et recréation de la base de données $DB_NAME..."
Invoke-Expression "mysql -u $DB_USER --password=`"$DB_PASSWORD`" -e `"DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME;`""

# Vérification de l'existence du fichier avant restauration
if (-Not (Test-Path $BACKUP_FILE)) {
    Write-Host "Erreur : Le fichier $BACKUP_FILE n'existe pas."
    exit 1
}

# Restauration de la base de données
Write-Host "Restauration de la base de données à partir de $BACKUP_FILE..."
Get-Content $BACKUP_FILE | mysql -u $DB_USER --password="$DB_PASSWORD" $DB_NAME

Write-Host "Restauration terminée depuis $BACKUP_FILE"


