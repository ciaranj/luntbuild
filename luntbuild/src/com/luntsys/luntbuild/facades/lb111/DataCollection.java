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

package com.luntsys.luntbuild.facades.lb111;

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
	private String version = "1.1.1";

	/**
	 * System properties
	 */
	private List properties = new ArrayList();

	/**
	 * All defined projects
	 */
	private List projects = new ArrayList();

	private List views = new ArrayList();

	private List schedules = new ArrayList();

	private List buildSchedules = new ArrayList();

	private List builds = new ArrayList();

	/**
	 * All defined users
	 */
	private List users = new ArrayList();

	private List vcsLoginConfig = new ArrayList();

	private List viewCompositionConfig = new ArrayList();

	private List notificationConfig = new ArrayList();

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
	 * Gets the list of facades of all defined views.
	 * 
	 * @return the list of view facades
	 * @see ViewFacade
	 */
	public List getViews() {
		return views;
	}

	/**
	 * Sets the list of facades of all defined views.
	 * 
	 * @param views the list of view facades
	 * @see ViewFacade
	 */
	public void setViews(List views) {
		this.views = views;
	}

	/**
	 * Gets the list of facades of all defined build schedules.
	 * 
	 * @return the list of build schedule facades
	 * @see BuildScheduleFacade
	 */
	public List getBuildSchedules() {
		return buildSchedules;
	}

	/**
	 * Sets the list of facades of all defined build schedules.
	 * 
	 * @param buildSchedules the list of build schedule facades
	 * @see BuildScheduleFacade
	 */
	public void setBuildSchedules(List buildSchedules) {
		this.buildSchedules = buildSchedules;
	}

	/**
	 * Gets the list of facades of all defined VCS logins.
	 * 
	 * @return the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public List getVcsLoginConfig() {
		return vcsLoginConfig;
	}

	/**
	 * Sets the list of facades of all defined VCS logins.
	 * 
	 * @param vcsLoginConfig the list of VCS login facades
	 * @see VcsLoginFacade
	 */
	public void setVcsLoginConfig(List vcsLoginConfig) {
		this.vcsLoginConfig = vcsLoginConfig;
	}

	/**
	 * Gets the list of facades of all defined view composition configs.
	 * 
	 * @return the list of view composition config facades
	 * @see ViewCompositionConfigFacade
	 */
	public List getViewCompositionConfig() {
		return viewCompositionConfig;
	}

	/**
	 * Sets the list of facades of all defined view composition configs.
	 * 
	 * @param viewCompositionConfig the list of view composition config facades
	 * @see ViewCompositionConfigFacade
	 */
	public void setViewCompositionConfig(List viewCompositionConfig) {
		this.viewCompositionConfig = viewCompositionConfig;
	}

	/**
	 * Gets the list of facades of all defined notification configs.
	 * 
	 * @return the list of notification config facades
	 * @see NotificationConfigFacade
	 */
	public List getNotificationConfig() {
		return notificationConfig;
	}

	/**
	 * Sets the list of facades of all defined notification configs.
	 * 
	 * @param notificationConfig the list of notification config facades
	 * @see NotificationConfigFacade
	 */
	public void setNotificationConfig(List notificationConfig) {
		this.notificationConfig = notificationConfig;
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
	 * Gets the view facade with the specified identifier.
	 * 
	 * @param viewId the view identifier
	 * @return the view facade, or <code>null</code> if no view has that identifier
	 */
	public ViewFacade getView(long viewId) {
		Iterator it = getViews().iterator();
		while (it.hasNext()) {
			ViewFacade viewFacade = (ViewFacade) it.next();
			if (viewFacade.getId() == viewId)
				return viewFacade;
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
	 * Gets the build schedule facade with the specified identifier.
	 * 
	 * @param buildScheduleId the build schedule identifier
	 * @return the build schedule facade, or <code>null</code> if no build schedule has that identifier
	 */
	public BuildScheduleFacade getBuildSchedule(long buildScheduleId) {
		Iterator it = getBuildSchedules().iterator();
		while (it.hasNext()) {
			BuildScheduleFacade buildScheduleFacade = (BuildScheduleFacade) it.next();
			if (buildScheduleFacade.getId() == buildScheduleId)
				return buildScheduleFacade;
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
}
