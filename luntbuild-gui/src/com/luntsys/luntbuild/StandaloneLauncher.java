package com.luntsys.luntbuild;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

//    private static Log logger = LogFactory.getLog(StandaloneStopper.class);
	Launcher winstone = null;

    /**
     * @param args 0 - port, [1 - stop port]
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
//                logger.warn("Failed to parse port " + args[0] + " , using 9090!");
                port = 9090;
            }
        }

        // Get stop port, default to application port + 1
        int stopPort = -1;
        if (args.length > 1) {
            try {
                stopPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
//                logger.error("Failed to parse stop port " + args[1]);
                stopPort = -1;
            }
        }
        if (stopPort == -1) {
            stopPort = port + 1;
        }

        // Start server
        try {
            Map wargs = new HashMap();
            wargs.put("httpPort", Integer.toString(port));
            wargs.put("webroot", "WebRoot"); // or any other command line args, eg port
            wargs.put("controlPort", Integer.toString(stopPort));
            Launcher.initLogger(wargs);
            Launcher winstone = new Launcher(wargs); // spawns threads, so your application doesn't block

            while (true) {
            	Thread.sleep(10000);
            }
        } catch (Throwable th) {
//            logger.fatal("Failed to start servlet container: ");
//            logger.debug("Failed to start servlet container: ", th);
            System.exit(-1);
        }
    }
}
