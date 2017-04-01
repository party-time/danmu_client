Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${flashCommonUpdateVbsPath}", 1).readall

checkflashIsOkUrl="http://localhost:8080/v1/api/javaClient/flashIsOk"
checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"


flashUpdateShell = "bash " & "${flashUpdateShellPath}"
flashRollbackShell = "bash " & "${flashRollBakShellPath}"
flashcurrentVersionPath = "${flashCurrentVersionPath}"
operateRequestUrl="${updatePlanCommitUrl}"

resultFilePath="${flashUpdatePlan}"

'Call showDailog("update File Path:" & resultFilePath)
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




version=updatePlanObject.version
'Call showDailog("operateRequestUrl:" & updatePlanObject.updateUpdatePlanPath)

status=updatePlanObject.status
'Call showDailog("update status:" & status)

'Call showDailog("flashcurrentVersionPath:" & flashcurrentVersionPath)
flashcurrentVersion=getFileContent(flashcurrentVersionPath,1)

'Call showDailog("current version:" & flashcurrentVersion)
If updatePlanObject.status="success" OR  version=flashcurrentVersion Then
Call showDailog("the current version is the latest version")
wscript.quit
end If

url = myRequestUrl("start",updatePlanObject,1)
Set requestResult=HttpRequest(url)
code =requestResult.readystate

If code=4 Then
    'Call showDailog("server response content:" & requestResult.responsetext)

    Set  resultObject=ParseJson(requestResult.responsetext)
    If resultObject.result =200 Then
        requestCode=1
        Call setResultToFile("start",requestCode,updatePlanObject)
        Call doUpdateExecute(updatePlanObject)
    END IF

END if

End Function





