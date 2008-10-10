
Webdav Access Project

This project offers Webdav access to user data (filesystem, DB, ...).
It supports both Webdav Servlet and Webdav Client access (class WebdavClient).
Webdab Servlet can be used as standalone or as part of another web application.

Motivation

I have started this project because the original WebDAV-Servlet was not able to
work as part of another web application and because bugs in WebDAV-Servlet.
Version 2.0 of WebDAV-Servlet was completely rewritten, but it is buggy and it was not released yet.

Distribution

webdav-access-1.0-client.jar  - client library and servlet jar. To start servlet in your web application
                                install in <webapp>/WEB_INF/lib library (see other dependencies). You can
                                use the webdav-access-*-install.zip to install all needed libraries.
                                For client access only, use just this jar.
webdav-access-1.0.war         - standalone Webdav Servlet
webdav-access-1.0-install.zip - use to install Webdav Servlet and dependencies in <webapp>/WEB_INF/lib
webdav-access-1.0-project.zip - project zip file with sources
webdav-access-1.0-javadoc.jar - javadoc distribution

Dependencies

	    <dependency>
	      <groupId>jdom</groupId>
	      <artifactId>jdom</artifactId>
	      <version>1.0</version>
	    </dependency>
	    <dependency>
	      <groupId>commons-logging</groupId>
	      <artifactId>commons-logging</artifactId>
	      <version>1.0.4</version>
	    </dependency>
	    <dependency>
	      <groupId>log4j</groupId>
	      <artifactId>log4j</artifactId>
	      <version>1.2.14</version>
	    </dependency>
	  	<dependency>
		    <groupId>commons-httpclient</groupId>
		    <artifactId>commons-httpclient</artifactId>
		    <version>3.0</version>
	  	</dependency>
		<dependency>
			<groupId>tomcat</groupId>
			<artifactId>servlet-api</artifactId>
			<version>5.5.12</version>
			<scope>provided</scope>
		</dependency>
	    <dependency>
	      <groupId>slide</groupId>
	      <artifactId>slide-webdavlib</artifactId>
	      <version>2.2pre1</version>
	    </dependency>

Note, that dependent libraries are available in WEB-INF/lib in 
Note, that slide-webdavlib is available in externaljars directory and should be installed
in local maven repository before build (development).

CREDITS

This project servlet is based on WebDAV-Servlet version 1.2 from
http://sourceforge.net/projects/webdav-servlet/.
Client part is based on Slide client library.

Thank Remy Maucherat for the original webdav-servlet
and the dependent files that come with tomcat,
and Oliver Zeigermann for the slide-WCK.
IWebdavStorage class is modeled after his BasicWebdavStore.
 
WebDAV Servlet
==============

What is it?
  A Servlet that brings basic WebDAV access to any store. Only 1 interface 
  (IWebdavStorage) has to be implemented, an example (LocalFileSystemStorage)
  which uses the local filesystem, is provided.
  Unlike large systems (like slide), this servlet only supports the most basic
  data access options. locking, versioning or user management are not supported

  
REQUIREMENTS

  JDK 1.42 or above
  apache-tomcat 5.0.28 or above

INSTALLATION & CONFIGURATION

  -place the webdav-servlet.jar in the /WEB-INF/lib/ of your webapp
  -open web.xml of the webapp. it needs to contain the following:
  
  	<servlet>
		<servlet-name>webdav</servlet-name>
		<servlet-class>org.webdavaccess.servlet.WebdavServlet</servlet-class>
		<init-param>
			<param-name>webdavStoreImplementation</param-name>
			<param-value>org.webdavaccess.LocalFileSystemStorage</param-value>
			<description>
				name of the class that implements
				org.webdavaccess.IWebdavStorage
			</description>
		</init-param>
		<init-param>
			<param-name>rootpath</param-name>
			<param-value>d:/tmp/</param-value>
			<description>
				place where to store the webdavcontent on the local
				filesystem
			</description>
		</init-param>
		<init-param>
			<param-name>webdavAuthorizationImplementation</param-name>
			<param-value>your.authorization.implementation.class</param-value>
			<description>
				name of the class that implements
				org.webdavaccess.IWebdavAuthorization (optional)
			</description>
		</init-param>
		<init-param>
			<param-name>webdavAliasManagerImplementation</param-name>
			<param-value>your.alias.manager.implementation.class</param-value>
			<description>
				name of the class that implements
				org.webdavaccess.IWebdavAlias (optional)
			</description>
		</init-param>
		<init-param>
			<param-name>servletDebug</param-name>
			<param-value>0</param-value>
			<description>
				triggers debug output of the
				webdavStoreImplementation (0 = off , 1 = on) off by default
			</description>
		</init-param>
		<init-param>
			<param-name>readonly</param-name>
			<param-value>false</param-value>
			<description>
				Makes the Webdav read only
			</description>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>webdav</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>
  
  -if you want to use the reference implementation, set the parameter "rootpath"
   to where you want to store your files.
   
  -if you have implemented your own store, insert the class name
   to the parameter  "webdavStoreImplementation" and copy your .jar to /WEB-INF/lib/

  -if you have implemented your own authorization, insert the class name
   to the parameter  "webdavAuthorizationImplementation" and copy your .jar to /WEB-INF/lib/

  -if you have implemented your own alias manager, insert the class name
   to the parameter  "webdavAliasManagerImplementation" and copy your .jar to /WEB-INF/lib/

  -with the "servletDebug" parameter you can trigger the reference store implementation
   to spam at every method call. this parameter is optional and can be omitted

  -authentication is done by the servlet-container. If you need it, you have to
   add the appropriate sections to the web.xml


ACCESSING THE FILESTORE

  the webdav-filestore can be reached at:
  "http://<ip/name + port of the server>/<name of the webapp>/webdav"
  e.g.:   http://localhost:8080/webdav or http://localhost:8080/mywebapp/webdav/

