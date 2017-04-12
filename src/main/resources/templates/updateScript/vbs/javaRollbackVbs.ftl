Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall
checkJavaIsOkUrl="${checkflashIsOkUrl}"

javaStartBatPath = "${javaStartBatPath}"
javaRollBackShell = "bash " & "${javaRollBakShellPath}"
javacurrentVersionPath = "${javaCurrentVersionPath}"
javabakVersionPath = "${javaBakVersionPath}"
operateRequestUrl="${updatePlanCommitUrl}"

resultFilePath="${javaUpdatePlan}"

Call javaRollBack

Function javaRollBack()

    versionInfo=getFileContent(resultFilePath,1)
    Call showDailog("versionInfo:" & 	versionInfo)

    Set updatePlanObject=ParseJson(versionInfo)
    If updatePlanObject="" Then
        Call showDailog("update plan is null")
        wscript.quit
    End If



    version=getFileContent(javacurrentVersionPath,1)
    Call showDailog("javacurrentVersionPath:" & 	javacurrentVersionPath)
    Call showDailog("current version:" & version)
    Call showDailog("javabakVersionPath:" & javabakVersionPath)

    if fso.fileExists(javabakVersionPath)=False Then
        Call showDailog("backup version not found")
        wscript.quit
    end If

    bakVersion=getFileContent(javabakVersionPath,1)
    if version = bakVersion Then
        Call showDailog("current version is same as backup version")
        wscript.quit
    end if

    if fso.fileExists(javabakVersionPath)=False Then
        Call showDailog("backup version not found")
        wscript.quit
    end If

    bakVersion=getFileContent(javabakVersionPath,1)
    if version = bakVersion Then
        Call showDailog("current version is same as backup version")
        wscript.quit
    end if

    Call doRollBackRequest("rollback",updatePlanObject)
    Call doExecute
End Function

Function doRollBackRequest(param,versionObject)
    url = myRequestUrl(param,versionObject,1)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate
    If code=4 Then
        Set  resultObject=ParseJson(requestResult.responsetext)
        'MsgBox resultObject.result
        If resultObject.result =200 Then
            requestCode=1
        Else
            requestCode=0
        End If
    Else
        requestCode=0
    End If
    Call setResultToFile(param,requestCode,versionObject)
End Function

Function doExecute()
    'kill java and flash process
    Call killProcess

    'execute update shell
    'ws.run javaRollBackShell
    Call executeShellFunction(javaRollBackShell)
    Call executeShellFunction(javaStartBatPath)

    WScript.Sleep 20000
    strComputer = "."

    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'java.exe'")
    If colProcessList.Count>0 Then
        Set requestContent=HttpRequest(checkJavaIsOkUrl)
        code =requestContent.readystate
        If code=4 Then
            'update ok
        Else
            Call showDailog("Java cannot be accessed after rollback")
            wscript.quit
        end If
    Else
        Call showDailog("Java does not start after rollback")
        wscript.quit
    End If
End Function