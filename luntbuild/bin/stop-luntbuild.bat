@echo off

cd /D "$INSTALL_PATH"
if "%1" == "" (
set host="localhost"
) else (
set host="%1"
)
if "%2" == "" (
set port="8081"
) else (
set port="%2"
)

java -cp luntbuild-standalone.jar com.luntsys.luntbuild.StandaloneStopper %host% %port%
