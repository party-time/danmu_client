Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")
'ws.run "cmd /c ${javaStartBatPath}",vbhide
executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${updateCheckVbsPath}", 1).readall

Function doStart
    If doUpdateCheck(javaresultFilePath,1)= True Then
        ExecuteShellFunction("${timerjavaUpdateVbsPath}")
    ElseIf doUpdateCheck(flashresultFilePath,1)=True Then
        ExecuteShellFunction("${timerflashUpdateVbsPath}")
    Else
        ws.run "cmd /c ${javaStartBatPath}",vbhide
    End If
End Function

