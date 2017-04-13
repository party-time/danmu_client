Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${javaCommonUpdateVbsPath}", 1).readall

Call doExecute
Function doExecute()
    Call showDailog("update File Path:" & javaresultFilePath)
    'Determine whether the update plan exists
    if fso.fileExists(javaresultFilePath)=False Then
        Call showDailog("No update plan found, will exit update......")
        wscript.quit
    end If
    versionInfo=getFileContent(javaresultFilePath,1)
    Call showDailog("versionInfo:" & versionInfo)
    Set updatePlanObject=ParseJson(versionInfo)
    If updatePlanObject="" Then
        Call showDailog("update plan is null")
        wscript.quit
    End If
    Call doStart(updatePlanObject)
End Function





