package com.luntsys.luntbuild.demo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Stops Demo launched by DemoLauncher
 *
 * @author lubosp
 *
 */
public class DemoStopper {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        int stopPort = 9999;
        if (args != null && args.length > 0) {
            try {
                stopPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.out.println("Failed to parse stop port " + args[0] + 1 + " , using 9999!");
                stopPort = 9999;
            }
        }

        String host = "localhost";
        if (args != null && args.length > 1) {
            host = args[1];
        }
        
        ObjectOutputStream oos = null;
        try {
            oos =
                new ObjectOutputStream(new Socket(InetAddress.getByName(host), stopPort).getOutputStream());
            oos.writeObject("STOP");
            oos.flush();
            Thread.sleep(5000);
            oos.close();
        } catch(IOException e) {
            System.out.println("Failed to stop using stop port " + stopPort);
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Failed to stop using stop port " + stopPort);
            e.printStackTrace();
        }
    }
}
