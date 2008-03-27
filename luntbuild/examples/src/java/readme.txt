Examples source directory

HelloWorldExtension - demonstrates used of extensions in Luntbuild
   to build use ant extension-test
   the jar stage/extension-test.jar should be copied to <tomcat>/webapp/luntbuild/WEB-INF/lib
   In Builder variables add: myvar=${system.getExtension("HelloWorldExtension").getHelloWorld()} 
   You should see "Hello World! I am an extension!" in Tomcat console
   
   