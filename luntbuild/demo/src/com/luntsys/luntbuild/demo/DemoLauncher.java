package com.luntsys.luntbuild.demo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;


/**
 * Launches standalone Demo using Jetty
 *
 * @author lubosp
 *
 */
public class DemoLauncher {

    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args == null || args.length < 2) {
            System.err.println("Usage: DemoLauncher port app");
            System.exit(-1);
        }
                
        //We will create our server running at http://localhost:args[0]
        final Server server = new Server();
        
        // Get stop port, default to 9999
        int stopPort = -1;
        if (args.length > 2) {
            try {
                stopPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                System.out.println("Failed to parse stop port " + args[2]);
                stopPort = -1;
            }
        }
        if (stopPort == -1) {
            try {
                stopPort = Integer.parseInt(args[0]) + 1;
            } catch (NumberFormatException ex) {
                System.out.println("Failed to parse stop port " + args[0] + 1 + " , using 9999!");
                stopPort = 9999;
            }
        }
        int port = 9090;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.out.println("Failed to parse port " + args[0] + 1 + " , using 9090!");
            port = 9090;
        }
        
        // Start server
        try {
            ServerStopper stopper = new ServerStopper(server, stopPort);
            stopper.setPriority(Thread.MAX_PRIORITY);
            stopper.start();
            
            server.addListener(new InetAddrPort(InetAddress.getLocalHost(), port));
            server.addWebApplication("luntbuild", args[1]);
            server.start();

        } catch (Throwable th) {
            System.err.println("Failed to start servlet container: ");
            th.printStackTrace();
            System.exit(-1);
        }
    }
}

class ServerStopper extends Thread {
    
    /** server for listening for connections **/
    private final ServerSocket mSvrSock;

    private final Server mServer;
    
    ServerStopper(Server server, int aPort) throws IOException {
        this.mSvrSock = new ServerSocket(aPort);
        this.mServer = server;
    }
    /** Listens for client connections **/
    public void run() {
        try {
            while (true) {
                final Socket client = this.mSvrSock.accept();
                final ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                while (true) {
                    final String stop = (String) ois.readObject();
                    if (stop.equals("STOP")) {
                        this.mServer.stop();
                        client.close();
                        Thread.sleep(5000);
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Failed to stop!");
        System.exit(-1);
    }
}
