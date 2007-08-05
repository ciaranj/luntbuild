#!/bin/sh
cd "$INSTALL_PATH/db"
if [ "$luntbuildDemo" = "yes" ]; then
mv demo/* .
else
mv empty/* .
fi
rm -rf demo empty
cd "$INSTALL_PATH/web"
jar -cvf "$INSTALL_PATH/luntbuild.war" .
cd "$INSTALL_PATH"
if [ "$appServerWebapps" != "" ]; then
rm -rf "$appServerWebapps/luntbuild"
cp -f "$INSTALL_PATH/luntbuild.war" "$appServerWebapps/luntbuild.war"
fi
chmod a+x bin/*
