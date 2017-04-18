Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")

executeglobal fso.opentextfile("D:/enterX/bin/javaUpdateCommon.vbs", 1).readall
executeglobal fso.opentextfile("D:/enterX/bin/common.vbs", 1).readall

checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"
javaUpdateShell="bash D:/enterX/bin/javaUpate.sh"
javaRollBackShell="bash D:/enterX/bin/javaRollback.sh"
javacurrentVersionPath="D:/enterX/java/version"


operateRequestUrl=""
findUpdateFileUrl="http://localhost:8080/v1/api/javaClient/findUpdateFile/0"

'Call doExecute
if findUpdateFile<>"" Then
	resultFilePath="D:/enterX/version/java/" & findUpdateFile
	Call showDailog("update File Path:" & resultFilePath)
	Call doExecute
end if


Function findUpdateFile()
	Set requestResult=HttpRequest(findUpdateFileUrl)
	code =requestResult.readystate
	If code=4 Then
		Set  resultObject=ParseJson(requestResult.responsetext)
		Call showDailog("requestResult.responsetext:" & requestResult.responsetext)
		If resultObject.result=200 Then
			findUpdateFile=resultObject.data
		Else
			Call showDailog("Can not find update file")
			wscript.quit
		End If
	Else
		Call showDailog("Can not find update file")
		wscript.quit
	End If
end Function
