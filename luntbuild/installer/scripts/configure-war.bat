cd /D "$INSTALL_PATH\db"
if "$luntbuildDemo" == "yes" (
move demo\* .
del /F luntbuild.script.base
) else (
move empty\* .
)
rmdir /S /Q demo empty
cd /D "$INSTALL_PATH\web"
jar -cvf "$INSTALL_PATH\luntbuild.war" .
cd /D "$INSTALL_PATH"
if "$appServerWebapps" == "" goto nodeploy
if "$appServerWebapps" == "." goto nodeploy
rmdir /S /Q "$appServerWebapps\luntbuild"
copy /Y "$INSTALL_PATH\luntbuild.war" "$appServerWebapps\luntbuild.war"
:nodeploy