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
public class ProjectCollection {

	/**
	 * The project
	 */
	private Project project = new Project();

	/**
	 * All defined schedules for the project, item type is Schedule
	 */
	private List schedules = new ArrayList();

	/**
	 * All defined builds for the project, item type is Build
	 */
	private List builds = new ArrayList();

	/**
	 * All defined users for the project, item type is User
	 */
	private List users = new ArrayList();

	/**
	 * All defined VcsLogins, item type is VcsLogin
	 */
	private List vcsLoginMapping = new ArrayList();

	/**
	 * All defined roles for the project, item type is Role
	 */
	private List roles = new ArrayList();

	/**
	 * Gets the project.
	 * 
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 * 
	 * @param project the project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Gets the list of all defined users for the project.
	 * 
	 * @return the list of users
	 * @see User
	 */
	public List getUsers() {
		return users;
	}

	/**
	 * Sets the list of users for the project.
	 * 
	 * @param users the list of users
	 * @see User
	 */
	public void setUsers(List users) {
		this.users = users;
	}

	/**
	 * Gets the list of VCS login mappings for the project.
	 * 
	 * @return the list of VCS login mappings
	 * @see VcsLogin
	 */
	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	/**
	 * Sets the list of VCS login mappings for the project.
	 * 
	 * @param vcsLoginMapping the list of VCS login mappings
	 * @see VcsLogin
	 */
	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	/**
	 * Gets the list of all defined roles for the project.
	 * 
	 * @return the list of roles
	 * @see Role
	 */
	public List getRoles() {
		return roles;
	}

	/**
	 * Sets the list of roles for the project.
	 * 
	 * @param roles the list of roles
	 * @see Role
	 */
	public void setRoles(List roles) {
		this.roles = roles;
	}

	/**
	 * Gets the list of all defined schedules for the project.
	 * 
	 * @return the list of schedules
	 * @see Schedule
	 */
	public List getSchedules() {
		return schedules;
	}

	/**
	 * Sets the list of schedules for the project.
	 * 
	 * @param schedules the list of schedules
	 * @see Schedule
	 */
	public void setSchedules(List schedules) {
		this.schedules = schedules;
	}

	/**
	 * Gets the list of all stored builds for the project.
	 * 
	 * @return the list of builds
	 * @see Build
	 */
	public List getBuilds() {
		return builds;
	}

	/**
	 * Sets the list of builds for the project.
	 * 
	 * @param builds the list of builds
	 * @see Build
	 */
	public void setBuilds(List builds) {
		this.builds = builds;
	}

	/**
	 * Gets the schedule with the specified name.
	 * 
	 * @param scheduleName the schedule name
	 * @return the schedule, or <code>null</code> if no schedule has that name
	 */
	public Schedule getSchedule(String scheduleName) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getName().equals(scheduleName))
				return schedule;
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
