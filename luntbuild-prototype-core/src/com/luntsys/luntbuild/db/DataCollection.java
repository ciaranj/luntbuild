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
 * This class represents data collection with relationships been setup
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

	public List getProperties() {
		return properties;
	}

	public void setProperties(List properties) {
		this.properties = properties;
	}

	public List getProjects() {
		return projects;
	}

	public void setProjects(List projects) {
		this.projects = projects;
	}

	public List getUsers() {
		return users;
	}

	public void setUsers(List users) {
		this.users = users;
	}

	public List getRoles() {
		return roles;
	}

	public void setRoles(List roles) {
		this.roles = roles;
	}

	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	public List getSchedules() {
		return schedules;
	}

	public void setSchedules(List schedules) {
		this.schedules = schedules;
	}

	public List getBuilds() {
		return builds;
	}

	public void setBuilds(List builds) {
		this.builds = builds;
	}

	public Project getProject(String projectName) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getName().equals(projectName))
				return project;
		}
		return null;
	}

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

	public Project getProject(long projectId) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getId() == projectId)
				return project;
		}
		return null;
	}

	public Schedule getSchedule(long scheduleId) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getId() == scheduleId)
				return schedule;
		}
		return null;
	}

	public User getUser(long userId) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			User user = (User) it.next();
			if (user.getId() == userId)
				return user;
		}
		return null;
	}

	public Role getRole(long roleId) {
		Iterator it = getRoles().iterator();
		while (it.hasNext()) {
			Role role = (Role) it.next();
			if (role.getId() == roleId)
				return role;
		}
		return null;
	}

	public Build getBuild(long buildId) {
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			if (build.getId() == buildId)
				return build;
		}
		return null;
	}

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
