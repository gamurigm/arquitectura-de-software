# EurekaBank UI and token implementation plan

**Goal:** Give the four EurekaBank variants a consistent visual identity and require valid session tokens for every account operation.

**Architecture:** Keep each project's MVC and transport boundaries. REST uses `Authorization: Bearer`; SOAP carries tokens in the operation contract. Sessions remain in current server-side stores and each client clears local state on logout or 401/session expiry.

**Tech Stack:** Java 25, Payara 7, Jakarta SOAP/REST, .NET, CoreWCF, MAUI, Swing, WPF, JSP/Razor, Android XML.

## Tasks

1. Protect movements and deposits in all four servers; verify token expiry, revocation, and account ownership.
2. Update SOAP contracts/WSDL clients and REST service clients/controllers so all protected calls carry the token and expired sessions clear locally.
3. Align visual tokens (navy, white, turquoise), navigation, forms, tables, and feedback across each platform while preserving native layouts.
4. Inspect diffs and statically trace every protected endpoint and client call; report builds/tests not run.

## Constraints

- Preserve existing uncommitted workspace changes and project structure.
- Payara Server Community 7 only for Java WARs.
- No DB secrets or token values in code, logs, or durable client storage.
- Avoid unrelated BanQuito work in REST .NET.
