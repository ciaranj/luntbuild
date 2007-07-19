# This script takes two optional params 
#  1. to specify the path of the maven executable
#  2. to specify a directory for the local maven repository (maven's maven.repo.local property)
# Note that the path should include the path for the 1st param separator in the end
# These params have been added to facilitate specifying the path to maven in the continuous and nightly build environments
   
${1}mvn install:install-file -DgroupId=slide -DartifactId=slide-webdavlib -Dversion=2.2pre1 -Dpackaging=jar -Dfile=./jakarta-slide-webdavlib-2.2pre1.jar -Dmaven.repo.local=${2}
