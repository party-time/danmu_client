Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${flashCommonUpdateVbsPath}", 1).readall


Call doExecute

Function doExecute()

    'Determine whether the update plan exists
    if fso.fileExists(resultFilePath)=False Then
        Call showDailog("No update plan found, will exit update......")
        wscript.quit
    end If

    'Get update plan information
    versionInfo=getFileContent(resultFilePath,1)

    Set updatePlanObject=ParseJson(versionInfo)
    If updatePlanObject="" Then
        Call showDailog("update plan is null")
        wscript.quit
    End If

    updateDate=updatePlanObject.updateDateStr
    nowDateStr=format_time(now(),2)

    Call showDailog("updateDate:" & updateDate)
    IF updateDate <> nowDateStr then
        Call showDailog("The update time is not today and can not be updated")
        wscript.quit
    END IF
    Call doStart(updatePlanObject)

End Function





