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
findIsFitForUpdate="http://localhost:8080/v1/api/javaClient/checkIsFitForUpdate/0/"
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
			resultFilePath="D:/enterX/version/java/" & updateDate
			Call doExecute
		End IF
	End IF
End If
