Documentación de EurekaBank — Grupo 1

01. ERS CLIENTES
  ERS_CLIENTE_CONSOLA_EUREKABANK.docx  Requisitos del cliente de consola
  ERS_CLIENTE_ESCRITORIO_EUREKABANK.docx Requisitos del cliente Swing
  ERS_CLIENTE_MOVIL_EUREKABANK.docx    Requisitos del cliente Android
  ERS_CLIENTE_WEB_EUREKABANK.docx      Requisitos del cliente web
  Los documentos se regeneran desde sus fuentes Markdown mediante
  01. ERS CLIENTES\generar_ers_desde_plantilla.ps1 y la plantilla local.

03. DIAGRAMAS UML
  Cada cliente cuenta con diagramas de casos de uso, actividad, secuencia,
  clases y despliegue. Los archivos .oom se editan en PowerDesigner 16;
  los PNG son exportaciones de esos modelos.
  La guía de generación está en el AGENTS.md de la raíz del proyecto.

Plataforma
  El servicio SOAP se prepara para Payara Server Community 7. Los clientes
  se ejecutan fuera de Payara y consumen el servicio publicado allí.
