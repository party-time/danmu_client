@echo off

cmd /c
d:
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::${resourceType} ${rsyncScriptPath}