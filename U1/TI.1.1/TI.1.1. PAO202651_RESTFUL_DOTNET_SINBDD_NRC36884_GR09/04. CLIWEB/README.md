# Cliente web

Cliente ASP.NET Core MVC en .NET 10. Mantiene el token en sesión web, con expiración de 10 minutos, y consume la API por HTTP desde el servidor web.

Desde la raíz del proyecto:

~~~powershell
. .\ConfigurarEntornoDotNet.ps1
& $env:DOTNET_ROOT\dotnet.exe run --project '.\04. CLIWEB\ClienteWeb\ClienteWebGR09\ClienteWebGR09.csproj' --artifacts-path 'D:\tools\GR09-REST-DotNet\artifacts'
~~~

Abre http://127.0.0.1:5082.
