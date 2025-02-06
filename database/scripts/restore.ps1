# Variables
$DB_NAME = "paymybuddy"
$DB_USER = "root"
$DB_PASSWORD = "your_password"
$BACKUP_FILE = $args[0]

# Vérifier si le fichier de sauvegarde est fourni
if (-not $BACKUP_FILE) {
    Write-Host "Usage: .\restore.ps1 <backup_file.sql>"
    exit 1
}

# Commande pour supprimer et recréer la base de données
Write-Host "Suppression et recréation de la base de données $DB_NAME..."
Invoke-Expression "mysql -u $DB_USER --password=$DB_PASSWORD -e `"DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME;`""

# Commande pour restaurer la base de données à partir du fichier de sauvegarde
Write-Host "Restauration de la base de données à partir de $BACKUP_FILE..."
Invoke-Expression "mysql -u $DB_USER --password=$DB_PASSWORD $DB_NAME < $BACKUP_FILE"

Write-Host "Restauration terminée depuis $BACKUP_FILE"

