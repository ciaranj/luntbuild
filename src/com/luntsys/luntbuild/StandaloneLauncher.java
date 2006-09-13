package com.luntsys.luntbuild;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;

import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Starts standalone Luntbuild with embedded Jetty servlet container
 *
 * @author lubosp
 *
 */
public class StandaloneLauncher {

    private static Log logger = LogFactory.getLog(StandaloneStopper.class);

    /**
     * @param args 0 - host, 1 - port, [2 - stop port]
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Usage: StandaloneLauncher host port [stopport]");
            System.exit(-1);
        }

        try {
            Luntbuild.setLuntbuildLogs(".", "log4j.properties");
        } catch (Exception e) {
            BasicConfigurator.configure();
        }

        //We will create our server running at http://args[0]:args[1]
        final Server server = new Server();

        // Get stop port, default to 9999
        int stopPort = -1;
        if (args.length > 2) {
            try {
                stopPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                logger.error("Failed to parse stop port " + args[2]);
                stopPort = -1;
            }
        }
        if (stopPort == -1) {
            try {
                stopPort = Integer.parseInt(args[1]) + 1;
            } catch (NumberFormatException ex) {
                logger.warn("Failed to parse stop port " + args[1] + 1 + " , using 9999!");
                stopPort = 9999;
            }
        }

        // Get application port, default to 9090
        int port = 9090;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            logger.warn("Failed to parse port " + args[1] + " , using 9090!");
            port = 9090;
        }

        // Get host ip address
        InetAddress hostInet = null;
        try {
            hostInet = InetAddress.getByName(args[0]);
        } catch (UnknownHostException uex) {
            hostInet = null;
            logger.warn("Unknown host " + args[0] + " using local host");
        } catch (SecurityException ex) {
            hostInet = null;
            logger.warn("Unable to connect to host " + args[0] + " using local host");
        }
        if (hostInet == null) {
            try {
                hostInet = InetAddress.getLocalHost();
            } catch (Exception e) {
                logger.fatal("Failed to aquire local host address: ");
                logger.debug("Failed to aquire local host address: ", e);
                System.exit(-1);
            }
        }

        // Start server
        try {
            ServerStopper stopper = new ServerStopper(server, stopPort, logger);
            stopper.setPriority(Thread.MAX_PRIORITY);
            stopper.start();

            server.addListener(new InetAddrPort(hostInet, port));
            server.addWebApplication("luntbuild", "web");
            server.start();

        } catch (Throwable th) {
            logger.fatal("Failed to start servlet container: ");
            logger.debug("Failed to start servlet container: ", th);
            System.exit(-1);
        }
    }
}

/**
 *
 * Server stopper thread
 *
 * @author lubosp
 *
 */
class ServerStopper extends Thread {

    /** server for listening for connections **/
    private final ServerSocket mSvrSock;
    private final Server mServer;
    private final Log logger;

    ServerStopper(Server server, int aPort, Log logger) throws IOException {
        this.mSvrSock = new ServerSocket(aPort);
        this.mServer = server;
        this.logger = logger;
    }
    /** Listens for client connections **/
    public void run() {
        try {
            while (true) {
                final Socket client = this.mSvrSock.accept();
                final ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                while (true) {
                    String stop = null;
                    try {
                        stop = (String) ois.readObject();
                        if (stop != null && stop.equals("STOP")) {
                            this.mServer.stop();
                            client.close();
                            Thread.sleep(5000);
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        this.logger.debug("Failed to stop!", e);
                    } catch (Exception e) {
                        this.logger.debug("Failed to stop!", e);
                    }
                }
            }
        } catch (IOException e) {
            this.logger.debug("Failed to stop!", e);
        } catch (Exception e) {
            this.logger.debug("Failed to stop!", e);
        }
        this.logger.warn("Failed to stop!");
        System.exit(-1);
    }
}
