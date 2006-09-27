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

/**
 * Facade of a luntbuild project, this is mainly used by web service client to operate
 * project specific properties
 *
 * @author robin shine
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
	 * Get name of this project
	 * @return name of this project
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get id of this project
	 * @return id of this project
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Set id of this project
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Set name of this project
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get description of this project
	 * @return description of this project
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set description of this project
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get vcs list of this project
	 * @return vcs list configured for this project
	 */
	public List getVcsList() {
		return this.vcsList;
	}

	/**
	 * Set vcs list of this project
	 * @param vcsList
	 */
	public void setVcsList(List vcsList) {
		this.vcsList = vcsList;
	}

	/**
	 * Get builders configured for this project
	 * @return builders configured for this project
	 */
	public List getBuilderList() {
		return this.builderList;
	}

	/**
	 * Set builders configured for this project
	 * @param builderList
	 */
	public void setBuilderList(List builderList) {
		this.builderList = builderList;
	}

	/**
	 * @return variables
	 */
	public String getVariables() {
		return this.variables;
	}

	/** Set variables
	 * @param variables
	 */
	public void setVariables(String variables) {
		this.variables = variables;
	}

	/**
	 * Get log level of this project
	 * @return one value of
	 * {@link com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_BRIEF},
	 * {@link com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_NORMAL},
	 * {@link com.luntsys.luntbuild.facades.Constants#LOG_LEVEL_VERBOSE}
	 */
	public int getLogLevel() {
		return this.logLevel;
	}

	/** Set log level
	 * @param logLevel
	 */
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * Get notifier list of this project
	 * @return notifiers
	 */
	public List getNotifiers() {
		return this.notifiers;
	}

	/** Set notifier clas names
	 * @param notifiers
	 */
	public void setNotifiers(List notifiers) {
		this.notifiers = notifiers;
	}

    /**
     * @return Returns the projectAdmins (array of names).
     * @since 1.3
     */
    public final String[] getProjectAdmins() {
        return this.projectAdmins;
    }

    /**
     * @param projectAdmins The projectAdmins to set (array of names).
     * @since 1.3
     */
    public final void setProjectAdmins(String[] projectAdmins) {
        this.projectAdmins = projectAdmins;
    }

    /**
     * @return Returns the projectBuilders (array of names).
     * @since 1.3
     */
    public final String[] getProjectBuilders() {
        return this.projectBuilders;
    }

    /**
     * @param projectBuilders The projectBuilders to set (array of names).
     * @since 1.3
     */
    public final void setProjectBuilders(String[] projectBuilders) {
        this.projectBuilders = projectBuilders;
    }

    /**
     * @return Returns the projectViewers (array of names).
     * @since 1.3
     */
    public final String[] getProjectViewers() {
        return this.projectViewers;
    }

    /**
     * @param projectViewers The projectViewers to set (array of names).
     * @since 1.3
     */
    public final void setProjectViewers(String[] projectViewers) {
        this.projectViewers = projectViewers;
    }

    /**
     * @return Returns the notifyUsers.
     * @since 1.3
     */
    public final String[] getNotifyUsers() {
        return this.notifyUsers;
    }

    /**
     * @param notifyUsers The notifyUsers to set.
     * @since 1.3
     */
    public final void setNotifyUsers(String[] notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

    /**
     * @return Returns the scheduleList.
     * @since 1.3
     */
    public final List getScheduleList() {
        return this.scheduleList;
    }

    /**
     * @param scheduleList The scheduleList to set.
     * @since 1.3
     */
    public final void setScheduleList(List scheduleList) {
        this.scheduleList = scheduleList;
    }

}
