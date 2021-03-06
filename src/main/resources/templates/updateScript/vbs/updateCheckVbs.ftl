Function doUpdateCheck(filePath,updateType,clientType)
    IF checkFileIsExist(filePath) =True Then
        versionInfo=getFileContent(filePath,1)
        Call showDailog("versionInfo:" & versionInfo)
        Set updatePlanObject=ParseJson(versionInfo)
        If updatePlanObject="" Then
            Call showDailog("update plan is null")
            logCommit("update plan is null")
            doUpdateCheck = FALSE
        ELSE
            updateDate=updatePlanObject.updateDateStr
            nowDateStr=format_time(now(),2)
            version=updatePlanObject.version
            status=updatePlanObject.status
            fileName=""
            IF clientType=1 Then
                fileName=flashNewClientPath & "/" &"flash_version."& version &".tar"
                currentVersion=getFileContent(flashcurrentVersionPath,1)
            ELSE
                fileName=javaNewClientPath & "/" & "danmu_java_client_version."& version &".jar"
                currentVersion=getFileContent(javacurrentVersionPath,1)
            End If
            IF  checkFileIsExist(fileName) =True Then
                Call showDailog("updateDate:" & updateDate)
                IF updateDate <> nowDateStr AND updateType=1 then
                    Call showDailog("The update time is not today and can not be updated")
                    logCommit("The update time is not today and can not be updated,today is" & nowDateStr )
                    doUpdateCheck = FALSE
                ElseIf updatePlanObject.status="success" OR  version=currentVersion Then
                    Call showDailog("the current version is the latest version")
                    logCommit("the current version is the latest version")
                    doUpdateCheck = FALSE
                ELSE
                    doUpdateCheck = True
                End If
            ELSE
                doUpdateCheck = False
                logCommit(fileName & "is not found")
            End IF
        End If
    Else
        logCommit(clientType & "updateFile Is not found")
        doUpdateCheck = FALSE
    end IF
End Function