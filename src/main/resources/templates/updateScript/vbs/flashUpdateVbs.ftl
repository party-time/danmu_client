Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${flashCommonUpdateVbsPath}", 1).readall



'Call showDailog("update File Path:" & flashresultFilePath)
Call doExecute

Function doExecute()
    'Determine whether the update plan exists
    if fso.fileExists(flashresultFilePath)=False Then
        Call showDailog("No update plan found, will exit update......")
        wscript.quit
    end If

    'Get update plan information
    versionInfo=getFileContent(flashresultFilePath,1)

    Set updatePlanObject=ParseJson(versionInfo)
        If updatePlanObject="" Then
        Call showDailog("update plan is null")
        wscript.quit
    End If

    Call doStart(updatePlanObject)

End Function





