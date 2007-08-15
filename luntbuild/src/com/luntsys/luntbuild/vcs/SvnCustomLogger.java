package com.luntsys.luntbuild.vcs;

import org.apache.tools.ant.Project;
import org.tmatesoft.svn.util.SVNDebugLogAdapter;

/**
 * Custom Subversion logger.  Logs messages to an Ant project.
 * 
 * @author lubosp
 */
public class SvnCustomLogger extends SVNDebugLogAdapter {

    private transient Project antProject = null;

    /**
     * Constructor, creates a new Subversion logger.
     * 
     * @param project the ant project used for logging
     */
    public SvnCustomLogger(Project project) {
        this.antProject = project;
    }

    /**
     * Logs a messages transmitted over a network.
     * 
     * @param message the message
     * @param data the data from the network
     */
    public void log(String message, byte[] data) {
        if (this.antProject != null) this.antProject.log(message, Project.MSG_DEBUG);
    }

    /**
     * Logs an info level message.
     * 
     * @param message the message
     */
    public void logInfo(String message) {
        if (this.antProject != null) this.antProject.log(message, Project.MSG_DEBUG);
    }

    /**
     * Logs an error level message.
     * 
     * @param message the message
     */
    public void logError(String message) {
        if (this.antProject != null) this.antProject.log(message, Project.MSG_ERR);
    }

    /**
     * Logs an info level message from an exception.
     * 
     * @param th the exception
     */
    public void logInfo(Throwable th) {
        if (this.antProject != null) this.antProject.log(th.getMessage(), Project.MSG_DEBUG);
    }

    /**
     * Logs an error level message from an exception.
     * 
     * @param th the exception
     */
    public void logError(Throwable th) {
        if (this.antProject != null) this.antProject.log(th.getMessage(), Project.MSG_ERR);
    }
}
