package com.luntsys.luntbuild;


import java.util.HashMap;
import java.util.Map;

import winstone.Launcher;

/**
 * Starts standalone Luntbuild with embedded Jetty servlet container
 *
 * @author lubosp
 *
 */
public class StandaloneLauncher {

	Launcher winstone = null;

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
        String webroot = "WebRoot";
        if (args.length > 2) {
        	webroot = args[2];
        }
        // Start server
        try {
            Map wargs = new HashMap();
            wargs.put("httpPort", Integer.toString(port));
            wargs.put("webroot", webroot);
            wargs.put("controlPort", Integer.toString(stopPort));
            Launcher.initLogger(wargs);
            Launcher winstone = new Launcher(wargs);

            while (winstone.isRunning()) {
            	Thread.sleep(10000);
            }
        } catch (Throwable th) {
        	System.err.println("Failed to start servlet container: " + th.getMessage());
            System.exit(-1);
        }
    }
}
