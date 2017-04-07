@echo off

cmd /c
d:
rsync -arvIz --delete --password-file=${rsyncPasswordPath} rsync_user@${serverIp}::initrsync /enterX/initrsync
java -jar ${javaJarPath} -Djava.awt.headless=false --spring.config.location=${javaPropertiesPath}