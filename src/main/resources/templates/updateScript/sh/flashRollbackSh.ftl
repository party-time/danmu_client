#!/bin/bash

#JAVA VERSION
FLASH_PATH=${flashPath}
NEW_FLASH_PATH=${flashNewClietPath}
OLD_FLASH_PATH=${flashBakClietPath}

echo "current flash version is:"$CURRENT_VERSION
IFS=$(echo -en "\n\b")
if [ "`ls -A $OLD_FLASH_PATH`" = "" ]; then
    echo "you could not resotore your client,because your histroy resource is null"
else
    echo "you will execute flash restore"
    for file in  $FLASH_PATH/*
    do
        fileNameNoSuffix=$(basename $file)
        if [ "$fileNameNoSuffix" = "resource" ]; then
            continue;
        fi
        rm -r $file
    done

    for file in $OLD_FLASH_PATH/*
    do
        fileNameNoSuffix=$(basename $file)
        cp -r $file  $FLASH_PATH
    done
    #java -jar ${javaJarPath} --spring.config.location=${javaPropertiesPath}
fi
exit 0

