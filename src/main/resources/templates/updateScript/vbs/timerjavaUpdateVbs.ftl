Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")


executeglobal fso.opentextfile("${commvbsPath}", 1).readall
executeglobal fso.opentextfile("${javaCommonUpdateVbsPath}", 1).readall

checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"

javaUpdateShell = "bash " & "${javaUpdateShellPath}"
javaRollBackShell = "bash " & "${javaRollBakShellPath}"
javacurrentVersionPath = "${javaCurrentVersionPath}"
operateRequestUrl="${updatePlanCommitUrl}"

resultFilePath="${javaUpdatePlan}"

'Call doExecute
Call doExecute

Function doExecute()
    Call showDailog("update File Path:" & resultFilePath)
    'Determine whether the update plan exists
    if fso.fileExists(resultFilePath)=False Then
        Call showDailog("No update plan found, will exit update......")
        wscript.quit
    end If

    versionInfo=getFileContent(resultFilePath,1)
    Call showDailog("versionInfo:" & versionInfo)
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
    version=updatePlanObject.version
    status=updatePlanObject.status

    flashcurrentVersion=getFileContent(javacurrentVersionPath,1)

    If updatePlanObject.status="success" OR  version=flashcurrentVersion Then
        Call showDailog("the current version is the latest version")
        wscript.quit
    end If

    'Send start request command
    url = myRequestUrl("start",updatePlanObject,0)
    Set requestResult=HttpRequest(url)

    code =requestResult.readystate

    If code=4 Then
        Set resultObject=ParseJson(requestResult.responsetext)
        'MsgBox resultObject.result
        If resultObject.result =200 Then
            requestCode=1
            Call setResultToFile("start",requestCode,updatePlanObject)
            Call doUpdateExecute(updatePlanObject)
        END IF
    END if
End Function





