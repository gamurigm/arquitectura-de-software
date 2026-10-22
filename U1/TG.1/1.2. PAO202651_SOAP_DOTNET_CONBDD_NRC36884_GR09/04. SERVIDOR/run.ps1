. (Join-Path $PSScriptRoot '..\CargarVariables.ps1')
if ([string]::IsNullOrWhiteSpace($env:EUREKABANK_DB_CONNECTION_STRING)) {
    throw 'Define EUREKABANK_DB_CONNECTION_STRING en .env o en el entorno de PowerShell.'
}
dotnet run --project (Join-Path $PSScriptRoot 'EurekaBank.SoapServer\EurekaBank.SoapServer.csproj')
