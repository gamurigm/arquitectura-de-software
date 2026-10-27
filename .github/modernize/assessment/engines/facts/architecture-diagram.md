# Architecture Diagram

Arquitectura de la comercializadora de celulares GR09. La solucion usa cuatro clientes, una API REST ASP.NET Core, SQL Server y una integracion HTTP independiente con BanQuito.

## Application Architecture

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart TD
    subgraph ClientLayer["Client Layer"]
        Console["Console client"]
        Desktop["WPF client"]
        Mobile["MAUI Android client"]
        Browser["ASP.NET MVC web"]
    end
    subgraph AppLayer["Application Layer"]
        Api["ASP.NET Core REST API"]
        Controllers["REST controllers"]
        Services["Application services"]
        Domain["Domain rules"]
    end
    subgraph DataLayer["Data Layer"]
        Contracts["Contracts and DTOs"]
        Database[("SQL Server ComercializadoraCelulares")]
    end
    subgraph ExternalLayer["External Services"]
        Bank["BanQuito REST API"]
    end
    Console -->|"HTTP JSON"| Api
    Desktop -->|"HTTP JSON"| Api
    Mobile -->|"HTTP JSON"| Api
    Browser -->|"HTTP JSON"| Api
    Api --> Controllers
    Controllers --> Services
    Services --> Domain
    Services --> Contracts
    Services -->|"SQL"| Database
    Services -->|"HTTP credit request"| Bank
~~~

### Technology Stack Summary

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Clients | .NET console, WPF, MAUI and ASP.NET MVC | .NET 10 | User interfaces |
| API | ASP.NET Core Web API | .NET 10 | REST endpoints |
| Integration | HttpClient and JSON | .NET 10 | Client and bank communication |
| Database | SQL Server | 2025 | Catalog and invoices |
| Tests | xUnit | .NET 10 | Domain and service verification |

### Data Storage and External Services

`ComercializadoraCelulares` stores catalog and invoices. Credit eligibility, credit creation and amortization belong to the independent BanQuito API. The store keeps remote customer and credit identifiers without cross-database foreign keys.

### Key Architectural Decisions

- MVC is preserved in each client; shared transport is isolated from views.
- The API separates controllers, services, contracts and domain rules.
- Credit integration is HTTP only, so the two databases remain independent.

## Component Relationships

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart LR
    subgraph cPresentation["Presentation"]
        cConsole["Console view"]
        cWpf["WPF view"]
        cMaui["MAUI view"]
        cWeb["MVC views"]
    end
    subgraph cBusiness["Business Logic"]
        cClientController["Client controllers"]
        cApiController["API controllers"]
        cCatalog["Catalog service"]
        cInvoices["Invoice service"]
        cDomain["Domain rules"]
    end
    subgraph cData["Data Access"]
        cContracts["Contracts"]
        cSql["SQL Server"]
    end
    subgraph cInfra["Infrastructure"]
        cHttp["HTTP client"]
        cErrors["Exception handler"]
        cBank["BanQuito API"]
    end
    cConsole --> cClientController
    cWpf --> cClientController
    cMaui --> cClientController
    cWeb --> cClientController
    cClientController --> cHttp
    cHttp --> cApiController
    cApiController --> cCatalog
    cApiController --> cInvoices
    cCatalog --> cContracts
    cInvoices --> cDomain
    cInvoices --> cSql
    cInvoices --> cBank
    cErrors -.-> cApiController
~~~

### Component Inventory

| Component | Layer | Type | Responsibility |
|---|---|---|---|
| Client views | Presentation | Native or Razor views | Capture and show user data |
| Client controllers | Business Logic | C# controllers | Coordinate UI actions |
| API controllers | Business Logic | ASP.NET controllers | Publish REST operations |
| Catalog service | Business Logic | Application service | Catalog CRUD |
| Invoice service | Business Logic | Application service | Sales and invoices |
| Domain rules | Business Logic | Domain component | Price and invoice rules |
| Contracts | Data Access | DTO assembly | Stable request and response shapes |
| SQL Server | Data Access | Relational database | Persistent store |
| HTTP client | Infrastructure | Adapter | Remote API communication |
| Exception handler | Infrastructure | ASP.NET middleware | Consistent error responses |
