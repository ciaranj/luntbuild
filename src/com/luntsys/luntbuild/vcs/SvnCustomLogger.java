package com.luntsys.luntbuild.vcs;

import org.apache.tools.ant.Project;
import org.tmatesoft.svn.util.SVNDebugLoggerAdapter;

/**
 * SvnCustomLogger
 * Override superclass methods to redirect logging
 * as you wish. Superclass implementaion is empty, i.e.
 * all log messages are swallowed.
 *
 * @author lubosp
 *
 */
public class SvnCustomLogger extends SVNDebugLoggerAdapter {

    private transient Project antProject = null;

     /**
     * @param project
     */
    public SvnCustomLogger(Project project) {
         this.antProject = project;
     }

     public void log(String message, byte[] data) {
         /*
          * Used to log all data received or transmitted
          * over network
          */
         if (this.antProject != null) this.antProject.log(message, Project.MSG_DEBUG);
     }

     public void logInfo(String message) {
         /*
          * Used to log information messages
          */
         if (this.antProject != null) this.antProject.log(message, Project.MSG_DEBUG);
     }

     public void logError(String message) {
         /*
          * Used to log error messages
          */
         if (this.antProject != null) this.antProject.log(message, Project.MSG_ERR);
     }

     public void logInfo(Throwable th) {
         /*
          * Used to log information on exceptions
          */
         if (this.antProject != null) this.antProject.log(th.getMessage(), Project.MSG_DEBUG);
     }

     public void logError(Throwable th) {
         /*
          * Used to log exceptions
          */
         if (this.antProject != null) this.antProject.log(th.getMessage(), Project.MSG_ERR);
     }
}
