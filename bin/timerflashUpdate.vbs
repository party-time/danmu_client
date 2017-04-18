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
findIsFitForUpdate="http://localhost:8080/v1/api/javaClient/checkIsFitForUpdate/1/"
operateRequestUrl=""
updateDate=format_time(now(),1)
findIsFitForUpdate=findIsFitForUpdate & updateDate
Set requestResult=HttpRequest(findIsFitForUpdate)
code =requestResult.readystate
If code=4 Then
	Set  resultObject=ParseJson(requestResult.responsetext)
	If resultObject.result =200 Then
		'requestCode=1
		if resultObject.data=True Then
			resultFilePath="D:/enterX/version/flash/" & updateDate
			Call doExecute
		End IF
	End IF
End If


