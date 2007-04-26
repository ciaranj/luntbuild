package com.luntsys.luntbuild.facades.lb20;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set builders = new HashSet();
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
	public Set getBuilders() {
		return this.builders;
	}

	/**
	 * Set builders configured for this project
	 * @param builders
	 */
	public void setBuilders(Set builders) {
		this.builders = builders;
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
     */
    public final String[] getProjectAdmins() {
        return this.projectAdmins;
    }

    /**
     * @param projectAdmins The projectAdmins to set (array of names).
     */
    public final void setProjectAdmins(String[] projectAdmins) {
        this.projectAdmins = projectAdmins;
    }

    /**
     * @return Returns the projectBuilders (array of names).
     */
    public final String[] getProjectBuilders() {
        return this.projectBuilders;
    }

    /**
     * @param projectBuilders The projectBuilders to set (array of names).
     */
    public final void setProjectBuilders(String[] projectBuilders) {
        this.projectBuilders = projectBuilders;
    }

    /**
     * @return Returns the projectViewers (array of names).
     */
    public final String[] getProjectViewers() {
        return this.projectViewers;
    }

    /**
     * @param projectViewers The projectViewers to set (array of names).
     */
    public final void setProjectViewers(String[] projectViewers) {
        this.projectViewers = projectViewers;
    }

    /**
     * @return Returns the notifyUsers.
     */
    public final String[] getNotifyUsers() {
        return this.notifyUsers;
    }

    /**
     * @param notifyUsers The notifyUsers to set.
     */
    public final void setNotifyUsers(String[] notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

    /**
     * @return Returns the scheduleList.
     */
    public final List getScheduleList() {
        return this.scheduleList;
    }

    /**
     * @param scheduleList The scheduleList to set.
     */
    public final void setScheduleList(List scheduleList) {
        this.scheduleList = scheduleList;
    }

}
