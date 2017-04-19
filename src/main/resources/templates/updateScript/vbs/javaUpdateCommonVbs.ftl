
Function execute(updateType)
    logCommit("execute java update")
    If doUpdateCheck(javaresultFilePath,updateType,0) =True Then
        versionInfo=getFileContent(javaresultFilePath,1)
        Set updatePlanObject=ParseJson(versionInfo)
        Call doStart(updatePlanObject)
    End If
End Function


Function doStart(updatePlanObject)
    'Send start request command
    url = myRequestUrl("start",updatePlanObject,0)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate
    If code=4 Then
        Set resultObject=ParseJson(requestResult.responsetext)
        'MsgBox resultObject.result
        If resultObject.result =200 Then
            requestCode=1
            Call setResultToFile(javaresultFilePath,"start",requestCode,updatePlanObject)
        END IF
    END IF
    Call doUpdateExecute(updatePlanObject)
End Function

Function doUpdateExecute(currentVersionObject)
    'Call showDailog("kill java and flash process")
    logCommit("kill java and flash process")
    Call killProcess

    version=currentVersionObject.version
    'Call showDailog("new version:" & version)
    logCommit("new version:" & version)

    'Call showDailog("execute flash update")
    logCommit("execute flash update")
    Call executeUpdateShell(version)

    WScript.Sleep 10000
    strComputer = "."
    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'java.exe'")
    If colProcessList.Count>0 Then
        Set requestContent=HttpRequest(checkJavaIsOkUrl)
        code =requestContent.readystate
        If code=4 Then
            Call SendSuccessRequestToServer("success",currentVersionObject)
            Call writeContentFile(javacurrentVersionPath,version)
            Call doRetart()
        Else
            Call showDailog("Java cannot be accessed after update")
            Call SendFailRequestToServer("error",currentVersionObject)
        end If
    Else
        Call showDailog("Java does not start after update")
        Call SendFailRequestToServer("error",currentVersionObject)
    End If
End Function

Function executeUpdateShell(version)
    'ws.run javaUpdateShell & " " &version
    logCommit(javaUpdateShell & " " &version)
    Call ExecuteShellFunction(javaUpdateShell & " " &version)
    Call executeShellFunction(javaStartBatPath)
End Function

Function SendFailRequestToServer(param,versionObject)
    url = myRequestUrl(param,versionObject,0)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate
    'if code is 4, then this request is ok
    logCommit("url:" & url & "code:" & code)

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

    Call setResultToFile(javaresultFilePath,param,requestCode,versionObject)
    Call showDailog("Execute rollback")

    logCommit("Execute rollback")
    Call rollBack
End Function

Function rollBack()

    Call killProcess
    'ws.run javaRollBackShell

    Call executeShellFunction(javaRollBackShell)
    Call executeShellFunction(javaStartBatPath)

    WScript.Sleep 10000
    strComputer = "."
    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'java.exe'")
    If colProcessList.Count>0 Then
        Set requestContent=HttpRequest(checkJavaIsOkUrl)
        code =requestContent.readystate
        If code=4 Then
        Else
            Call showDailog("Java cannot be accessed after rollback")
        end If
    Else
        Call showDailog("Java does not start after rollback")
    End If
    Call doRetart()
End Function

Function SendSuccessRequestToServer(param,versionObject)
    url = myRequestUrl(param,versionObject,0)
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
    Call setResultToFile(javaresultFilePath,param,requestCode,versionObject)
End Function