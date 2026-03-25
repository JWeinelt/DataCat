param(
[string]$AppName = "DataCat",
[string]$MainJar = "launcher.jar",
[string]$MainClass = "de.julianweinelt.databench.launcher.Launcher",
[string]$Version = "1.0.0"
)

$ErrorActionPreference = "Stop"

$root = Resolve-Path "."
$buildDir = "$root\build"
$appDir = "$buildDir\app"
$distDir = "$buildDir\dist"

Write-Host "Cleaning build directory..."
Remove-Item $buildDir -Recurse -Force -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Path $appDir | Out-Null
New-Item -ItemType Directory -Path $distDir | Out-Null

Write-Host "Running Maven build..."
#mvn clean package -DskipTests
echo $appDir

Write-Host "Collecting JAR files..."

$modules = @("launcher","ui","reporter","flow","dbx","server")

foreach ($mod in $modules) {
    $targetDir = "$root\$mod\target"
    $jar = Get-ChildItem -Path $targetDir -Filter "*.jar" | Where-Object { $_.Name -notmatch "sources|javadoc" } | Select-Object -First 1
    if ($jar) {
        $destJar = Join-Path $appDir "$mod.jar"
        Copy-Item $jar.FullName $destJar -Force
        Write-Host "Copied $($jar.Name) to $destJar"
    } else {
        Write-Host "No jar found for module $mod"
    }
}

Write-Host "Running jpackage..."

$args_win = @(
    "--type", "exe",
    "--name", $AppName,
    "--input", $appDir,
    "--main-jar", $MainJar,
    "--main-class", $MainClass,
    "--dest", $distDir,
    "--app-version", $Version,
    "--icon", "favicon.ico",
    "--win-dir-chooser",
    "--win-menu",
    "--win-shortcut",
    "--copyright", "Copyright (c) 2026 Julian Weinelt"
)
$args_win2 = @(
    "--type", "msi",
    "--name", $AppName,
    "--input", $appDir,
    "--main-jar", $MainJar,
    "--main-class", $MainClass,
    "--dest", $distDir,
    "--app-version", $Version,
    "--icon", "favicon.ico",
    "--win-dir-chooser",
    "--win-menu",
    "--win-shortcut",
    "--copyright", "Copyright (c) 2026 Julian Weinelt"
)

$args_lin_deb = @(
    "--type", "deb",
    "--name", $AppName,
    "--input", $appDir,
    "--main-jar", $MainJar,
    "--main-class", $MainClass,
    "--dest", $distDir,
    "--app-version", $Version,
    "--icon", "favicon.ico",
    "--linux-package-name", "datacat",
    "--linux-shortcut",
    "--copyright", "Copyright (c) 2026 Julian Weinelt"
)

$args_lin_rpm = @(
    "--type", "rpm",
    "--name", $AppName,
    "--input", $appDir,
    "--main-jar", $MainJar,
    "--main-class", $MainClass,
    "--dest", $distDir,
    "--app-version", $Version,
    "--icon", "favicon.ico",
    "--linux-package-name", "datacat",
    "--linux-shortcut",
    "--copyright", "Copyright (c) 2026 Julian Weinelt"
)

& jpackage @args_win
& jpackage @args_win2
& jpackage @args_lin_deb
& jpackage @args_lin_rpm

Write-Host ""
Write-Host "Build finished."
Write-Host "Output located in: $distDir"
