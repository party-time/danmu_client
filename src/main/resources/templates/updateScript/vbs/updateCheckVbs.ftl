Function doUpdateCheck(filePath,updateType)
    IF checkFileIsExist(filePath) =True Then
        versionInfo=getFileContent(filePath,1)
        Call showDailog("versionInfo:" & versionInfo)
        Set updatePlanObject=ParseJson(versionInfo)
        If updatePlanObject="" Then
            Call showDailog("update plan is null")
            doUpdateCheck = FALSE
        End If

        updateDate=updatePlanObject.updateDateStr
        nowDateStr=format_time(now(),2)

        Call showDailog("updateDate:" & updateDate)
        IF updateDate <> nowDateStr AND updateType=1 then
            Call showDailog("The update time is not today and can not be updated")
            doUpdateCheck = FALSE
        END IF

        version=updatePlanObject.version
        status=updatePlanObject.status
        flashcurrentVersion=getFileContent(javacurrentVersionPath,1)
        If updatePlanObject.status="success" OR  version=flashcurrentVersion Then
            Call showDailog("the current version is the latest version")
            doUpdateCheck = FALSE
        END IF
    Else
        doUpdateCheck = FALSE
    end IF
End Function