Set ws = CreateObject("Wscript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
Set html = CreateObject("htmlfile")
Set http = CreateObject("Msxml2.ServerXMLHTTP")

executeglobal fso.opentextfile("D:/enterX/bin/common.vbs", 1).readall

checkflashIsOkUrl="http://localhost:8080/v1/api/javaClient/flashIsOk"
checkJavaIsOkUrl="http://localhost:8080/v1/api/javaClient/startOk"

flashUpdateShell="bash D:/enterX/bin/flashUpdate.sh"
flashRollbackShell="bash D:/enterX/bin/flashRollback.sh"

'current flash version
flashcurrentVersionPath="D:/enterX/flash/version"


'bakup java version
flashbakVersionPath="D:/enterX/bak/flash/version"

findFileNameByVersionUrl="http://localhost:8080/v1/api/javaClient/findFileNameByVersion?type=1&version="
flashNewUpdateVersionFilePath="D:/enterX/version/flash/"

operateRequestUrl=""
resultFilePath=""
Call javaRollBack

Function javaRollBack()
	
	version=getFileContent(flashcurrentVersionPath,1)
	Call showDailog("current version:" & version)
	
	if fso.fileExists(flashbakVersionPath)=False Then
		Call showDailog("backup version not found")
		wscript.quit
	end If
	
	bakVersion=getFileContent(flashbakVersionPath,1)
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
				resultFilePath=flashNewUpdateVersionFilePath & resultObject.data
				resultFilePath=flashNewUpdateVersionFilePath & resultObject.data
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
	
	Call showDailog("flash execute rollback")
	'ws.run flashRollbackShell
	Call executeShellFunction(flashRollbackShell)
	
	
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
