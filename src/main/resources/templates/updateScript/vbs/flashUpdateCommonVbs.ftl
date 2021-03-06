Function execute(updateType)
    logCommit("execute flash update")
    IF doUpdateCheck(flashresultFilePath,updateType,1)=True Then
        versionInfo=getFileContent(flashresultFilePath,1)
        Set updatePlanObject=ParseJson(versionInfo)
        Call doStart(updatePlanObject)
    End IF
End Function
Function doStart(updatePlanObject)
    version=updatePlanObject.version
    'Call showDailog("operateRequestUrl:" & updatePlanObject.updateUpdatePlanPath)
    status=updatePlanObject.status
    'Call showDailog("update status:" & status)
     logCommit("update status:" & status)

    'Call showDailog("flashcurrentVersionPath:" & flashcurrentVersionPath)
    flashcurrentVersion=getFileContent(flashcurrentVersionPath,1)
    logCommit("flashcurrentVersionPath:" & flashcurrentVersionPath)

    'Call showDailog("current version:" & flashcurrentVersion)
    If updatePlanObject.status="success" OR  version=flashcurrentVersion Then
        Call showDailog("the current version is the latest version")
        logCommit("the current version is the latest version")
        wscript.quit
    end If

    url = myRequestUrl("start",updatePlanObject,1)
    logCommit("url:" & url)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate

    If code=4 Then
        Call showDailog("server response content:" & requestResult.responsetext)
        logCommit("server response content:" & requestResult.responsetext)
        Set  resultObject=ParseJson(requestResult.responsetext)
        If resultObject.result =200 Then
            requestCode=1
            Call setResultToFile(flashresultFilePath,"start",requestCode,updatePlanObject)
        END IF
    END IF
    Call doUpdateExecute(updatePlanObject)
End Function

Function doUpdateExecute(versionObject)

    'Call showDailog("kill java and flash process")
    logCommit("kill java and flash process")

    Call killProcess
    version=versionObject.version
    'Call showDailog("new version:" & version)
    'Call showDailog("execute flash update")
    Call executeFlashUpdate(version)
    'wscript.quit
    WScript.Sleep 20000

    If checkJavaIsStart=1 Then
        Call checkFlashIsStart(versionObject)
    ElseIf checkJavaIsStart=2 Then
        Call showDailog("Java cannot be accessed after update")
        logCommit("Java cannot be accessed after update")

        Call SendFailRequestToServer("error",versionObject)
    ElseIf checkJavaIsStart=3 Then
        Call showDailog("Java does not start after update")
        logCommit("Java does not start after update")

        Call SendFailRequestToServer("error",versionObject)
    End If

End Function

Function executeFlashUpdate(version)

    Call showDailog("flash update shell execute:" & flashUpdateShell &" "& version)
    logCommit("Java does not start after update")

    Call executeShellFunction(flashUpdateShell &" "& version)

    WScript.Sleep 3000

    Call executeShellFunction(javaStartBatPath)
End Function


Function checkJavaIsStart()
    strComputer = "."
    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'java.exe'")

    Call showDailog("java process count is:" & colProcessList.Count)

    If colProcessList.Count>0 Then
        Call showDailog("CheckJavaUpdateUrl:" & checkJavaIsOkUrl)
        Set requestContent=HttpRequest(checkJavaIsOkUrl)
        code =requestContent.readystate
        Call showDailog("local java http request code  is:" & code)

        If code=4 Then
            checkJavaIsStart=1
        Else
            checkJavaIsStart=2
        end If
    Else
        checkJavaIsStart=3
    End If

End Function

Function checkFlashIsStart(versionObject)
    version=versionObject.version
    strComputer = "."
    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'dmMovie.exe'")
    If colProcessList.Count>0 Then

        Set requestResult=HttpRequest(checkflashIsOkUrl)
        code =requestResult.readystate
        If requestResult.readystate=4 Then
            Set  resultObject=ParseJson(requestResult.responsetext)
            If resultObject.data="0" OR  resultObject.data="" Then
                Call showDailog("Flash self-test failed after update")
                Call SendFailRequestToServer("error",versionObject)
            Else
                Call SendSuccessRequestToServer("success",versionObject)
                Call writeContentFile(flashcurrentVersionPath,version)
            End If
        Else
            Call showDailog("Flash self-test failed after update,Java cannot be accessed")
            Call SendFailRequestToServer("error",versionObject)
        End If
    Else
        Call showDailog("flash does not start after update")
        Call SendFailRequestToServer("error",versionObject)
    End If
End Function

Function rollBack()

    Call killProcess

    Call showDailog("flash execute rollback")
    'ws.run flashRollbackShell
    Call executeShellFunction(flashRollbackShell)

    WScript.Sleep 3000

    Call executeShellFunction(javaStartBatPath)
    WScript.Sleep 20000
    If checkJavaIsStart=1 Then
        strComputer = "."
        Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
        Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'dmMovie.exe'")
        If colProcessList.Count>0 Then
            Set requestResult=HttpRequest(checkflashIsOkUrl)
            code =requestResult.readystate
            If requestResult.readystate=4 Then
                Set  resultObject=ParseJson(requestResult.responsetext)
                If resultObject.data="0" OR  resultObject.data="" Then
                    Call showDailog("Flash self-test failed after rollback")
                End If
            Else
                Call showDailog("Flash self-test failed after rollback,Java cannot be accessed")
            End If
        Else
            Call showDailog("flash does not start after rollback")
        End If
    ElseIf checkJavaIsStart=2 Then
        Call showDailog("Java cannot be accessed after rollback")
    ElseIf checkJavaIsStart=3 Then
        Call showDailog("Java does not start after rollback")
    End If
End Function


Function SendFailRequestToServer(param,versionObject)
    url = myRequestUrl(param,versionObject,1)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate
    'if code is 4, then this request is ok
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

    Call setResultToFile(flashresultFilePath,param,requestCode,versionObject)
    Call rollBack
End Function

Function SendSuccessRequestToServer(param,versionObject)
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
    Call setResultToFile(flashresultFilePath,param,requestCode,versionObject)
End Function