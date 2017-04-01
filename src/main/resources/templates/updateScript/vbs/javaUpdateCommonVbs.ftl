Function doUpdateExecute(currentVersionObject)
    'Call showDailog("kill java and flash process")
    Call killProcess

    version=currentVersionObject.version
    'Call showDailog("new version:" & version)

    'Call showDailog("execute flash update")
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

    ExecuteShellFunction(javaUpdateShell & " " &version)
End Function

Function SendFailRequestToServer(param,versionObject)

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

    Call setResultToFile(param,requestCode,versionObject)
    Call showDailog("Execute rollback")
    Call rollBack
End Function

Function rollBack()

    Call killProcess
    ws.run javaRollBackShell

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
            wscript.quit
        end If
    Else
        Call showDailog("Java does not start after rollback")
        wscript.quit
    End If
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
    Call setResultToFile(param,requestCode,versionObject)
End Function