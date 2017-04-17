Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${updateCheckVbsPath}", 1).readall
executeglobal fso.opentextfile("${flashCommonUpdateVbsPath}", 1).readall


Call execute(1)





