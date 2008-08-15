package com.luntsys.luntbuild;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Starts standalone Luntbuild with embedded Winstone servlet container
 *
 * @author lubosp
 *
 */
public class StandaloneLauncher {

	public static final String STANDALONE_JAR_NAME = "luntbuild-standalone.war";

    /**
     * @param args 0 - port, [1 - stop port] [2 - webroot]
     */
    public static void main(String[] args) {
//        try {
//            // Luntbuild.setLuntbuildLogs(".", "log4j.properties");
//        	PropertyConfigurator.configure("log4j.properties");
//        } catch (Exception e) {
//            BasicConfigurator.configure();
//        }

        // Get application port, default to 9090
        int port = 9090;
        if (args == null || args.length < 1) {
            port = 9090;
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.err.println("Failed to parse port " + args[0] + " , using 9090!");
                port = 9090;
            }
        }

        // Get stop port, default to application port + 1
        int stopPort = -1;
        if (args.length > 1) {
            try {
                stopPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
            	System.err.println("Failed to parse stop port " + args[1]);
                stopPort = -1;
            }
        }
        if (stopPort == -1) {
            stopPort = port + 1;
        }
        String warfile = null;
        String webroot = null;
        if (args.length > 2) {
        	if (args[2].endsWith(".war") || args[2].endsWith(".jar"))
        		warfile = args[2];
        	else
        		webroot = args[2];
        } else {
        	File f = new File(STANDALONE_JAR_NAME);
        	if (f.exists() && f.isFile()) {
        		warfile = STANDALONE_JAR_NAME;
        	} else {
	        	f = new File("WebRoot");
	        	if (f.exists() && f.isDirectory())
	        		webroot = "WebRoot";
        	}
    	}

        ArrayList argsList = new ArrayList();
        argsList.add("--httpPort=" + Integer.toString(port));
        if (webroot != null) {
        	argsList.add("--webroot=" + webroot);
        } else if (warfile != null) {
        	// Do noting we will use extracted temp jar
        } else {
        	webroot = "";
        	argsList.add("--webroot=" + ".");
        }
        argsList.add("--controlPort=" + Integer.toString(stopPort));

        String[] arguments = (String[])argsList.toArray(new String[argsList.size()]);
        // Start server
        try {
            if (webroot != null) {
            	startWinstoneStandalone(arguments);
            } else if (warfile != null) {
            	startWinstoneFromWar(warfile, arguments);
            }

        } catch (Throwable th) {
        	System.err.println("Failed to start servlet container: " + th.getMessage());
            System.exit(-1);
        }

    }

    public static void startWinstoneFromWar(String warname, String[] args) throws Exception {
    	File warFile = new File(warname);

        // clean up any previously extracted copy, since
        // winstone doesn't do so and that causes problems when newer version
        // is deployed.
        File tempFile = File.createTempFile("dummy", "dummy");
        deleteContents(new File(tempFile.getParent()), "winstone", "jar");
        tempFile.delete();

        // locate Winstone jar
        // put this jar in a file system so that we can load jars from there
        File tmpJar = File.createTempFile("winstone", "jar");
        copyStream(getWinstoneJar(STANDALONE_JAR_NAME), new FileOutputStream(tmpJar));
        tmpJar.deleteOnExit();

        // locate the Winstone launcher
        ClassLoader cl = new URLClassLoader(new URL[]{tmpJar.toURL()});
        Class launcher = cl.loadClass("winstone.Launcher");
        Method mainMethod = launcher.getMethod("main", new Class[]{String[].class});

        // figure out the arguments
        List arguments = new ArrayList(Arrays.asList(args));
        arguments.add(0,"--warfile="+ warFile.getAbsolutePath());

        // run
        mainMethod.invoke(null,new Object[]{arguments.toArray(new String[0])});
    }

    public static void startWinstoneStandalone(String[] args) throws Exception {

    	File dir = new File("standalone/lib");
    	ArrayList urlList = new ArrayList();
        File[] files = dir.listFiles();
        if(files!=null) {// be defensive
            for (int i = 0; i < files.length; i++)
            	if (files[i].getName().endsWith(".jar"))
            		urlList.add(files[i].toURL());
        }
        URL[] urls = (URL[])urlList.toArray(new URL[urlList.size()]);
        // locate the Winstone launcher
        ClassLoader cl = new URLClassLoader(urls);
        Class launcher = cl.loadClass("winstone.Launcher");
        Method mainMethod = launcher.getMethod("main", new Class[]{String[].class});

        // figure out the arguments
        List arguments = new ArrayList(Arrays.asList(args));

        // run
        mainMethod.invoke(null,new Object[]{arguments.toArray(new String[0])});
    }

    public static InputStream getWinstoneJar(String warName) throws Exception {
    	JarFile jarFile = new JarFile(warName);
    	InputStream in = jarFile.getInputStream(jarFile.getEntry("winstone.jar"));
    	return in;
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len;
        while((len=in.read(buf))>0)
            out.write(buf,0,len);
        in.close();
        out.close();
    }

    private static void deleteContents(File file, String start, String ext) throws IOException {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            if(files!=null) {// be defensive
                for (int i = 0; i < files.length; i++)
                    deleteContents(files[i], start, ext);
            }
        }
        String name = file.getName();
        if (name.indexOf(start) >= 0 && name.endsWith(ext)) file.delete();
    }
}
