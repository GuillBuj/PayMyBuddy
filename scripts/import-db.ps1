# Définir le nom de la base de données et le fichier SQL
$DatabaseName = "paymybuddy"
$InputFile = "./database/$DatabaseName.sql"

# Vérifier si le fichier SQL existe
if (-Not (Test-Path $InputFile)) {
    Write-Error "Le fichier SQL $InputFile n'existe pas."
    exit
}

# Créer la base de données
Write-Output "Création de la base de données $DatabaseName..."
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS $DatabaseName"

# Importer le fichier SQL
Write-Output "Importation des données depuis $InputFile..."
Get-Content $InputFile | mysql -u root -p $DatabaseName

Write-Output "Importation terminée avec succès."
