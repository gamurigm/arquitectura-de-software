$cargarVariables = Join-Path $PSScriptRoot 'CargarVariables.ps1'
if (Test-Path -LiteralPath $cargarVariables) {
    . $cargarVariables
}

$api = Join-Path $PSScriptRoot '01. SERVIDOR/BanQuito.Api/BanQuito.Api.csproj'

if (-not [string]::IsNullOrWhiteSpace($env:BANQUITO_DB_CONNECTION_STRING)) {
    $env:ConnectionStrings__BaseDatos = $env:BANQUITO_DB_CONNECTION_STRING
} else {
    $env:ConnectionStrings__BaseDatos = 'Server=.\SQLEXPRESS;Database=BanQuitoCore;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=8'
}

& dotnet run --project $api
exit $LASTEXITCODE
