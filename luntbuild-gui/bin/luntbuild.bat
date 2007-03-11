@echo off

cd /D "$INSTALL_PATH"
if "%1" == "" (
set port="9090"
) else (
set port="%1"
)

java -jar luntbuild-standalone.war %port% %2 %3
