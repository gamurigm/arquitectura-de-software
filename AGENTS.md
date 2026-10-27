# GR09 project work

## Project layout

- Keep each course project under its existing numbered `U1/TG.1` or `U1/TI.1.1` root.
- Use `01. UML/01. ERS`, `01. UML/02. ECUD`, and `01. UML/03. DIAGRAMAS UML` with numbered case, activity, sequence, class, and architecture diagrams.
- Keep data models under `02. MER/01. CONCEPTUAL`, `02. LOGICO`, and `03. FISICO`; SQL scripts belong in `03. BDD`.
- Use `04. CLICON`, `04. CLIESC`, `04. CLIMOV`, and `04. CLIWEB` for console, desktop, mobile, and web clients. Put reusable client transport and contracts in `04. COMPARTIDO` when the project needs them.
- Keep APIs and servers in `04. SERVIDOR`; keep Word manuals and PowerPoint presentations in `05. DOCUMENTACION`.

## Implementation and delivery

- Use the project's existing MVC layers and native platform entry points. Keep shared business rules and API contracts consistent across equivalent clients.
- Match equivalent screens in wording, flow, validation, error states, and visual identity while adapting controls to each platform.
- Keep editable PowerDesigner sources with their project files and rendered previews. Preserve existing model identities and manual layouts when regenerating.
- Reuse the exact supplied GR09 Word and PowerPoint templates: retain covers, styles, layouts, masters, fonts, tables, and slide structure; change project content and its evidence only.
- Treat other student projects as read-only references. Never stage unrelated local work.
- .NET projects that require a relational database use SQL Server. Store configurable SDKs, tools, downloads, packages, and build outputs on `D:`.
- Verify the affected projects and artifacts before claiming completion. Commit only scoped work; push to `origin/main` only when requested.
