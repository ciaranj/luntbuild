/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-20
 * Time: 19:31:43
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.luntsys.luntbuild.facades.lb12;

import java.util.List;
import java.util.ArrayList;

import com.luntsys.luntbuild.db.Schedule;

/**
 * Project facade.
 *
 * @author robin shine
 * @see com.luntsys.luntbuild.db.Project
 */
public class ProjectFacade {
	private long id;
	private String name;
	private String description;
	private String variables;
	private int logLevel;
	private List vcsList = new ArrayList();
	private List builderList = new ArrayList();
    private List scheduleList = new ArrayList();
	private List notifiers = new ArrayList();
    private String[] projectAdmins = null;
    private String[] projectBuilders = null;
    private String[] projectViewers = null;
    private String[] notifyUsers = null;

	/**
	 * Gets the identifer of this project.
	 * 
	 * @return the identifer of this project
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the identifier of this project.
	 *
	 * @param id the identifier of this project
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the name of this project.
	 * 
	 * @return the name of this project
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this project.
	 * 
	 * @param name the name of this project
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description of this project.
	 * 
	 * @return the description of this project
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description of this project.
	 * 
	 * @param description the description of this project
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the VCS list of this project.
	 * 
	 * @return the VCS list of this project
	 * @see com.luntsys.luntbuild.vcs.Vcs
	 */
	public List getVcsList() {
		return this.vcsList;
	}

	/**
	 * Sets the VCS list of this project.
	 * 
	 * @param vcsList the list of VCS adaptors
	 * @see com.luntsys.luntbuild.vcs.Vcs
	 */
	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	/**
	 * Gets the builder list of this project.
	 *
	 * @return the builder list of this project
	 * @see com.luntsys.luntbuild.builders.Builder
	 */
	public List getBuilderList() {
		return this.builderList;
	}

	/**
	 * Sets the builder list of this project.
	 * 
	 * @param builderList the list of builders
	 * @see com.luntsys.luntbuild.builders.Builder
	 */
	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

	/**
	 * Gets all variables, encoded as a string.
	 * 
	 * @return all variables encoded as a string
	 */
	public String getVariables() {
		return this.variables;
	}

	/**
	 * Sets all variables, encoded as a string.
	 * 
	 * @param variables all variables encoded as a string
	 */
	public void setVariables(String variables) {
		this.variables = variables;
	}

	/**
	 * Gets the log level of this project.
	 * 
	 * @return the log level
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_BRIEF
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_NORMAL
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_VERBOSE
	 */
	public int getLogLevel() {
		return this.logLevel;
	}

	/**
	 * Sets the log level of this project.
	 * 
	 * @param logLevel the log level
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_BRIEF
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_NORMAL
	 * @see com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_VERBOSE
	 */
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * Gets the notifiers configured for this project.
	 * 
	 * @return the notifiers configured for this project
	 * @see com.luntsys.luntbuild.notifiers.Notifier
	 */
	public List getNotifiers() {
		return this.notifiers;
	}

	/**
	 * Sets list of configured notifiers.
	 * 
	 * @param notifiers the list of notifiers
	 * @see com.luntsys.luntbuild.notifiers.Notifier
	 */
	public void setNotifiers(List notifiers) {
		this.notifiers = notifiers;
	}

    /**
     * Gets the list of project administrator user names.
     * 
     * @return the list of administrator names
     * @since 1.3
     */
    public final String[] getProjectAdmins() {
        return this.projectAdmins;
    }

    /**
     * Sets the list of project administrator user names.
     * 
     * @param projectAdmins the list of administrator names
     * @since 1.3
     */
    public final void setProjectAdmins(String[] projectAdmins) {
        this.projectAdmins = projectAdmins;
    }

    /**
     * Gets the list of project builder user names.
     * 
     * @return the list of builder names
     * @since 1.3
     */
    public final String[] getProjectBuilders() {
        return this.projectBuilders;
    }

    /**
     * Sets the list of project builder user names.
     * 
     * @param projectBuilders the list of builder names
     * @since 1.3
     */
    public final void setProjectBuilders(String[] projectBuilders) {
        this.projectBuilders = projectBuilders;
    }

    /**
     * Gets the list of project viewer user names.
     * 
     * @return the list of viewer names
     * @since 1.3
     */
    public final String[] getProjectViewers() {
        return this.projectViewers;
    }

    /**
     * Gets the list of project viewer user names.
     * 
     * @param projectViewers the list of viewer names
     * @since 1.3
     */
    public final void setProjectViewers(String[] projectViewers) {
        this.projectViewers = projectViewers;
    }

    /**
     * Gets the list of user names to notify.
     * 
     * @return the list of notify user names
     * @since 1.3
     */
    public final String[] getNotifyUsers() {
        return this.notifyUsers;
    }

    /**
     * Sets the list of user names to notify.
     * 
     * @param notifyUsers the list of notify user names
     * @since 1.3
     */
    public final void setNotifyUsers(String[] notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

	/**
	 * Gets the list of schedules of this project.
	 * 
	 * @return the schedules list of this project
	 * @see Schedule
     * @since 1.3
	 */
    public final List getScheduleList() {
        return this.scheduleList;
    }

	/**
	 * Sets the list of schedules of this project.
	 * 
	 * @param scheduleList the list of schedules
	 * @see Schedule
     * @since 1.3
	 */
    public final void setScheduleList(List scheduleList) {
        this.scheduleList = scheduleList;
    }
}
