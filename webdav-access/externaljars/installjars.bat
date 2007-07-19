REM This script takes two optional params 
REM  1. to specify the path of the maven executable
REM  2. to specify a directory for the local maven repository (maven's maven.repo.local property)
REM Note that the path should include the path for the 1st param separator in the end
REM These params have been added to facilitate specifying the path to maven in the continuous and nightly build environments
   
call %1mvn install:install-file -DgroupId=slide -DartifactId=slide-webdavlib -Dversion=2.2pre1 -Dpackaging=jar -Dfile=.\jakarta-slide-webdavlib-2.2pre1.jar -Dmaven.repo.local=%2
