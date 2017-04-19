@echo off

taskkill /F /IM rsync.exe

cmd /c
d:
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::${resourceType} ${rsyncScriptPath}