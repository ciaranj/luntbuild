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
 * Collection of facades related to a single Luntbuild project.
 * 
 * @author Jason Archer
 */
public class ProjectCollection {
	/**
	 * Version of this project collection, do not use final here, cause JVM compatibility issue
	 * may exist when doing reflection by XStream.
	 */
	private String version = "1.2";

	/** The project */
	private ProjectFacade project = new ProjectFacade();

	/** All schedules of the project */
	private List schedules = new ArrayList();

	/** All builds of the project */
	private List builds = new ArrayList();

	/** All users of the project */
	private List users = new ArrayList();

	/** All VCS logins of the project */
	private List vcsLoginMapping = new ArrayList();

	/** All role mappings of the project */
	private List rolesMapping = new ArrayList();

	/** All notify mappings of the project */
	private List notifyMapping = new ArrayList();

	/** All defined roles */
	private List roles = new ArrayList();

	/**
	 * Gets the version of this project collecton.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the facade of the project.
	 * 
	 * @return the project facade
	 */
	public ProjectFacade getProject() {
		return project;
	}

	/**
	 * Sets the facade of the project.
	 * 
	 * @param project the project facade
	 */
	public void setProject(ProjectFacade project) {
		this.project = project;
	}

	/**
	 * Gets the list of facades of the users who interact with the project.
	 * 
	 * @return the list of user facades
	 * @see UserFacade
	 */
	public List getUsers() {
		return users;
	}

	/**
	 * Sets the list of facades of the users who interact with the project.
	 * 
	 * @param users the list of user facades
	 * @see UserFacade
	 */
	public void setUsers(List users) {
		this.users = users;
	}

	/**
	 * Gets the list of facades for all possible roles.
	 * 
	 * @return the list of role facades
	 * @see RoleFacade
	 */
	public List getRoles() {
		return roles;
	}

	/**
	 * Sets the list of facades for all possible roles.
	 * 
	 * @param roles the list of role facades
	 * @see RoleFacade
	 */
	public void setRoles(List roles) {
		this.roles = roles;
	}

	/**
	 * Gets the list of facades of the VCS logins of the project.
	 * 
	 * @return the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public List getVcsLoginMapping() {
		return vcsLoginMapping;
	}

	/**
	 * Gets the list of facades of the VCS logins of the project.
	 * 
	 * @param vcsLoginMapping the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public void setVcsLoginMapping(List vcsLoginMapping) {
		this.vcsLoginMapping = vcsLoginMapping;
	}

	/**
	 * Gets the list of facades of the role mappings of the project.
	 * 
	 * @return the list of role mapping facades
	 * @see RolesMappingFacade
	 */
	public List getRolesMapping() {
		return rolesMapping;
	}

	/**
	 * Sets the list of facades of the role mappings of the project.
	 * 
	 * @param rolesMapping the list of role mapping facades
	 * @see RolesMappingFacade
	 */
	public void setRolesMapping(List rolesMapping) {
		this.rolesMapping = rolesMapping;
	}

	/**
	 * Gets the list of facades of the notify mappings of the project.
	 * 
	 * @return the list of notify mapping facades
	 * @see NotifyMappingFacade
	 */
	public List getNotifyMapping() {
		return notifyMapping;
	}

	/**
	 * Sets the list of facades of the notify mappings of the project.
	 * 
	 * @param notifyMapping the list of notify mapping facades
	 * @see NotifyMappingFacade
	 */
	public void setNotifyMapping(List notifyMapping) {
		this.notifyMapping = notifyMapping;
	}

	/**
	 * Gets the list of facades of the schedules of the project.
	 * 
	 * @return the list of schedule facades
	 * @see ScheduleFacade
	 */
	public List getSchedules() {
		return schedules;
	}

	/**
	 * Sets the list of facades of the schedules of the project.
	 * 
	 * @param schedules the list of schedule facades
	 * @see ScheduleFacade
	 */
	public void setSchedules(List schedules) {
		this.schedules = schedules;
	}

	/**
	 * Gets the list of facades of the builds of the project.
	 * 
	 * @return the list of build facades
	 * @see BuildFacade
	 */
	public List getBuilds() {
		return builds;
	}

	/**
	 * Sets the list of facades of the builds of the project.
	 * 
	 * @param builds the list of build facades
	 * @see BuildFacade
	 */
	public void setBuilds(List builds) {
		this.builds = builds;
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
	 * Gets the all the build facades for the schedule with the specified identifier.
	 * 
	 * @param scheduleId the schedule identifier
	 * @return the list of build facades
	 * @see BuildFacade
	 */
	public List getBuilds(long scheduleId) {
		List builds = new ArrayList();
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			BuildFacade buildFacade = (BuildFacade) it.next();
			if (buildFacade.getScheduleId() == scheduleId)
				builds.add(buildFacade);
		}
		return builds;
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
}
