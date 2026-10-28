. (Join-Path $PSScriptRoot 'CargarVariables.ps1')

if ([string]::IsNullOrWhiteSpace($env:EUREKABANK_DB_CONNECTION_STRING)) {
    throw 'Defina EUREKABANK_DB_CONNECTION_STRING en el .env local común.'
}

$api = Join-Path $PSScriptRoot '04. SERVIDOR/EurekaBank.Api/EurekaBank.Api.csproj'
& dotnet run --project $api --launch-profile EurekaBank.Api
exit $LASTEXITCODE
