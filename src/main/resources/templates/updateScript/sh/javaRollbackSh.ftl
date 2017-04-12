#!/bin/bash

#JAVA VERSION
NEW_JAVA_PATH=${javaNewClietPath}
OLD_JAVA_PATH=${javaBakClietPath}
JAVA_PATH=${javaPath}
IFS=$(echo -en "\n\b")
if [ "`ls -A $OLD_JAVA_PATH`" = "" ]; then
        echo "you could not resotore your client,because your histroy resource is null"
else
    HISTORY_VERSION=$(cat $OLD_JAVA_PATH/version)
    if [ "$HISTORY_VERSION" = "$CURRENT_VERSION" ]; then
        echo "The current version is the same as the previous version"
    else
        cp -r $OLD_JAVA_PATH/* $JAVA_PATH
    fi
fi
#java -jar ${javaJarPath} --spring.config.location=${javaPropertiesPath}
exit 0