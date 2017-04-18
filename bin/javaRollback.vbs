Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")


executeglobal fso.opentextfile("D:/enterX/bin/common.vbs", 1).readall
checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"
'java rollback shell
javaRollBackShell="bash D:/enterX/bin/javaRollback.sh"

'current java version
javacurrentVersionPath="D:/enterX/java/version"
'bakup java version
javabakVersionPath="D:/enterX/bak/java/version"

findFileNameByVersionUrl="http://localhost:8080/v1/api/javaClient/findFileNameByVersion?type=0&version="
javaNewUpdateVersionFilePath="D:/enterX/version/java/"
operateRequestUrl=""
resultFilePath=""
Call javaRollBack

Function javaRollBack()
	
	version=getFileContent(javacurrentVersionPath,1)
	Call showDailog("current version:" & version)
	
	if fso.fileExists(javabakVersionPath)=False Then
		Call showDailog("backup version not found")
		wscript.quit
	end If
	
	bakVersion=getFileContent(javabakVersionPath,1)
	if version = bakVersion Then
		Call showDailog("current version is same as backup version")
		wscript.quit
	end if
	
	findFileNameByVersionUrl= findFileNameByVersionUrl & version
	Set requestResult=HttpRequest(findFileNameByVersionUrl)
	code =requestResult.readystate
	Call showDailog(requestResult.responsetext)
	If code=4 Then
		Set resultObject=ParseJson(requestResult.responsetext)
		'MsgBox resultObject.result
		If resultObject.result =200 Then
			if resultObject.data="" Then
				Call showDailog("No update plan file found")
			else
				resultFilePath=javaNewUpdateVersionFilePath & resultObject.data
				resultFilePath=javaNewUpdateVersionFilePath & resultObject.data
				if fso.fileExists(resultFilePath)=False Then
					Call showDailog("No update plan file found")
					wscript.quit
				end If
				
				versionInfoStr=getFileContent(resultFilePath,1)
				Call showDailog("versionInfoStr:" & versionInfoStr)
				Set versionInfo=ParseJson(versionInfoStr)
				Call doRollBackRequest("rollback",versionInfo)
				Call doExecute
			end If
		END IF
	Else
		Call showDailog("request local server error")
	end if
		
End Function

Function doRollBackRequest(param,versionObject)
	operateRequestUrl=versionObject.domainName &"/v1/api/javaClient/updateUpdatePlan"
	url = myRequestUrl(param,versionObject)
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
	Call setResultToFile(versionObject.id,versionObject.version,versionObject.machineNum,param,requestCode,versionObject.domainName,versionObject.updateDate)
End Function

Function doExecute()
	'kill java and flash process
	Call killProcess
	
	'execute update shell
	'ws.run javaRollBackShell
	Call executeShellFunction(javaRollBackShell)
	
	WScript.Sleep 10000
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