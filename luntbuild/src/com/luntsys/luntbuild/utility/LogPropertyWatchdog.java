/**
 * 
 */
package com.luntsys.luntbuild.utility;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;

/**
*
* WorkerPropertyWatchdog watches the log configuration file for changes,
* and sets new configuration on change.
*
* @author lubosp
*
*/
public class LogPropertyWatchdog extends FileWatchdog {

	private static Log logger = LogFactory.getLog(Luntbuild.class);
	private boolean isInitialized = false;
	
	LogPropertyWatchdog(String config, String installDir) {
       super(config);
       isInitialized = true;
   }

   /**
    * Call {@link PropertyConfigurator#configure(String)} with the
    * <code>filename</code> to reconfigure log4j.
    */
   public void doOnChange() {

	   if (!isInitialized) return;
	   
       new PropertyConfigurator().doConfigure(super.filename, LogManager.getLoggerRepository());

       Logger rootLogger = Logger.getRootLogger();
       Enumeration en = rootLogger.getAllAppenders();
       while ( en.hasMoreElements() ) {
           Appender app = (Appender) en.nextElement();
           if ( app instanceof FileAppender ) {
               // activate/reload options
               // set the new logfile
        	   String name = app.getName();
        	   if (name != null && name.equals("luntbuild_logfile")) {
        		   setLuntbuildHtmlLog(app);
        	   } else if (name != null && name.equals("luntbuild_txt_logfile")) {
        		   setLuntbuildTextLog(app);
        	   } else {
        		   String fileName = ((FileAppender)app).getFile();
        		   ((FileAppender) app).setFile(new File(Luntbuild.installDir + "/logs/" +
        				   fileName).getAbsolutePath());
        		   ((FileAppender) app).activateOptions();
        	   }
           }
       }
       logger.info("Updated log files on change of configuration file: " + super.filename);
   }
   
   /**
    * Sets the Luntbuild HTML log.
    * 
    * @param installDir the luntbuild installation directory
    * @throws IOException from {@link FileAppender#FileAppender(org.apache.log4j.Layout, java.lang.String, boolean)}
    */
   public final void setLuntbuildHtmlLog(Appender app) {
       if (app != null) {
           ((FileAppender) app).setFile(new File(Luntbuild.installDir + "/logs/" +
                   Luntbuild.log4jFileName).getAbsolutePath());
           ((FileAppender) app).activateOptions();
       } else {
           logger.warn("Can not find luntbuild_logfile appender, creating...");
           HTMLLayout layout = new HTMLLayout();
           layout.setTitle("Luntbuild System Log");
           layout.setLocationInfo(true);
           try {
	           app = new FileAppender(layout, new File(Luntbuild.installDir + "/logs/" +
	                   Luntbuild.log4jFileName).getAbsolutePath(), true);
	           ((FileAppender) app).setAppend(false);
	           Logger log = LogManager.getLogger("com.luntsys.luntbuild");
	           log.setLevel(Level.INFO);
	           log.addAppender(app);
	           ((FileAppender) app).activateOptions();
           } catch (Exception e) {
               logger.error("Can not create luntbuild_logfile appender");
           }
       }
   }

   /**
    * Sets the Luntbuild text log.
    * 
    * @param installDir the luntbuild installation directory
    * @throws IOException from {@link FileAppender#FileAppender(org.apache.log4j.Layout, java.lang.String, boolean)}
    */
   public final void setLuntbuildTextLog(Appender app) {
       if (app != null) {
           ((FileAppender) app).setFile(new File(Luntbuild.installDir + "/logs/" +
                   Luntbuild.log4jFileNameTxt).getAbsolutePath());
           ((FileAppender) app).activateOptions();
       } else {
           logger.warn("Can not find luntbuild_txt_logfile appender, creating...");
           Layout layout = new PatternLayout("%5p [%t] (%F:%L) - %m%n");
           try {
	           app = new FileAppender(layout, new File(Luntbuild.installDir + "/logs/" +
	                   Luntbuild.log4jFileNameTxt).getAbsolutePath(), true);
	           Logger log = LogManager.getLogger("com.luntsys.luntbuild");
	           log.setLevel(Level.INFO);
	           log.addAppender(app);
	           ((FileAppender) app).activateOptions();
           } catch (Exception e) {
               logger.error("Can not create luntbuild_txt_logfile appender");
           }
       }
   }

}

