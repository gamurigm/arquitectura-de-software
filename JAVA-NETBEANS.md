# Proyectos Java GR09 en NetBeans

Desde la raíz del repositorio, ejecute:

```powershell
.\Abrir-Proyectos-Java-NetBeans.ps1
```

El acceso abre los 20 proyectos Maven GR09 (los cuatro agregadores y sus 16 módulos) y las cuatro apps Android GR09. NetBeans usa JDK 25 para Maven y Payara; las apps Gradle Android seleccionan JDK 21 mediante `JDK_21`. El script registra ambas plataformas en NetBeans 31, configura la misma caché `tools/maven-repo` para el modelo Maven del editor y sus ejecuciones, escribe los `local.properties` Android ignorados por Git y asigna el cliente REST de escritorio sin BDD como proyecto principal para F6 / Play. Si NetBeans ya estaba abierto con otra caché Maven, vuelva a abrirlo para cargar esa configuración.

## Ver los proyectos en NetBeans

Use **Projects** (`Ctrl+1`) para ver los nodos lógicos de proyecto y su tipo Maven o Gradle. **Files** (`Ctrl+2`) muestra las carpetas y archivos físicos, como en el árbol de la captura; allí no se ven los iconos ni las acciones de proyecto. Los proyectos existentes se abren desde sus carpetas dentro del repositorio. `Documents\NetBeansProjects` es la carpeta predeterminada del asistente para crear proyectos nuevos; NetBeans no exige mover allí los proyectos existentes.

## Ejecutar

- En los módulos Maven de consola y escritorio, **Run Project** ejecuta la clase principal configurada para cada cliente. El botón global F6 / Play ejecuta el cliente de escritorio REST sin BDD seleccionado como proyecto principal.
- El servidor REST de Conversiones usa **Run Project** para `spring-boot:run`.
- Los cuatro proyectos Maven de nivel superior son agregadores `pom`, por lo que no tienen una aplicación que el botón Play pueda arrancar. Cada módulo hijo se abre también como proyecto Maven independiente. Para usar F6 / Play con otro proyecto, establézcalo como **Set as Main Project** o usa **Run Project** desde su menú contextual.
- Los siete WAR Jakarta EE usan Payara 7, dominio `gr09-payara`, puertos HTTP `8080` y admin `14848`, con JDK 25. Si se usa otro perfil de NetBeans, regístrelo desde **Tools > Servers > Add Server > Payara Server** con la instalación `tools/payara7`, ese dominio y JDK 25. Los POM de esos WAR apuntan al identificador Payara `pfv5ee8`.
- Para una app Android, conecte un teléfono con depuración USB o inicie el emulador preparado con `.\Iniciar-Emulador-Android-GR09.ps1`. **Run Project** instala y abre el APK debug; **Build Project** genera el APK. Las acciones predeterminadas se definen mediante `action.run.args`, `action.build.args`, `action.test.args`, `action.clean.args` y `action.rebuild.args` en `gradle.properties`. Mantenga un único dispositivo conectado durante la ejecución.

Los servidores EurekaBank SOAP y REST requieren `U1/TG.1/.env` con su configuración local de base de datos. Mantenga ese archivo fuera de Git. La conexión al banco es independiente del proyecto de conversión de unidades.
