@echo off

cmd /c
d:
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::initrsync /enterX/initrsync
java  -Djava.awt.headless=false -jar ${javaJarPath} --spring.config.location=${javaPropertiesPath}