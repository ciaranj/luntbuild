package com.luntsys.luntbuild.demo;

import org.mortbay.jetty.Server;

/**
 * TODO DOCUMENT ME 
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
        Server server = new Server();
        try {
            server.addListener(":" + args[0]);
            server.addWebApplication("luntbuild", args[1]);
            server.start();

        } catch (Throwable th) {
            System.err.println("Failed to start servlet container: ");
            th.printStackTrace();
            System.exit(-1);
        }
    }
}
