Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")
Set wShell=CreateObject("Wscript.Shell")

executeglobal fso.opentextfile("${commvbsPath}", 1).readall



Call javaRollBack

Function javaRollBack()
    versionInfo=getFileContent(flashresultFilePath,1)
    Call showDailog("versionInfo:" & 	versionInfo)

    Set updatePlanObject=ParseJson(versionInfo)
    If updatePlanObject="" Then
        Call showDailog("update plan is null")
        wscript.quit
    End If



    version=getFileContent(flashcurrentVersionPath,1)
    Call showDailog("flashcurrentVersionPath:" & 	flashcurrentVersionPath)
    Call showDailog("current version:" & version)
    Call showDailog("flashbakVersionPath:" & flashbakVersionPath)

    If fso.fileExists(flashbakVersionPath)=False Then
        Call showDailog("backup version not found")
        wscript.quit
    END If

    bakVersion=getFileContent(flashbakVersionPath,1)
    if version = bakVersion Then
        Call showDailog("current version is same as backup version")
        wscript.quit
    end if

    Call doRollBackRequest("rollback",updatePlanObject)
    Call doExecute
End Function

Function doRollBackRequest(param,updatePlanObject)
    url = myRequestUrl(param,updatePlanObject,1)
    Call showDailog("url:" & url)
    Set requestResult=HttpRequest(url)
    code =requestResult.readystate
    If code=4 Then
        Set  resultObject=ParseJson(requestResult.responsetext)
        'MsgBox resultObject.result
        If resultObject.result = 200 Then
            requestCode=1
        Else
            requestCode=0
        End If
    Else
        requestCode=0
    End If
    Call setResultToFile(flashresultFilePath,param,requestCode,updatePlanObject)
End Function

Function doExecute()
    'kill java and flash process
    Call killProcess

    Call showDailog("flash execute rollback")
    'ws.run flashRollbackShell
    Call executeShellFunction(flashRollbackShell)
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

Function checkJavaIsStart()
    strComputer = "."
    Set objWMIService = GetObject("winmgmts:{impersonationLevel=impersonate}!\\" & strComputer & "\root\cimv2")
    Set colProcessList = objWMIService.ExecQuery("Select * from Win32_Process Where Name = 'java.exe'")
    If colProcessList.Count>0 Then
        Set requestContent=HttpRequest(checkJavaIsOkUrl)
        code =requestContent.readystate
        If code=4 Then
            checkJavaIsStart=1
        Else
            checkJavaIsStart=2
        end If
    Else
        checkJavaIsStart=3
    End If
End Function
