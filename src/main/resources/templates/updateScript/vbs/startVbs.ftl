Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")


executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${updateCheckVbsPath}", 1).readall

Call doStart()
Function doStart
    If doUpdateCheck(javaresultFilePath,1,0)= True Then
        ExecuteShellFunction("${timerjavaUpdateVbsPath}")
    ElseIf doUpdateCheck(flashresultFilePath,1,1)=True Then
        ExecuteShellFunction("${timerflashUpdateVbsPath}")
    Else
        ws.run "cmd /c ${javaStartBatPath}",vbhide
    End If
End Function

