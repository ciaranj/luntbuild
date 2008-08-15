#!/bin/sh

cd "$INSTALL_PATH"
if [ "X$1" == "X" ]; then
port="9091"
else
port="$1"
fi

java -cp standalone/lib/winstone.jar winstone.tools.WinstoneControl shutdown --port=$port
