/*
 *
 */

package com.luntsys.luntbuild;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import com.luntsys.luntbuild.utility.Luntbuild;

/**
 * Stops the standalone Luntbuild.
 *
 * @author lubosp
 */
public class StandaloneStopper {

    private static Log logger = LogFactory.getLog(StandaloneStopper.class);

    /**
     * Stops the standalone version of Luntbuild.
     * 
     * @param args 0 - host, 1 - port
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Usage: StandaloneStopper host port");
            System.exit(-1);
        }

        try {
            Luntbuild.setLuntbuildLogs();
        } catch (Exception e) {
            BasicConfigurator.configure();
        }

        int stopPort = 9999;
        if (args != null && args.length > 0) {
            try {
                stopPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                logger.error("Failed to parse stop port " + args[1] + " , using 9999!");
                stopPort = 9999;
            }
        }

        ObjectOutputStream oos = null;
        try {
            oos =
                new ObjectOutputStream(new Socket(InetAddress.getByName(args[0]), stopPort).getOutputStream());
            oos.writeObject("STOP");
            oos.flush();
            Thread.sleep(5000);
            oos.close();
        } catch(IOException e) {
            logger.debug("Failed to stop using stop port " + stopPort, e);
        } catch(Exception e) {
            logger.debug("Failed to stop using stop port " + stopPort);
        }
    }
}
