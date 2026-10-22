Option Explicit

Dim app, model, diagram, modelPath, imagePath, client, actor, symbol, point
If WScript.Arguments.Count <> 3 Then
    WScript.Echo "Uso: cscript //NoLogo ajustar_diagrama_clientes.vbs <modelo.oom> <imagen.png> <consola|movil|web>"
    WScript.Quit 2
End If
modelPath = WScript.Arguments(0)
imagePath = WScript.Arguments(1)
client = LCase(WScript.Arguments(2))
Set app = CreateObject("PowerDesigner.Application")
Set model = app.OpenModel(modelPath, 0)
Set diagram = model.UseCaseDiagrams.Item(0)
diagram.AutoLayout

MoveActor "ACTOR_USUARIO", -16000, 0
MoveActor "ACTOR_SOAP", 16000, 0
If client = "consola" Then
    MoveCase "CU01", -9000, 15000
    MoveCase "CU02", -9000, 5000
    MoveCase "CU03", -9000, -5000
    MoveCase "CU04", -9000, -15000
    MoveCase "CU05", 9000, 15000
    MoveCase "CU06", 9000, 3000
    MoveCase "CU07", 9000, -9000
ElseIf client = "escritorio" Then
    MoveCase "CU01", -9000, 16000
    MoveCase "CU02", -9000, 8000
    MoveCase "CU03", -9000, 0
    MoveCase "CU04", -9000, -8000
    MoveCase "CU05", -9000, -16000
    MoveCase "CU06", 9000, 15000
    MoveCase "CU07", 9000, 3000
    MoveCase "CU08", 9000, -9000
ElseIf client = "movil" Then
    MoveCase "CU01", -9000, 12000
    MoveCase "CU02", -9000, 4000
    MoveCase "CU03", -9000, -5000
    MoveCase "CU04", -9000, -14000
    MoveCase "CU05", 9000, 12000
    MoveCase "CU06", 9000, -5000
ElseIf client = "web" Then
    MoveCase "CU01", -9000, 16000
    MoveCase "CU02", -9000, 8000
    MoveCase "CU03", -6000, 0
    MoveCase "CU04", -9000, -8000
    MoveCase "CU05", -9000, -16000
    MoveCase "CU06", 9000, 16000
    MoveCase "CU07", 9000, 5000
    MoveCase "CU08", 9000, -7000
Else
    Err.Raise vbObjectError + 1, "Layout", "Tipo de cliente desconocido: " & client
End If

ReattachLinks model.UseCaseAssociations
ReattachLinks model.Dependencies
StraightenLinks model.UseCaseAssociations
StraightenLinks model.Dependencies
diagram.RedrawAllViews
model.Save modelPath, False, ""
diagram.ExportImage imagePath
model.Close False
WScript.Echo "Diagrama maquetado con PowerDesigner."

Sub MoveActor(actorCode, x, y)
    Dim item, found, actorSymbol, actorPoint
    found = False
    For Each item In model.Actors
        If item.Code = actorCode Then
            Set actorSymbol = item.Symbols.Item(0)
            Set actorPoint = actorSymbol.Position
            actorPoint.x = x
            actorPoint.y = y
            actorSymbol.Position = actorPoint
            found = True
            Exit For
        End If
    Next
    If Not found Then Err.Raise vbObjectError + 2, "MoveActor", "Actor no encontrado: " & actorCode
End Sub

Sub MoveCase(useCaseCode, x, y)
    Dim item, found, caseSymbol, casePoint
    found = False
    For Each item In model.UseCases
        If item.Code = useCaseCode Then
            Set caseSymbol = item.Symbols.Item(0)
            Set casePoint = caseSymbol.Position
            casePoint.x = x
            casePoint.y = y
            caseSymbol.Position = casePoint
            found = True
            Exit For
        End If
    Next
    If Not found Then Err.Raise vbObjectError + 3, "MoveCase", "Caso de uso no encontrado: " & useCaseCode
End Sub

Sub ReattachLinks(theLinks)
    Dim link, oldSymbol, sourceSymbol, destinationSymbol
    For Each link In theLinks
        While link.Symbols.Count > 0
            Set oldSymbol = link.Symbols.Item(0)
            oldSymbol.Delete
        Wend
        Set sourceSymbol = link.Object1.Symbols.Item(0)
        Set destinationSymbol = link.Object2.Symbols.Item(0)
        diagram.AttachLinkObject link, sourceSymbol, destinationSymbol
    Next
End Sub

Sub StraightenLinks(theLinks)
    Dim link, linkSymbol, points
    For Each link In theLinks
        Set linkSymbol = link.Symbols.Item(0)
        Set points = linkSymbol.ListOfPoints
        Do While points.Count > 2
            points.Remove 1
        Loop
        linkSymbol.AutomaticRoutingState = 0
        linkSymbol.CornerStyle = 0
    Next
End Sub
