package com.luntsys.luntbuild.vcs;

import java.util.logging.Level;

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

	public void log(Throwable th, Level level) {
		log(th.getMessage(), level);
	}

	public void log(String msg, Level level) {
		if (this.antProject == null) return;
		if (level.intValue() == Level.FINE.intValue()) {
			this.antProject.log(msg, Project.MSG_INFO);
		} else if (level.intValue() == Level.FINER.intValue()) {
			this.antProject.log(msg, Project.MSG_DEBUG);
		} else if (level.intValue() == Level.FINEST.intValue()) {
			this.antProject.log(msg, Project.MSG_DEBUG);
		} else if (level.intValue() == Level.INFO.intValue()) {
			this.antProject.log(msg, Project.MSG_INFO);
		} else if (level.intValue() == Level.WARNING.intValue()) {
			this.antProject.log(msg, Project.MSG_WARN);
		} else if (level.intValue() == Level.SEVERE.intValue()) {
			this.antProject.log(msg, Project.MSG_ERR);
		} else {
			this.antProject.log(msg, Project.MSG_DEBUG);
		}
	}

	public void logFine(Throwable th) {
		log(th.getMessage(), Level.FINE);
	}

	public void logFine(String msg) {
		log(msg, Level.FINE);
	}

	public void logFiner(Throwable th) {
		log(th.getMessage(), Level.FINER);
	}

	public void logFiner(String msg) {
		log(msg, Level.FINER);
	}

	public void logFinest(Throwable th) {
		log(th.getMessage(), Level.FINEST);
	}

	public void logFinest(String msg) {
		log(msg, Level.FINEST);
	}

	public void logSevere(Throwable th) {
		log(th.getMessage(), Level.SEVERE);
	}

	public void logSevere(String msg) {
		log(msg, Level.SEVERE);
	}
}
