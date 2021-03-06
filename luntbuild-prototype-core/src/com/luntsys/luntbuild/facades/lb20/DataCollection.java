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
package com.luntsys.luntbuild.facades.lb20;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents all data in this luntbuild system. Its content can be exported
 * to outer XML, and thus import to another system.
 * @author robin shine
 */
public class DataCollection {
	/**
	 * Version of this data collection, do not use final here, cause JVM compatibility issue
	 * may exist when doing reflection by xstream.
	 */
	private String version = "2.0";

	/**
	 * System properties
	 */
	private List properties = new ArrayList();

	/**
	 * All defined projects
	 */
	private List projects = new ArrayList();

	private List schedules = new ArrayList();

	private List builds = new ArrayList();

	/**
	 * All defined users
	 */
	private List users = new ArrayList();

	private List vcsLoginMapping = new ArrayList();

	private List rolesMapping = new ArrayList();

	private List notifyMapping = new ArrayList();

	/**
	 * All defined roles
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

	public String getVersion() {
		return version;
	}

	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	public List getRolesMapping() {
		return rolesMapping;
	}

	public void setRolesMapping(List rolesMapping) {
		this.rolesMapping = rolesMapping;
	}

	public List getNotifyMapping() {
		return notifyMapping;
	}

	public void setNotifyMapping(List notifyMapping) {
		this.notifyMapping = notifyMapping;
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

	public ProjectFacade getProject(String projectName) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			ProjectFacade projectFacade = (ProjectFacade) it.next();
			if (projectFacade.getName().equals(projectName))
				return projectFacade;
		}
		return null;
	}

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

	public ProjectFacade getProject(long projectId) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			ProjectFacade projectFacade = (ProjectFacade) it.next();
			if (projectFacade.getId() == projectId)
				return projectFacade;
		}
		return null;
	}

	public ScheduleFacade getSchedule(long scheduleId) {
		Iterator it = getSchedules().iterator();
		while (it.hasNext()) {
			ScheduleFacade scheduleFacade = (ScheduleFacade) it.next();
			if (scheduleFacade.getId() == scheduleId)
				return scheduleFacade;
		}
		return null;
	}

	public UserFacade getUser(long userId) {
		Iterator it = getUsers().iterator();
		while (it.hasNext()) {
			UserFacade userFacade = (UserFacade) it.next();
			if (userFacade.getId() == userId)
				return userFacade;
		}
		return null;
	}

	public RoleFacade getRole(long roleId) {
		Iterator it = getRoles().iterator();
		while (it.hasNext()) {
			RoleFacade roleFacade = (RoleFacade) it.next();
			if (roleFacade.getId() == roleId)
				return roleFacade;
		}
		return null;
	}

	public com.luntsys.luntbuild.facades.lb20.BuildFacade getBuild(long buildId) {
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			BuildFacade buildFacade = (BuildFacade) it.next();
			if (buildFacade.getId() == buildId)
				return buildFacade;
		}
		return null;
	}

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
