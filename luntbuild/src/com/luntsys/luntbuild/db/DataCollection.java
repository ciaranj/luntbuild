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

package com.luntsys.luntbuild.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of beans with relationships already setup.
 * @author robin shine
 */
public class DataCollection {
	/**
	 * System properties, item type is Property
	 */
	private List properties = new ArrayList();

	/**
	 * All defined projects, item type is Project
	 */
	private List projects = new ArrayList();

	/**
	 * All defined schedules, item type is Schedule
	 */
	private List schedules = new ArrayList();

	/**
	 * All defined builds, item type is Build
	 */
	private List builds = new ArrayList();

	/**
	 * All defined users, item type is User
	 */
	private List users = new ArrayList();

	/**
	 * All defined VcsLogins, item type is VcsLogin
	 */
	private List vcsLoginMapping = new ArrayList();

	/**
	 * All defined roles, item type is Role
	 */
	private List roles = new ArrayList();

	/**
	 * Gets the list of all defined system level properties.
	 * 
	 * @return the list of properties
	 * @see Property
	 */
	public List getProperties() {
		return properties;
	}

	/**
	 * Sets the list of system level properties.
	 * 
	 * @param properties the list of properties
	 * @see Property
	 */
	public void setProperties(List properties) {
		this.properties = properties;
	}

	/**
	 * Gets the list of all defined projects.
	 * 
	 * @return the list of projects
	 * @see Project
	 */
	public List getProjects() {
		return projects;
	}

	/**
	 * Sets the list of projects.
	 * 
	 * @param projects the list of projects
	 * @see Project
	 */
	public void setProjects(List projects) {
		this.projects = projects;
	}

	/**
	 * Gets the list of all defined users.
	 * 
	 * @return the list of users
	 * @see User
	 */
	public List getUsers() {
		return users;
	}

	/**
	 * Sets the list of users.
	 * 
	 * @param users the list of users
	 * @see User
	 */
	public void setUsers(List users) {
		this.users = users;
	}

	/**
	 * Gets the list of all defined roles.
	 * 
	 * @return the list of roles
	 * @see Role
	 */
	public List getRoles() {
		return roles;
	}

	/**
	 * Sets the list of roles.
	 * 
	 * @param roles the list of roles
	 * @see Role
	 */
	public void setRoles(List roles) {
		this.roles = roles;
	}

	/**
	 * Gets the list of all defined VCS login mappings.
	 * 
	 * @return the list of VCS login mappings
	 * @see VcsLogin
	 */
	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	/**
	 * Sets the list of VCS login mappings.
	 * 
	 * @param vcsLoginMapping the list of VCS login mappings
	 * @see VcsLogin
	 */
	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	/**
	 * Gets the list of all defined schedules.
	 * 
	 * @return the list of schedules
	 * @see Schedule
	 */
	public List getSchedules() {
		return schedules;
	}

	/**
	 * Sets the list of schedules.
	 * 
	 * @param schedules the list of schedules
	 * @see Schedule
	 */
	public void setSchedules(List schedules) {
		this.schedules = schedules;
	}

	/**
	 * Gets the list of all stored builds.
	 * 
	 * @return the list of builds
	 * @see Build
	 */
	public List getBuilds() {
		return builds;
	}

	/**
	 * Sets the list of builds.
	 * 
	 * @param builds the list of builds
	 * @see Build
	 */
	public void setBuilds(List builds) {
		this.builds = builds;
	}

	/**
	 * Gets the project with the specified name.
	 * 
	 * @param projectName the project name
	 * @return the project, or <code>null</code> if no project has that name
	 */
	public Project getProject(String projectName) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getName().equals(projectName))
				return project;
		}
		return null;
	}

	/**
	 * Gets the schedule with the specified name from the specified project identifier.
	 * 
	 * @param projectId the project identifier
	 * @param scheduleName the schedule name
	 * @return the schedule, or <code>null</code> if no schedule has that name for that project identifier
	 */
	public Schedule getSchedule(long projectId, String scheduleName) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getProject().getId() == projectId &&
					schedule.getName().equals(scheduleName))
				return schedule;
		}
		return null;
	}

	/**
	 * Gets the project with the specified identifier.
	 * 
	 * @param projectId the project identifier
	 * @return the project, or <code>null</code> if no project has that identifier
	 */
	public Project getProject(long projectId) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getId() == projectId)
				return project;
		}
		return null;
	}

	/**
	 * Gets the schedule with the specified identifier.
	 * 
	 * @param scheduleId the schedule identifier
	 * @return the schedule, or <code>null</code> if no schedule has that identifier
	 */
	public Schedule getSchedule(long scheduleId) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getId() == scheduleId)
				return schedule;
		}
		return null;
	}

	/**
	 * Gets the user with the specified identifier.
	 * 
	 * @param userId the user identifier
	 * @return the user, or <code>null</code> if no user has that identifier
	 */
	public User getUser(long userId) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (user.getId() == userId)
				return user;
		}
		return null;
	}

	/**
	 * Gets the role with the specified identifier.
	 * 
	 * @param roleId the role identifier
	 * @return the role, or <code>null</code> if no role has that identifier
	 */
	public Role getRole(long roleId) {
		Iterator it = getRoles().iterator();
		while (it.hasNext()) {
			Role role = (Role) it.next();
			if (role.getId() == roleId)
				return role;
		}
		return null;
	}

	/**
	 * Gets the build with the specified identifier.
	 * 
	 * @param buildId the build identifier
	 * @return the build, or <code>null</code> if no build has that identifier
	 */
	public Build getBuild(long buildId) {
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			if (build.getId() == buildId)
				return build;
		}
		return null;
	}

	/**
	 * Gets the user with the specified name.
	 * 
	 * @param userName the user name
	 * @return the user, or <code>null</code> if no user has that name
	 */
	public User getUser(String userName) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (user.getName().equals(userName))
				return user;
		}
		return null;
	}
}
