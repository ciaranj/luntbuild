
Luntbuild uses open source package IzPack http://www.izforge.com/izpack/ to build its installer.
IzPack documentation is avaialble in http://www.izforge.com/izpack/izpack-doc.pdf, short tutorial
is available in http://www.izforge.com/izpack/izpack-tutorial.pdf.

IzPack installer was incorporated into ant build script build/build.xml. To build the installer:

> cd build
> ant clean installer

The installer is available in distribute directory. To run:

> java -jar luntbuild-1.2-installer.jar

You can also double click on the file on Windows platform.

Installer uses its own copy of files web.xml, applicationContext.xml in installer/data directory.
Those files needs to be kept in sync with its original versions in web/WEB-INF.
Following variables are used in those files for substitution:

web.xml: $INSTALL_PATH, ${sessionTimout}
applicationContext.xml: ${luntbuildPassword}

Directory installer/scripts contains scripts that perform an executable part of the installation.
Those scripts unzip luntbuild.war from distribution and replace the files web.xml, applicationContext.xml
with version containing substituted variables. Then the script creates the new version of luntbuild.war
and optionally deploys the luntbuild.war to selected directory of the application server (servlet container).

Note. IMPORTANT - .sh script(s) is intended to run on Unix platforms, and needs to be edited on Unix.

