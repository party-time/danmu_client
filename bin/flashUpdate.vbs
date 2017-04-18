Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")


executeglobal fso.opentextfile("D:/enterX/bin/flashUpdateCommon.vbs", 1).readall
executeglobal fso.opentextfile("D:/enterX/bin/common.vbs", 1).readall

checkflashIsOkUrl="http://localhost:8080/v1/api/javaClient/flashIsOk"
checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"

flashUpdateShell="bash D:/enterX/bin/flashUpdate.sh"
flashRollbackShell="bash D:/enterX/bin/flashRollback.sh"
flashcurrentVersionPath="D:/enterX/flash/version"
operateRequestUrl=""

findUpdateFileUrl="http://localhost:8080/v1/api/javaClient/findUpdateFile/1"

'Call doExecute
if findUpdateFile<>"" Then
	resultFilePath="D:/enterX/version/flash/" & findUpdateFile
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
