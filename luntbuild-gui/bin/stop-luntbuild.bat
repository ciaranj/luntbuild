@echo off

cd /D "$INSTALL_PATH"
if "%1" == "" (
set port="9091"
) else (
set port="%1"
)

java -cp standalone/lib/winstone.jar winstone.tools.WinstoneControl shutdown --port=%port%
