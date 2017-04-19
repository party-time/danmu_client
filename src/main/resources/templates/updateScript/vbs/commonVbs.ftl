
checkflashIsOkUrl="${checkflashIsOkUrl}"
checkJavaIsOkUrl="${checkJavaIsOkUrl}"
javaresultFilePath="${javaUpdatePlan}"
flashresultFilePath="${flashUpdatePlan}"
operateRequestUrl="${updatePlanCommitUrl}"

javaStartBatPath = "${javaStartBatPath}"
javaRollBackShell = "bash " & "${javaRollBakShellPath}"
javaUpdateShell = "bash " & "${javaUpdateShellPath}"
javacurrentVersionPath = "${javaCurrentVersionPath}"
javabakVersionPath = "${javaBakVersionPath}"
flashRollbackShell = "bash " & "${flashRollBakShellPath}"
flashUpdateShell = "bash " & "${flashUpdateShellPath}"
flashcurrentVersionPath = "${flashCurrentVersionPath}"
flashbakVersionPath = "${flashBakVersionPath}"
logUrlPath="${logUrlPath}"
addressId="${addressId}"
machineNumber="${machineNumber}"

Function checkFileIsExist(path)
    'Determine whether the update plan exists
    If fso.fileExists(path)=False Then
        Call showDailog("No update plan found, will exit update......")
        checkFileIsExist = False
    Else
        checkFileIsExist = True
    End If
End Function

Function showDailog(message)
    flg=false
    if flg=true then
    ws.Popup message,3
    end if
End Function

Function ExecuteShellFunction(shellContent)
    Call showDailog("shellContent:" & shellContent)
    ws.run shellContent,vbhide
    'ws.run shellContent
End Function

Function Format_Time(s_Time, n_Flag)
    Dim y, m, d, h, mi, s
    Format_Time = ""
    If IsDate(s_Time) = False Then Exit Function
    y = cstr(year(s_Time))
    m = cstr(month(s_Time))
    If len(m) = 1 Then m = "0" & m
    d = cstr(day(s_Time))
    If len(d) = 1 Then d = "0" & d
    h = cstr(hour(s_Time))
    If len(h) = 1 Then h = "0" & h
    mi = cstr(minute(s_Time))
    If len(mi) = 1 Then mi = "0" & mi
    s = cstr(second(s_Time))
    If len(s) = 1 Then s = "0" & s
    Select Case n_Flag
    Case 1
    Format_Time = y  &"-"& m &"-"& d  &"-"& h  &"-"& mi &"-"& "00"
    Case 2
    Format_Time = y & "-" & m & "-" & d
    Case 3
    ' hh:mm:ss
    Format_Time = h & ":" & mi & ":" & s
    Case 4
    Format_Time = y & "year" & m & "month" & d & "day"
    Case 5
    Format_Time = y & m & d

    End Select
End Function

Function ParseJson(strJson)
    Set window = html.parentWindow
    window.execScript "var json = " & strJson, "JScript"
    Set ParseJson = window.json
End Function

Function HttpRequest(url)
    On Error Resume Next
    http.open "GET", url, False
    http.send
    Set HttpRequest=http
End Function

Function setResultToFile(resultFilePath,status,code,versionObject)
    Set file = fso.OpenTextFile(resultFilePath, 2)
    file.write("{")
    file.Write(Chr(34) & "id" & Chr(34)&":"&Chr(34)& versionObject.id & Chr(34) & ",")
    file.Write(Chr(34) & "version" & Chr(34)&":"&Chr(34)& versionObject.version & Chr(34) & ",")
    file.Write(Chr(34) & "machineNum" & Chr(34)&":"&Chr(34)& versionObject.machineNum & Chr(34) & ",")
    file.Write(Chr(34) & "status" & Chr(34)&":"&Chr(34)& status & Chr(34) & ",")
    file.Write(Chr(34) & "updateDate" & Chr(34)&":"&Chr(34)& versionObject.updateDate & Chr(34) & ",")
    file.Write(Chr(34) & "updateDateStr" & Chr(34)&":"&Chr(34)& versionObject.updateDateStr & Chr(34) & ",")
    file.Write(Chr(34) & "code" & Chr(34)&":"&Chr(34)& code & Chr(34) )
    file.write("}")
    file.close
End Function

Function writeContentFile(filePath,content)
    Set file = fso.OpenTextFile(filePath, 2,true)
    file.write(content)
    file.close
End Function


Function getFileContent(filePath,readOrwrite)
    Set file = fso.OpenTextFile(filePath, readOrwrite, false)
    On Error Resume Next
    readfile=file.readall
    file.close
    Set file=Nothing
    getFileContent=readfile
End Function

Function killProcess()
    ws.run "taskkill /F /IM java.exe" ,vbhide
    ws.run "taskkill /F /IM dmMovie.exe",vbhide
End Function

Function myRequestUrl(param,versionObject,clientType)
    url = operateRequestUrl & "?id=" & versionObject.id
    url = url & "&result=" & param
    url = url & "&machineNum=" & versionObject.machineNum
    url = url & "&type=" & clientType
    myRequestUrl=url
End Function

Function logCommit(content)
    url = logUrlPath & "?addressId=" & addressId & "&param= machineNumber is:" & machineNumber & " vbs content:" & content
    Call showDailog("url:" & url)
    HttpRequest(url)
End Function

Function doRetart
    WScript.Sleep 5000
    ExecuteShellFunction("shutdown -r -t 0")
End Function

