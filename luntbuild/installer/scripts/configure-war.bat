cd /D "$INSTALL_PATH\db"
del /F luntbuild.script
if "$luntbuildDemo" == "yes" (
ren luntbuild.script.demo luntbuild.script
del /F luntbuild.script.base
) else (
ren luntbuild.script.base luntbuild.script
del /F luntbuild.script.demo
)
del /F luntbuild.base
del /F luntbuild.demo
cd /D "$INSTALL_PATH\web"
jar -cvf "$INSTALL_PATH\luntbuild.war" .
cd /D "$INSTALL_PATH"
rmdir /S /Q "$INSTALL_PATH\web"
if "$appServerWebapps" == "" goto nodeploy
REM del /F /S /Q "$appServerWebapps\luntbuild"
rmdir /S /Q "$appServerWebapps\luntbuild"
copy /Y "$INSTALL_PATH\luntbuild.war" "$appServerWebapps\luntbuild.war"
:nodeploy