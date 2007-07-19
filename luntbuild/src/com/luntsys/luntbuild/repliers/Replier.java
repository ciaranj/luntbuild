/*
 * 
 */
package com.luntsys.luntbuild.repliers;

import com.luntsys.luntbuild.builders.*;
import com.luntsys.luntbuild.db.*;
import com.luntsys.luntbuild.facades.Constants;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;
import com.luntsys.luntbuild.vcs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

/**
 * Base class for all API repliers.
 * @author Jason Archer
 */
public abstract class Replier {
	protected static Log logger = LogFactory.getLog(Replier.class);

	/**
	 * Constants for available methods
	 */
	protected static final int ROOT = 0;
	protected static final int PROJECTS = 1;
	protected static final int SCHEDULES = 2;
	protected static final int BUILDS = 3;
	protected static final int BUILD = 4;
	protected static final int USERS = 5;
	protected static final int USER = 6;
	protected static final int NOTAMETHOD = 99;

	protected static final int NO_STRATEGY = 99;

	protected String source = null;
	protected String[] sources = null;
	protected int method = ROOT;
	protected int notify = NO_STRATEGY;

	/**
     * Gets the display name for this replier.
     *
     * @return the display name for this replier
	 */
	public abstract String getDisplayName();

	/**
	 * Gets the display comment for this replier.
	 * 
	 * @return comment the display comment
	 */
	public String getComment() {
		return "";
	}

	/** 
	 * Sets the source path to use.
	 * 
	 * @param source the source path
	 */
	public void setSource(String source) {
		this.source = source;
		this.sources = this.source.split("/");
	}

	/** 
	 * Sets the method to use.  The method describes what output is desired.
	 * 
	 * @param method the method
	 */
	public void setMethod(String method) {
		if (method == null) {
			/* ignore */
		} else if (method.equals("")) {
			this.method = ROOT;
		} else if (method.equals("getProjectList") || method.equals("projects")) {
			this.method = PROJECTS;
		} else if (method.equals("getScheduleList") || method.equals("schedules")) {
			this.method = SCHEDULES;
		} else if (method.equals("getBuildList") || method.equals("builds")) {
			this.method = BUILDS;
		} else if (method.equals("getBuild") || method.equals("build")) {
			this.method = BUILD;
		} else if (method.equals("getUserList") || method.equals("users")) {
			this.method = USERS;
		} else if (method.equals("getUser") || method.equals("user")) {
			this.method = USER;
		} else {
			this.method = NOTAMETHOD;
		}
	}

	/** 
	 * Sets the notification strategy to use.
	 * 
	 * @param notify the notification strategy
	 */
	public void setNotify(String notify) {
		if (notify == null) {
			this.notify = NO_STRATEGY;
		} else if (notify.equals("")) {
			this.notify = NO_STRATEGY;
		} else if (notify.equals("changed") || notify.equals("notify when status changed")) {
			this.notify = Constants.NOTIFY_WHEN_STATUS_CHANGED;
		} else if (notify.equals("success") || notify.equals("notify when success")) {
			this.notify = Constants.NOTIFY_IF_SUCCESS;
		} else if (notify.equals("failed") || notify.equals("notify when failed")) {
			this.notify = Constants.NOTIFY_IF_FAILED;
		} else if (notify.equals("always") || notify.equals("notify always")) {
			this.notify = Constants.NOTIFY_ALWAYS;
		} else if (notify.equals("failedorchanged") || notify.equals("notify when failed or status changed")) {
			this.notify = Constants.NOTIFY_IF_FAILED_OR_CHANGED;
		} else {
			this.notify = NO_STRATEGY;
		}
	}

	/** 
	 * Checks if a build can be notified on based on the chosen notification strategy.
	 * 
	 * @param build the build
	 * @return <code>true</code> if the build fits the notification strategy
	 */
	public boolean canNotify(Build build) {
		int strategy = Constants.NOTIFY_ALWAYS;
		if (notify == NO_STRATEGY) {
			strategy = build.getSchedule().getNotifyStrategy();
		} else {
			strategy = notify;
		}

		if (strategy == Constants.NOTIFY_WHEN_STATUS_CHANGED) {
			try {
				Build lastBuild = Luntbuild.getDao().loadPreviousBuild(build);
				if (lastBuild == null || build.getStatus() != lastBuild.getStatus()) {
					return true;
				}
			} catch (Exception e) {
				logger.error("Error retrieving last build", e);
			}
		} else if (strategy == Constants.NOTIFY_IF_SUCCESS) {
			if (build.getStatus() == Constants.BUILD_STATUS_SUCCESS) {
				return true;
			}
		} else if (strategy == Constants.NOTIFY_IF_FAILED) {
			if (build.getStatus() == Constants.BUILD_STATUS_FAILED) {
				return true;
			}
		} else if (strategy == Constants.NOTIFY_ALWAYS || strategy == Constants.NOTIFY_NONE) {
			return true;
		} else if (strategy == Constants.NOTIFY_IF_FAILED_OR_CHANGED) {
			try {
				Build lastBuild = Luntbuild.getDao().loadPreviousBuild(build);
				if (lastBuild == null || build.getStatus() == Constants.BUILD_STATUS_FAILED
					|| build.getStatus() != lastBuild.getStatus()) {
					return true;
				}
			} catch (Exception e) {
				logger.error("Error retrieving last build", e);
			}
		} else {
			return true;
		}

		return false;
	}

	/**
	 * Gets the full reply based on the chosen method.
	 * 
	 * @return the full reply message
	 */
	public abstract String getReply();

	/**
	 * Gets the "system" object containing system wide settings and users.
	 * 
	 * @return the system object
	 */
	public abstract String getSystem();

	/**
	 * Gets the property object of the specified system level property.
	 * 
	 * @param property the property
	 * @return the property object
	 */
	public abstract String getNotifierProperty(NotifierProperty property);

	/**
	 * Gets the property object of the specified user level property.
	 * 
	 * @param property the property
	 * @param user the user
	 * @return the property object
	 */
	public abstract String getNotifierProperty(NotifierProperty property, User user);

	/**
	 * Gets the user object of the specified user.
	 * 
	 * @param user the user
	 * @return the user object
	 */
	public abstract String getUser(User user);

	/**
	 * Gets the project object of the specified project.
	 * 
	 * @param project the project
	 * @return the project object
	 */
	public abstract String getProject(Project project);

	/**
	 * Gets the vcs object of the specified vcs.
	 * 
	 * @param vcs the vcs
	 * @return the vcs object
	 */
	public abstract String getVcs(Vcs vcs);

	/**
	 * Gets the builder object of the specified builder.
	 * 
	 * @param builder the builder
	 * @return the builder object
	 */
	public abstract String getBuilder(Builder builder);

	/**
	 * Gets the schedule object of the specified schedule.
	 * 
	 * @param schedule the schedule
	 * @return the schedule object
	 */
	public abstract String getSchedule(Schedule schedule);

	/**
	 * Gets the build object of the specified build.
	 * 
	 * @param build the build
	 * @return the build object
	 */
	public abstract String getBuild(Build build);

    /**
     * Determins the hostname and port of the server.
     * 
     * @param text the URL of the server
     * @return the root of the URL with only the protocol, hostname and port
     */
    protected static final String extractRootUrl(String text) throws Exception {
        URL url = new URL(text);
        return url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
    }
}