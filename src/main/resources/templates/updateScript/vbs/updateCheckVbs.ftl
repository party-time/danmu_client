Function doUpdateCheck(filePath,updateType,clientType)
    IF checkFileIsExist(filePath) =True Then
        versionInfo=getFileContent(filePath,1)
        Call showDailog("versionInfo:" & versionInfo)
        Set updatePlanObject=ParseJson(versionInfo)
        If updatePlanObject="" Then
            Call showDailog("update plan is null")
            doUpdateCheck = FALSE
        ELSE
            updateDate=updatePlanObject.updateDateStr
            nowDateStr=format_time(now(),2)
            version=updatePlanObject.version
            status=updatePlanObject.status
            IF clientType=1 Then
                currentVersion=getFileContent(flashcurrentVersionPath,1)
            ELSE
                currentVersion=getFileContent(javacurrentVersionPath,1)
            End If
            Call showDailog("updateDate:" & updateDate)
            IF updateDate <> nowDateStr AND updateType=1 then
                Call showDailog("The update time is not today and can not be updated")
                doUpdateCheck = FALSE
            ElseIf updatePlanObject.status="success" OR  version=currentVersion Then
                Call showDailog("the current version is the latest version")
                doUpdateCheck = FALSE
            ELSE
                doUpdateCheck = True
            End If
        End If
    Else
        doUpdateCheck = FALSE
    end IF
End Function