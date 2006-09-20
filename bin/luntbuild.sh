#!/bin/sh

cd "$INSTALL_PATH"
if [ "X$1" == "X" ]; then
host="localhost"
else
host="$1"
fi
if [ "X$2" == "X" ]; then
port="8080"
else
port="$2"
fi

java -jar luntbuild-standalone.jar $host $port $3
