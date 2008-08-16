set jvm_dll=%JAVA_HOME%\jre\bin\server\jvm.dll
set lb_home=..
set classpath=%lb_home%\luntbuild-standalone.jar
REM *********************************************************
LuntBuild.exe -install LuntBuild %jvm_dll% -Djava.class.path="%classpath%" -Xms512m -Xmx1024m -start com.luntsys.luntbuild.StandaloneLauncher -params localhost 8888 -out "%lb_home%\logs\stdout.log" -err "%lb_home%\logs\stderr.log" -current %lb_home%