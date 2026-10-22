Option Explicit

Dim app, model, diagram, modelPath, imagePath, item, symbol, point
If WScript.Arguments.Count <> 2 Then
    WScript.Echo "Uso: cscript //NoLogo ajustar_secuencia.vbs <modelo.oom> <imagen.png>"
    WScript.Quit 2
End If
modelPath = WScript.Arguments(0)
imagePath = WScript.Arguments(1)
Set app = CreateObject("PowerDesigner.Application")
Set model = app.OpenModel(modelPath, 0)
Set diagram = model.SequenceDiagrams.Item(0)

For Each item In model.Objects
    Set symbol = item.Symbols.Item(0)
    Set point = symbol.Position
    Select Case item.Name
        Case "cliente": point.x = -20000
        Case "adaptador": point.x = 0
        Case "Payara 7": point.x = 20000
    End Select
    point.y = 0
    symbol.Position = point
Next

diagram.RedrawAllViews
diagram.ExportImage imagePath
model.Save modelPath, False, ""
model.Close False
WScript.Echo "Diagrama de secuencia organizado y exportado."
