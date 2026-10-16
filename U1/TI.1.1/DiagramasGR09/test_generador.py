"""Pruebas reales contra PowerDesigner; los modelos temporales quedan en D:\\tools."""

import os
from pathlib import Path
import re
import struct
import subprocess
import tempfile
import unittest
import xml.etree.ElementTree as ET


BASE = Path(__file__).resolve().parent
CSCRIPT = Path(os.environ["WINDIR"]) / "SysWOW64" / "cscript.exe"
NS = {"o": "object", "c": "collection", "a": "attribute"}
PROYECTOS = [
    f"TI.1.1. PAO202651_{tecnologia}_SINBDD_NRC36884_GR09"
    for tecnologia in ("SOAP_JAVA", "SOAP_DOTNET", "RESTFUL_JAVA", "RESTFUL_DOTNET")
]
CARPETAS = ["01_CASOS_DE_USO", "02_ACTIVIDAD", "03_SECUENCIA", "04_CLASES", "05_ARQUITECTURA"]


def ejecutar(script, *argumentos):
    resultado = subprocess.run(
        [str(CSCRIPT), "//nologo", str(script), *map(str, argumentos)],
        capture_output=True, timeout=180,
    )
    salida = (resultado.stdout + resultado.stderr).decode("mbcs", errors="replace")
    if resultado.returncode or (script.name == "GenerarDiagramas.vbs" and "Generados 20 modelos, 20 proyectos y PNG con 32 px de margen." not in salida):
        raise AssertionError(salida)
    return salida


def objetos(ruta):
    return {
        elemento.findtext("a:Code", namespaces=NS): elemento
        for elemento in ET.parse(ruta).getroot().iter()
        if elemento.get("Id") and elemento.findtext("a:Code", namespaces=NS)
    }


def geometria(ruta):
    return {
        elemento.findtext("a:ObjectID", namespaces=NS): {
            hijo.tag: hijo.text
            for hijo in elemento
            if hijo.tag in {"{attribute}Rect", "{attribute}ListOfPoints", "{attribute}Position"}
        }
        for elemento in ET.parse(ruta).getroot().iter()
        if elemento.get("Id") and elemento.tag.endswith("Symbol")
    }


def dimensiones_png(ruta):
    return struct.unpack(">II", ruta.read_bytes()[16:24])


class GeneradorPowerDesigner(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temporal = tempfile.TemporaryDirectory(prefix="gr09-pd-", dir="D:/tools")
        cls.salida = Path(cls.temporal.name)
        for proyecto in PROYECTOS:
            (cls.salida / proyecto).mkdir()
        cls.log = ejecutar(BASE / "GenerarDiagramas.vbs", cls.salida)

    @classmethod
    def tearDownClass(cls):
        cls.temporal.cleanup()

    def modelos(self):
        return sorted([*self.salida.rglob("GR09_*.oom"), *self.salida.rglob("GR09_*.eam")])

    def test_cinco_tipos_editables_por_proyecto(self):
        self.assertEqual(20, len(self.modelos()))
        self.assertEqual(36, len(list(self.salida.rglob("GR09_*.png"))))
        for carpeta_proyecto in PROYECTOS:
            raiz = self.salida / carpeta_proyecto / "01. UML" / "03. DIAGRAMAS UML"
            for carpeta in CARPETAS:
                archivos = list((raiz / carpeta).glob("GR09_*"))
                modelo = next(a for a in archivos if a.suffix in {".oom", ".eam"})
                respaldo = modelo.with_suffix(".oob" if modelo.suffix == ".oom" else ".eab")
                self.assertTrue(respaldo.is_file())
                ET.parse(respaldo)
                self.assertEqual(1, sum(a.suffix == ".prj" for a in archivos))
                archivo_proyecto = next(a for a in archivos if a.suffix == ".prj")
                self.assertTrue(archivo_proyecto.with_suffix(".pjb").is_file())
                ET.parse(archivo_proyecto.with_suffix(".pjb"))
            self.assertEqual([
                "GR09_Actividad_Convertir unidades.png",
                "GR09_Actividad_Iniciar sesión.png",
            ], sorted(a.name for a in (raiz / "02_ACTIVIDAD").glob("GR09_*.png")))
            self.assertEqual([
                "GR09_Secuencia_Convertir longitud.png",
                "GR09_Secuencia_Convertir masa.png",
                "GR09_Secuencia_Convertir temperatura.png",
                "GR09_Secuencia_Iniciar sesión.png",
            ], sorted(a.name for a in (raiz / "03_SECUENCIA").glob("GR09_*.png")))
        for modelo in self.modelos():
            imagenes = list(modelo.parent.glob(modelo.stem + "_*.png"))
            self.assertGreaterEqual(len(imagenes), 1, modelo)
            if "03_SECUENCIA" in str(modelo):
                self.assertIn("[secuencia-v1]", modelo.read_text(encoding="utf-8"))
                for codigo in ("GR09_DS_Clave", "GR09_DS_Logout", "GR09_DS_Catalogo"):
                    self.assertNotIn(codigo, modelo.read_text(encoding="utf-8"))
            if "02_ACTIVIDAD" in str(modelo):
                self.assertNotIn("GR09_DA_Clave", modelo.read_text(encoding="utf-8"))
            raiz = ET.parse(modelo).getroot()
            simbolos = [e for e in raiz.iter() if e.get("Id") and e.tag.endswith("Symbol")]
            self.assertGreater(len(simbolos), 5, modelo)
            self.assertNotIn("CLIWEB", modelo.read_text(encoding="utf-8"))
            ids = [e.get("Id") for e in raiz.iter() if e.get("Id")]
            self.assertEqual(len(ids), len(set(ids)), modelo)
            self.assertTrue(all(e.get("Ref") in ids for e in raiz.iter() if e.get("Ref")), modelo)
        for imagen in self.salida.rglob("GR09_*.png"):
            self.assertEqual(b"\x89PNG\r\n\x1a\n", imagen.read_bytes()[:8], imagen)

    def test_proyectos_con_referencias_portables(self):
        proyectos = list(self.salida.rglob("GR09_*.prj"))
        self.assertEqual(20, len(proyectos))
        for proyecto in proyectos:
            ubicaciones = ET.parse(proyecto).findall(".//o:ModelDocument/a:Location", NS)
            self.assertEqual(1, len(ubicaciones))
            self.assertIn("%[PRJ_ROOT_DIR]%", ubicaciones[0].text)
            self.assertTrue((proyecto.parent / ubicaciones[0].text.rsplit("/", 1)[-1]).is_file())

    def test_migracion_normaliza_dimensiones_y_rutas_legacy(self):
        clases = [m for m in self.modelos() if "04_CLASES" in str(m)]
        legado = next(m for m in clases if "SOAP_JAVA" in str(m))
        pngs_obsoletos = []
        for modelo in clases:
            obsoleto = modelo.parent / "GR09_Clases_Cliente MVC y servidor.png"
            imagen_actual = next(modelo.parent.glob("GR09_Clases_*.png"))
            obsoleto.write_bytes(imagen_actual.read_bytes())
            pngs_obsoletos.append(obsoleto)
        png_referencia = next(self.salida.rglob("GR09_CasosUso_*.png")).read_bytes()
        for proyecto in PROYECTOS:
            diagramas = self.salida / proyecto / "01. UML" / "03. DIAGRAMAS UML"
            obsoleto = diagramas / "01_CASOS_DE_USO" / "casos_de_uso_conversion_unidades.png"
            obsoleto.write_bytes(png_referencia)
            pngs_obsoletos.append(obsoleto)
            for carpeta, nombre in (
                ("02_ACTIVIDAD", "GR09_Actividad_Cambiar contraseña.png"),
                ("03_SECUENCIA", "GR09_Secuencia_Cambiar contraseña.png"),
                ("03_SECUENCIA", "GR09_Secuencia_Cerrar sesión.png"),
                ("03_SECUENCIA", "GR09_Secuencia_Consultar catálogo.png"),
            ):
                obsoleto = diagramas / carpeta / nombre
                obsoleto.write_bytes(png_referencia)
                pngs_obsoletos.append(obsoleto)
        mutar = self.salida / "marcar_legacy.vbs"
        mutar.write_text('''Option Explicit
Dim app, m, d, s, objeto, p
Set app=CreateObject("PowerDesigner.Application")
app.InteractiveMode=2
For Each p In WScript.Arguments
 Set m=app.OpenModel(p,9)
 For Each d In m.ClassDiagrams
  For Each s In d.Symbols
   If TypeName(s)="ClassSymbol" Then
    Set objeto=s.Object
    If objeto.Code="GR09_DC_Remoto" Then
     s.Width=25000
     s.Height=14000
    End If
   End If
  Next
 Next
 m.Comment=Replace(Replace(Replace(m.Comment,"[clases-v2]","[legacy]"),"[clases-v3]","[legacy]"),"[clases-v4]","[legacy]")
 m.Save p
 m.Close False
Next
''', encoding="utf-16")
        ejecutar(mutar, legado)
        ejecutar(BASE / "GenerarDiagramas.vbs", self.salida)
        self.assertTrue(all(not png.exists() for png in pngs_obsoletos))

        firmas = []
        for modelo in clases:
            raiz = ET.parse(modelo).getroot()
            codigos = {
                elemento.get("Id"): elemento.findtext("a:Code", namespaces=NS)
                for elemento in raiz.iter() if elemento.get("Id")
            }
            geometria_clases = {}
            for simbolo in raiz.iter():
                if not simbolo.tag.endswith(("ClassSymbol", "InterfaceSymbol", "AssociationSymbol", "DependencySymbol", "GeneralizationSymbol")):
                    continue
                objeto = simbolo.find("c:Object", NS)
                referencia = next(iter(objeto), None) if objeto is not None else None
                codigo = codigos.get(referencia.get("Ref")) if referencia is not None else None
                if codigo and codigo.startswith("GR09_DC_"):
                    es_enlace = simbolo.tag.endswith(("AssociationSymbol", "DependencySymbol", "GeneralizationSymbol"))
                    geometria_clases[codigo] = (
                        simbolo.tag.split("}")[-1],
                        None if es_enlace else simbolo.findtext("a:Rect", namespaces=NS),
                        simbolo.findtext("a:ListOfPoints", namespaces=NS),
                    )
            firmas.append(geometria_clases)

        for firma in firmas[1:]:
            self.assertEqual(firmas[0].keys(), firma.keys())
            for codigo, (tipo, rect, ruta) in firmas[0].items():
                otro_tipo, otro_rect, otra_ruta = firma[codigo]
                self.assertEqual((tipo, ruta), (otro_tipo, otra_ruta), codigo)
                if rect and otro_rect:
                    coordenadas = list(map(int, re.findall(r"-?\d+", rect)))
                    otras = list(map(int, re.findall(r"-?\d+", otro_rect)))
                    self.assertEqual(len(coordenadas), len(otras), codigo)
                    self.assertTrue(all(abs(a - b) <= 1 for a, b in zip(coordenadas, otras)), codigo)
        rect = firmas[0]["GR09_DC_Remoto"][1]
        coordenadas = list(map(int, re.findall(r"-?\d+", rect or "")))
        # PowerDesigner serializa Rect con ambos extremos inclusivos.
        self.assertEqual((17999, 5999), (abs(coordenadas[2] - coordenadas[0]), abs(coordenadas[3] - coordenadas[1])))

    def test_margen_png_de_32_pixeles(self):
        imagenes = list(self.salida.rglob("GR09_*.png"))
        antes = {imagen: dimensiones_png(imagen) for imagen in imagenes}
        powershell = Path(os.environ["WINDIR"]) / "System32" / "WindowsPowerShell" / "v1.0" / "powershell.exe"
        resultado = subprocess.run(
            [str(powershell), "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(BASE / "AgregarMargenPng.ps1"), "-Raiz", str(self.salida)],
            capture_output=True, timeout=30,
        )
        self.assertEqual(0, resultado.returncode, resultado.stderr.decode("mbcs", errors="replace"))
        for imagen in imagenes:
            ancho, alto = dimensiones_png(imagen)
            self.assertEqual((antes[imagen][0] + 64, antes[imagen][1] + 64), (ancho, alto), imagen)

    def test_nodos_no_se_traslapan(self):
        for modelo in self.modelos():
            if not any(carpeta in str(modelo) for carpeta in ("01_CASOS_DE_USO", "02_ACTIVIDAD", "03_SECUENCIA", "04_CLASES", "05_ARQUITECTURA")):
                continue
            raiz = ET.parse(modelo).getroot()
            objetos_por_id = {
                e.get("Id"): e.findtext("a:Code", namespaces=NS)
                for e in raiz.iter() if e.get("Id")
            }
            for diagrama in raiz.iter():
                if not diagrama.tag.endswith(("ActivityDiagram", "ClassDiagram", "UseCaseDiagram", "SequenceDiagram", "ApplicationArchitectureDiagram")):
                    continue
                simbolos = diagrama.find("c:Symbols", NS)
                cajas = []
                if simbolos is None:
                    continue
                for simbolo in simbolos:
                    if simbolo.find("c:SourceSymbol", NS) is not None:
                        continue
                    if simbolo.tag.endswith("InteractionSymbol"):
                        continue
                    rect = simbolo.findtext("a:Rect", namespaces=NS)
                    coordenadas = list(map(int, re.findall(r"-?\d+", rect or "")))
                    if len(coordenadas) == 4:
                        x1, y1, x2, y2 = coordenadas
                        objeto = simbolo.find("c:Object", NS)
                        ref = next(iter(objeto), None) if objeto is not None else None
                        codigo = objetos_por_id.get(ref.get("Ref")) if ref is not None else simbolo.tag.split("}")[-1]
                        cajas.append((min(x1, x2), min(y1, y2), max(x1, x2), max(y1, y2), codigo))
                for i, (x1, y1, x2, y2, codigo) in enumerate(cajas):
                    for x3, y3, x4, y4, otro_codigo in cajas[i + 1:]:
                        traslape_x = min(x2, x4) > max(x1, x3)
                        traslape_y = min(y2, y4) > max(y1, y3)
                        self.assertFalse(
                            traslape_x and traslape_y,
                            f"{modelo}: {diagrama.findtext('a:Name', namespaces=NS)}: {codigo} / {otro_codigo}",
                        )

    def test_contenido_funcional_y_variantes(self):
        self.assertEqual(20, len(self.modelos()))
        for modelo in self.modelos():
            contenido = modelo.read_text(encoding="utf-8")
            if modelo.suffix == ".eam":
                self.assertIn("Cliente MVC", contenido)
                self.assertIn("IConversor", contenido)
                self.assertIn("IConversor: longitud, masa y temperatura", contenido)
                self.assertIn("[arquitectura-v7]", contenido)
                raiz = ET.parse(modelo).getroot()
                codigos_por_id = {
                    e.get("Id"): e.findtext("a:Code", namespaces=NS)
                    for e in raiz.iter() if e.get("Id")
                }
                rutas = {}
                rects = {}
                for simbolo in raiz.iter():
                    if not simbolo.tag.endswith("ApplicationLinkSymbol"):
                        if not simbolo.tag.endswith("EnterpriseApplicationSymbol"):
                            continue
                    objeto = simbolo.find("c:Object", NS)
                    referencia = next(iter(objeto), None) if objeto is not None else None
                    codigo = codigos_por_id.get(referencia.get("Ref")) if referencia is not None else None
                    if codigo:
                        if simbolo.tag.endswith("ApplicationLinkSymbol"):
                            rutas[codigo] = simbolo.findtext("a:ListOfPoints", namespaces=NS)
                        else:
                            rects[codigo] = simbolo.findtext("a:Rect", namespaces=NS)
                self.assertEqual("((-14400,-29400), (-9601,-24601))", rects["GR09_ARQ_Conversores"])
                self.assertEqual({
                    "GR09_ARQ_ClienteVista": "((-30000,-2500),(-30000,-4000),(-42000,-4000),(-42000,-6500))",
                    "GR09_ARQ_VistaControlador": "((-39500,-9000),(-32500,-9000))",
                    "GR09_ARQ_ControladorModelo": "((-27500,-9000),(-20500,-9000))",
                    "GR09_ARQ_ControladorAdaptador": "((-30000,-11500),(-30000,-15500))",
                    "GR09_ARQ_Transporte": "((-27500,-18000),(0,-18000),(0,-9000),(3500,-9000))",
                    "GR09_ARQ_ServidorAPI": "((6000,-11500),(6000,-15500))",
                    "GR09_ARQ_APIServicios": "((6000,-20500),(6000,-24500))",
                    "GR09_ARQ_APISeguridad": "((8500,-18000),(15500,-18000))",
                    "GR09_ARQ_SeguridadTokens": "((20500,-18000),(27500,-18000))",
                    "GR09_ARQ_ServiciosConversores": "((3500,-27000),(-9500,-27000))",
                }, {codigo: rutas.get(codigo) for codigo in (
                    "GR09_ARQ_ClienteVista", "GR09_ARQ_VistaControlador", "GR09_ARQ_ControladorModelo",
                    "GR09_ARQ_ControladorAdaptador", "GR09_ARQ_Transporte", "GR09_ARQ_ServidorAPI",
                    "GR09_ARQ_APIServicios", "GR09_ARQ_APISeguridad", "GR09_ARQ_SeguridadTokens",
                    "GR09_ARQ_ServiciosConversores",
                )})
                self.assertEqual("((-14400,-29400), (-9601,-24601))", rects["GR09_ARQ_Conversores"])
            if "01_CASOS" in str(modelo):
                casos = {
                    e.findtext("a:Code", namespaces=NS): e.findtext("a:Name", namespaces=NS)
                    for e in ET.parse(modelo).getroot().iter()
                    if e.tag.endswith("UseCase") and (e.findtext("a:Code", namespaces=NS) or "").startswith("GR09_CU_")
                }
                self.assertEqual({
                    "GR09_CU_Login": "Iniciar Sesión",
                    "GR09_CU_ValidarToken": "Validar Token",
                    "GR09_CU_Conversion": "Convertir Unidad",
                    "GR09_CU_ValidarUnidades": "Validar Unidades",
                    "GR09_CU_Longitud": "Convertir Longitud",
                    "GR09_CU_Masa": "Convertir Masa",
                    "GR09_CU_Temperatura": "Convertir Temperatura",
                }, casos)
                self.assertNotIn("GR09_CU_Catalogo", contenido)
                self.assertNotIn("GR09_CU_Clave", contenido)
                dependencias = {
                    e.findtext("a:Code", namespaces=NS): e
                    for e in ET.parse(modelo).getroot().iter()
                    if e.tag.endswith("Dependency") and (e.findtext("a:Code", namespaces=NS) or "").startswith("GR09_CU_")
                }
                raizm = ET.parse(modelo).getroot()
                codigos_por_id = {
                    e.get("Id"): e.findtext("a:Code", namespaces=NS)
                    for e in raizm.iter() if e.get("Id")
                }
                def extremos(relacion):
                    extremos_relacion = []
                    for nombre in ("Object1", "Object2"):
                        campo = relacion.find(f"c:{nombre}", NS)
                        ref = next(iter(campo), None) if campo is not None else None
                        extremos_relacion.append(codigos_por_id.get(ref.get("Ref")) if ref is not None else None)
                    return tuple(extremos_relacion)
                self.assertEqual("extend", dependencias["GR09_CU_ExtiendeToken"].findtext("a:Stereotype", namespaces=NS))
                self.assertEqual("include", dependencias["GR09_CU_IncluyeToken"].findtext("a:Stereotype", namespaces=NS))
                self.assertEqual("include", dependencias["GR09_CU_IncluyeUnidades"].findtext("a:Stereotype", namespaces=NS))
                self.assertEqual(("GR09_CU_ValidarToken", "GR09_CU_Login"), extremos(dependencias["GR09_CU_ExtiendeToken"]))
                self.assertEqual(("GR09_CU_ValidarToken", "GR09_CU_Conversion"), extremos(dependencias["GR09_CU_IncluyeToken"]))
                self.assertEqual(("GR09_CU_ValidarUnidades", "GR09_CU_Conversion"), extremos(dependencias["GR09_CU_IncluyeUnidades"]))
                simbolos = {}
                for simbolo in raizm.iter():
                    if not simbolo.tag.endswith(("DependencySymbol", "GeneralizationSymbol")):
                        continue
                    objeto = simbolo.find("c:Object", NS)
                    referencia = next(iter(objeto), None) if objeto is not None else None
                    codigo = codigos_por_id.get(referencia.get("Ref")) if referencia is not None else None
                    if codigo:
                        simbolos[codigo] = simbolo.findtext("a:ListOfPoints", namespaces=NS)
                self.assertEqual("((3599,0),(14000,0),(14000,-10000),(22301,-10000))", simbolos["GR09_CU_ExtiendeToken"])
                self.assertEqual("((0,-16301),(0,-14000),(26000,-14000),(26000,-12700))", simbolos["GR09_CU_IncluyeToken"])
                self.assertEqual("((4348,-19000),(21551,-19000))", simbolos["GR09_CU_IncluyeUnidades"])
                generalizaciones = {
                    e.findtext("a:Code", namespaces=NS): e
                    for e in raizm.iter()
                    if e.tag.endswith("Generalization") and (e.findtext("a:Code", namespaces=NS) or "").startswith("GR09_CU_")
                }
                for magnitud in ("Longitud", "Masa", "Temperatura"):
                    self.assertEqual(
                        ("GR09_CU_Conversion", f"GR09_CU_{magnitud}"),
                        extremos(generalizaciones[f"GR09_CU_Generaliza{magnitud}"]),
                    )

    def test_clases_comunes_separan_capas_y_declaran_relaciones(self):
        clases = [m for m in self.modelos() if "04_CLASES" in str(m)]
        esperadas = {
            "GR09_DC_Remoto", "GR09_DC_Servicio", "GR09_DC_IConversor",
            "GR09_DC_ConversorLongitud", "GR09_DC_ConversorMasa", "GR09_DC_ConversorTemperatura",
            "GR09_DC_UnidadLongitud", "GR09_DC_UnidadMasa", "GR09_DC_UnidadTemperatura",
        }
        firma_comun = None
        for modelo in clases:
            raiz = ET.parse(modelo).getroot()
            elementos = objetos(modelo)
            clases_diagrama = {
                codigo for codigo, elemento in elementos.items()
                if codigo.startswith("GR09_DC_") and elemento.tag.endswith(("Class", "Interface"))
            }
            self.assertEqual(esperadas, clases_diagrama, modelo)
            self.assertEqual("ControladorConversion", elementos["GR09_DC_Remoto"].findtext("a:Name", namespaces=NS))
            self.assertEqual("boundary", elementos["GR09_DC_Remoto"].findtext("a:Stereotype", namespaces=NS))
            self.assertEqual("control", elementos["GR09_DC_Servicio"].findtext("a:Stereotype", namespaces=NS))
            self.assertEqual("interface", elementos["GR09_DC_IConversor"].findtext("a:Stereotype", namespaces=NS))
            self.assertEqual("IConversor<TUnidad>", elementos["GR09_DC_IConversor"].findtext("a:Name", namespaces=NS))
            self.assertNotIn("rol MVC", modelo.read_text(encoding="utf-8"))
            self.assertNotIn("FiltroAutenticacion", modelo.read_text(encoding="utf-8"))

            id_a_codigo = {
                elemento.get("Id"): elemento.findtext("a:Code", namespaces=NS)
                for elemento in raiz.iter() if elemento.get("Id")
            }

            def extremos(relacion):
                resultado = []
                for nombre in ("Object1", "Object2"):
                    campo = relacion.find(f"c:{nombre}", NS)
                    referencia = next(iter(campo), None) if campo is not None else None
                    resultado.append(id_a_codigo.get(referencia.get("Ref")) if referencia is not None else None)
                return tuple(resultado)

            for codigo, extremo1, extremo2, tipo in (
                ("GR09_DC_ControladorServicio", "GR09_DC_Remoto", "GR09_DC_Servicio", "Association"),
                ("GR09_DC_ServicioConversor", "GR09_DC_Servicio", "GR09_DC_IConversor", "Association"),
                ("GR09_DC_ImplementaLongitud", "GR09_DC_IConversor", "GR09_DC_ConversorLongitud", "Realization"),
                ("GR09_DC_ImplementaMasa", "GR09_DC_IConversor", "GR09_DC_ConversorMasa", "Realization"),
                ("GR09_DC_ImplementaTemperatura", "GR09_DC_IConversor", "GR09_DC_ConversorTemperatura", "Realization"),
            ):
                relacion = elementos[codigo]
                self.assertTrue(relacion.tag.endswith(tipo), f"{modelo}: {codigo}")
                self.assertEqual({extremo1, extremo2}, set(extremos(relacion)), f"{modelo}: {codigo}")

            self.assertEqual("0..*", elementos["GR09_DC_ControladorServicio"].findtext("a:RoleAMultiplicity", namespaces=NS))
            self.assertEqual("1", elementos["GR09_DC_ControladorServicio"].findtext("a:RoleBMultiplicity", namespaces=NS))
            self.assertEqual("1", elementos["GR09_DC_ServicioConversor"].findtext("a:RoleAMultiplicity", namespaces=NS))
            self.assertEqual("3", elementos["GR09_DC_ServicioConversor"].findtext("a:RoleBMultiplicity", namespaces=NS))

            rutas = {}
            for simbolo in raiz.iter():
                if not simbolo.tag.endswith(("AssociationSymbol", "DependencySymbol", "GeneralizationSymbol")):
                    continue
                objeto = simbolo.find("c:Object", NS)
                referencia = next(iter(objeto), None) if objeto is not None else None
                codigo = id_a_codigo.get(referencia.get("Ref")) if referencia is not None else None
                if codigo and codigo.startswith("GR09_DC_"):
                    rutas[codigo] = simbolo.findtext("a:ListOfPoints", namespaces=NS)
            self.assertEqual({
                "GR09_DC_ControladorServicio": "((-15000,0),(-9000,0))",
                "GR09_DC_ServicioConversor": "((0,-3000),(0,-13000))",
                "GR09_DC_ImplementaLongitud": "((-22000,-31500),(-22000,-23000),(-5000,-23000),(-5000,-19000))",
                "GR09_DC_ImplementaMasa": "((0,-31500),(0,-19000))",
                "GR09_DC_ImplementaTemperatura": "((22000,-31500),(22000,-23000),(5000,-23000),(5000,-19000))",
                "GR09_DC_UsaUnidadLongitud": "((-22000,-36500),(-22000,-47500))",
                "GR09_DC_UsaUnidadMasa": "((0,-36500),(0,-47500))",
                "GR09_DC_UsaUnidadTemperatura": "((22000,-36500),(22000,-47500))",
            }, {codigo: rutas.get(codigo) for codigo in (
                "GR09_DC_ControladorServicio", "GR09_DC_ServicioConversor",
                "GR09_DC_ImplementaLongitud", "GR09_DC_ImplementaMasa", "GR09_DC_ImplementaTemperatura",
                "GR09_DC_UsaUnidadLongitud", "GR09_DC_UsaUnidadMasa", "GR09_DC_UsaUnidadTemperatura",
            )}, modelo)

            for magnitud in ("Longitud", "Masa", "Temperatura"):
                relacion = elementos[f"GR09_DC_UsaUnidad{magnitud}"]
                self.assertTrue(relacion.tag.endswith("Dependency"), f"{modelo}: {magnitud}")
                self.assertEqual({
                    f"GR09_DC_Conversor{magnitud}", f"GR09_DC_Unidad{magnitud}"
                }, set(extremos(relacion)), f"{modelo}: {magnitud}")

            firma = tuple(sorted(
                (codigo, elemento.tag, elemento.findtext("a:Name", namespaces=NS),
                 elemento.findtext("a:Stereotype", namespaces=NS),
                 tuple(sorted(extremos(elemento))) if codigo in {
                     "GR09_DC_ControladorServicio", "GR09_DC_ServicioConversor",
                     "GR09_DC_ImplementaLongitud", "GR09_DC_ImplementaMasa", "GR09_DC_ImplementaTemperatura",
                     "GR09_DC_UsaUnidadLongitud", "GR09_DC_UsaUnidadMasa", "GR09_DC_UsaUnidadTemperatura",
                 } else ())
                for codigo, elemento in elementos.items() if codigo in esperadas or codigo.startswith("GR09_DC_")
            ))
            if firma_comun is None:
                firma_comun = firma
            self.assertEqual(firma_comun, firma, modelo)

    def test_arquitectura_separa_autenticacion_y_almacen_de_tokens(self):
        arquitecturas = [m for m in self.modelos() if m.suffix == ".eam"]
        for modelo in arquitecturas:
            raiz = ET.parse(modelo).getroot()
            elementos = objetos(modelo)
            self.assertEqual("Validación y autenticación", elementos["GR09_ARQ_Seguridad"].findtext("a:Name", namespaces=NS))
            self.assertEqual("Tokens", elementos["GR09_ARQ_Tokens"].findtext("a:Name", namespaces=NS))
            id_a_codigo = {
                elemento.get("Id"): elemento.findtext("a:Code", namespaces=NS)
                for elemento in raiz.iter() if elemento.get("Id")
            }

            def extremos(relacion):
                resultado = []
                for nombre in ("Object1", "Object2"):
                    campo = relacion.find(f"c:{nombre}", NS)
                    referencia = next(iter(campo), None) if campo is not None else None
                    resultado.append(id_a_codigo.get(referencia.get("Ref")) if referencia is not None else None)
                return set(resultado)

            self.assertEqual({"GR09_ARQ_API", "GR09_ARQ_Seguridad"}, extremos(elementos["GR09_ARQ_APISeguridad"]))
            self.assertEqual({"GR09_ARQ_Seguridad", "GR09_ARQ_Tokens"}, extremos(elementos["GR09_ARQ_SeguridadTokens"]))

    def test_regeneracion_conserva_identidad_posiciones_y_elementos_manuales(self):
        modelos = self.modelos()
        self.assertEqual(20, len(modelos))
        mover = self.salida / "mover.vbs"
        mover.write_text('''Option Explicit
Dim app, m, d, s, c, p
Set app=CreateObject("PowerDesigner.Application")
app.InteractiveMode=2
For Each p In WScript.Arguments
 Set m=app.OpenModel(p,9)
 For Each d In m.AllDiagrams
  For Each s In d.Symbols
   If TypeName(s)="TextSymbol" Then
    s.Position=app.NewPoint(s.Position.X+300,s.Position.Y-200)
   ElseIf Not s.IsLink Then
    s.Position=app.NewPoint(s.Position.X+300,s.Position.Y-200)
   End If
  Next
 Next
 m.Name="Nombre modificado para probar actualización"
 m.Save p
 m.Close False
Next
''', encoding="utf-16")
        ejecutar(mover, *modelos)
        identidades = {m: {k: v.findtext("a:ObjectID", namespaces=NS) for k, v in objetos(m).items()} for m in modelos}
        formas = {m: geometria(m) for m in modelos}
        ejecutar(BASE / "GenerarDiagramas.vbs", self.salida)
        for modelo in modelos:
            self.assertEqual(identidades[modelo], {k: v.findtext("a:ObjectID", namespaces=NS) for k, v in objetos(modelo).items()}, modelo)
            self.assertEqual(formas[modelo], geometria(modelo), modelo)
            self.assertNotIn("Nombre modificado para probar", modelo.read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main(verbosity=2)
