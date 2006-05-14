#!/bin/sh
cd "$INSTALL_PATH/web"
jar -cvf "$INSTALL_PATH/luntbuild.war" .
cd "$INSTALL_PATH"
rm -rf "$INSTALL_PATH/web"
if [ "$appServerWebapps" != "" ]; then
rm -rf "$appServerWebapps/luntbuild"
cp -f "$INSTALL_PATH/luntbuild.war" "$appServerWebapps/luntbuild.war"
fi
