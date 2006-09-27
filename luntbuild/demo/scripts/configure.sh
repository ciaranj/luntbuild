#!/bin/sh
cd "$INSTALL_PATH/db"
rm -f luntbuild.script
if [ "$luntbuildDemo" == "yes" ]; then
mv luntbuild.script.demo luntbuild.script
rm -f luntbuild.script.base
else
mv luntbuild.script.base luntbuild.script
rm -f luntbuild.script.demo
fi
rm -f luntbuild.base
rm -f luntbuild.demo
cd "$INSTALL_PATH/web"
jar -cvf "$INSTALL_PATH/luntbuild.war" .
cd "$INSTALL_PATH"
rm -rf "$INSTALL_PATH/web"
if [ "$appServerWebapps" != "" ]; then
rm -rf "$appServerWebapps/luntbuild"
cp -f "$INSTALL_PATH/luntbuild.war" "$appServerWebapps/luntbuild.war"
fi
