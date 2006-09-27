#!/bin/sh

cd "$INSTALL_PATH"
if [ "X$1" == "X" ]; then
host="localhost"
else
host="$1"
fi
if [ "X$2" == "X" ]; then
port="8081"
else
port="$2"
fi

java -cp luntbuild-standalone.jar com.luntsys.luntbuild.StandaloneStopper $host $port
