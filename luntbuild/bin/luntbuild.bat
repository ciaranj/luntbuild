@echo off

cd /D "$INSTALL_PATH"
if "%1" == "" (
set host="localhost"
) else (
set host="%1"
)
if "%2" == "" (
set port="8080"
) else (
set port="%2"
)

java -jar luntbuild-standalone.jar %host% %port% %3
