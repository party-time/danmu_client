@echo off

taskkill /F /IM rsync.exe

cmd /c
${rootPath}
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::${resourceType} ${rsyncScriptPath}