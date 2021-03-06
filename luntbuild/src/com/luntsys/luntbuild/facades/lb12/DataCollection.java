/*
 * Copyright luntsys (c) 2001-2004,
 * Date: 2005-4-1
 * Time: 21:29:53
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of facades for all objects in Luntbuild.
 * 
 * @author robin shine
 */
public class DataCollection {
	/**
	 * Version of this data collection, do not use final here, cause JVM compatibility issue
	 * may exist when doing reflection by xstream.
	 */
	private String version = "1.2";

	/** System properties */
	private List properties = new ArrayList();

	/** All defined projects */
	private List projects = new ArrayList();

	/** All defined schedules */
	private List schedules = new ArrayList();

	/** All existing builds */
	private List builds = new ArrayList();

	/** All defined users */
	private List users = new ArrayList();

	/** All defined VCS logins */
	private List vcsLoginMapping = new ArrayList();

	/** All defined role mappings */
	private List rolesMapping = new ArrayList();

	/** All defined notify mappings */
	private List notifyMapping = new ArrayList();

	/** All defined roles */
	private List roles = new ArrayList();

	/**
	 * Gets the version of this data collecton.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the list of facades of all system properties.
	 * 
	 * @return the list of property facades
	 * @see PropertyFacade
	 */
	public List getProperties() {
		return properties;
	}

	/**
	 * Sets the list of facades of all system properties.
	 * 
	 * @param properties the list of property facades
	 * @see PropertyFacade
	 */
	public void setProperties(List properties) {
		this.properties = properties;
	}

	/**
	 * Gets the list of facades of all defined projects.
	 * 
	 * @return the list of project facades
	 * @see ProjectFacade
	 */
	public List getProjects() {
		return projects;
	}

	/**
	 * Sets the list of facades of all defined projects.
	 * 
	 * @param projects the list of project facades
	 * @see ProjectFacade
	 */
	public void setProjects(List projects) {
		this.projects = projects;
	}

	/**
	 * Gets the list of facades of all defined users.
	 * 
	 * @return the list of user facades
	 * @see UserFacade
	 */
	public List getUsers() {
		return users;
	}

	/**
	 * Sets the list of facades of all defined users.
	 * 
	 * @param users the list of user facades
	 * @see UserFacade
	 */
	public void setUsers(List users) {
		this.users = users;
	}

	/**
	 * Gets the list of facades of all defined roles.
	 * 
	 * @return the list of role facades
	 * @see RoleFacade
	 */
	public List getRoles() {
		return roles;
	}

	/**
	 * Sets the list of facades of all defined roles.
	 * 
	 * @param roles the list of role facades
	 * @see RoleFacade
	 */
	public void setRoles(List roles) {
		this.roles = roles;
	}

	/**
	 * Gets the list of facades of all defined VCS logins.
	 * 
	 * @return the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	/**
	 * Sets the list of facades of all defined VCS logins.
	 * 
	 * @param vcsLoginMapping the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	/**
	 * Gets the list of facades of all defined role mappings.
	 * 
	 * @return the list of role mapping facades
	 * @see RolesMappingFacade
	 */
	public List getRolesMapping() {
		return rolesMapping;
	}

	/**
	 * Sets the list of facades of all defined role mappings.
	 * 
	 * @param rolesMapping the list of role mapping facades
	 * @see RolesMappingFacade
	 */
	public void setRolesMapping(List rolesMapping) {
		this.rolesMapping = rolesMapping;
	}

	/**
	 * Gets the list of facades of all defined notify mappings.
	 * 
	 * @return the list of notify mapping facades
	 * @see NotifyMappingFacade
	 */
	public List getNotifyMapping() {
		return notifyMapping;
	}

	/**
	 * Sets the list of facades of all defined notify mappings.
	 * 
	 * @param notifyMapping the list of notify mapping facades
	 * @see NotifyMappingFacade
	 */
	public void setNotifyMapping(List notifyMapping) {
		this.notifyMapping = notifyMapping;
	}

	/**
	 * Gets the list of facades of all defined schedules.
	 * 
	 * @return the list of schedule facades
	 * @see ScheduleFacade
	 */
	public List getSchedules() {
		return schedules;
	}

	/**
	 * Sets the list of facades of all defined schedules.
	 * 
	 * @param schedules the list of schedule facades
	 * @see ScheduleFacade
	 */
	public void setSchedules(List schedules) {
		this.schedules = schedules;
	}

	/**
	 * Gets the list of facades of existing builds.
	 * 
	 * @return the list of build facades
	 * @see BuildFacade
	 */
	public List getBuilds() {
		return builds;
	}

	/**
	 * Sets the list of facades of existing builds.
	 * 
	 * @param builds the list of build facades
	 * @see BuildFacade
	 */
	public void setBuilds(List builds) {
		this.builds = builds;
	}

	/**
	 * Gets the project facade with the specified name.
	 * 
	 * @param projectName the project name
	 * @return the project facade, or <code>null</code> if no project has that name
	 */
	public ProjectFacade getProject(String projectName) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			ProjectFacade projectFacade = (ProjectFacade) it.next();
			if (projectFacade.getName().equals(projectName))
				return projectFacade;
		}
		return null;
	}

	/**
	 * Gets the schedule facade with the specified name from the specified project identifier.
	 * 
	 * @param projectId the project identifier
	 * @param scheduleName the schedule name
	 * @return the schedule facade, or <code>null</code> if no schedule has that name for that project identifier
	 */
	public ScheduleFacade getSchedule(long projectId, String scheduleName) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			ScheduleFacade scheduleFacade = (ScheduleFacade) it.next();
			if (scheduleFacade.getProjectId() == projectId &&
					scheduleFacade.getName().equals(scheduleName))
				return scheduleFacade;
		}
		return null;
	}

	/**
	 * Gets the project facade with the specified identifier.
	 * 
	 * @param projectId the project identifier
	 * @return the project facade, or <code>null</code> if no project has that identifier
	 */
	public ProjectFacade getProject(long projectId) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			ProjectFacade projectFacade = (ProjectFacade) it.next();
			if (projectFacade.getId() == projectId)
				return projectFacade;
		}
		return null;
	}

	/**
	 * Gets the schedule facade with the specified identifier.
	 * 
	 * @param scheduleId the schedule identifier
	 * @return the schedule facade, or <code>null</code> if no schedule has that identifier
	 */
	public ScheduleFacade getSchedule(long scheduleId) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			ScheduleFacade scheduleFacade = (ScheduleFacade) it.next();
			if (scheduleFacade.getId() == scheduleId)
				return scheduleFacade;
		}
		return null;
	}

	/**
	 * Gets the user facade with the specified identifier.
	 * 
	 * @param userId the user identifier
	 * @return the user facade, or <code>null</code> if no user has that identifier
	 */
	public UserFacade getUser(long userId) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			UserFacade userFacade = (UserFacade) it.next();
			if (userFacade.getId() == userId)
				return userFacade;
		}
		return null;
	}

	/**
	 * Gets the role facade with the specified identifier.
	 * 
	 * @param roleId the role identifier
	 * @return the role facade, or <code>null</code> if no role has that identifier
	 */
	public RoleFacade getRole(long roleId) {
		Iterator it = getRoles().iterator();
		while (it.hasNext()) {
			RoleFacade roleFacade = (RoleFacade) it.next();
			if (roleFacade.getId() == roleId)
				return roleFacade;
		}
		return null;
	}

	/**
	 * Gets the build facade with the specified identifier.
	 * 
	 * @param buildId the build identifier
	 * @return the build facade, or <code>null</code> if no build has that identifier
	 */
	public BuildFacade getBuild(long buildId) {
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			BuildFacade buildFacade = (BuildFacade) it.next();
			if (buildFacade.getId() == buildId)
				return buildFacade;
		}
		return null;
	}

	/**
	 * Gets the user facade with the specified name.
	 * 
	 * @param userName the user name
	 * @return the user facade, or <code>null</code> if no user has that name
	 */
	public UserFacade getUser(String userName) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			UserFacade userFacade = (UserFacade) it.next();
			if (userFacade.getName().equals(userName))
				return userFacade;
		}
		return null;
	}
}
