@echo off
REM the classpath is currently hard-coded with absolute path. You need to change them to reflect your
REM environment. Also the parameters need to be changed to point to url of your luntbuild server, your 
REM project and schedule
c:\j2sdk1.4.2_04\bin\java -classpath d:\lunt\cvs\lunt\luntbuild\remoting\samples\classes;d:\lunt\cvs\lunt\luntbuild\remoting\luntbuild-api.jar;d:\lunt\cvs\lunt\luntbuild\remoting\hessian-3.0.8.jar TriggerBuild http://localhost:8081/luntbuild/app?service=hessian testcvs "continuous integration"