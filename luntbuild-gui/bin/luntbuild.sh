#!/bin/sh

cd "$INSTALL_PATH"
if [ "X$1" == "X" ]; then
port="9090"
else
port="$1"
fi

java -jar luntbuild-gui-standalone.war $port $2 $3
