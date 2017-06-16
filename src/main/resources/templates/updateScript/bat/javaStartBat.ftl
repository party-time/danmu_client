@echo off

taskkill /F /IM java.exe

cmd /c

${rootPath}
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::initrsync /enterX/initrsync
java  -Djava.awt.headless=false -jar ${javaJarPath} --spring.config.location=${javaPropertiesPath}