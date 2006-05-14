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
 * This class represents all data in this luntbuild system. Its content can be exported
 * to outer XML, and thus import to another system.
 * @author robin shine
 */
public class DataCollection {
	/**
	 * Version of this data collection
	 */
	private final String version = "1.1.1";

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

	public String getVersion() {
		return version;
	}

	public List getViews() {
		return views;
	}

	public void setViews(List views) {
		this.views = views;
	}

	public List getBuildSchedules() {
		return buildSchedules;
	}

	public void setBuildSchedules(List buildSchedules) {
		this.buildSchedules = buildSchedules;
	}

	public List getVcsLoginConfig() {
		return vcsLoginConfig;
	}

	public void setVcsLoginConfig(List vcsLoginConfig) {
		this.vcsLoginConfig = vcsLoginConfig;
	}

	public List getViewCompositionConfig() {
		return viewCompositionConfig;
	}

	public void setViewCompositionConfig(List viewCompositionConfig) {
		this.viewCompositionConfig = viewCompositionConfig;
	}

	public List getNotificationConfig() {
		return notificationConfig;
	}

	public void setNotificationConfig(List notificationConfig) {
		this.notificationConfig = notificationConfig;
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

	public ProjectFacade getProject(long projectId) {
		Iterator it = getProjects().iterator();
		while (it.hasNext()) {
			ProjectFacade projectFacade = (ProjectFacade) it.next();
			if (projectFacade.getId() == projectId)
				return projectFacade;
		}
		return null;
	}

	public ViewFacade getView(long viewId) {
		Iterator it = getViews().iterator();
		while (it.hasNext()) {
			ViewFacade viewFacade = (ViewFacade) it.next();
			if (viewFacade.getId() == viewId)
				return viewFacade;
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

	public BuildScheduleFacade getBuildSchedule(long buildScheduleId) {
		Iterator it = getBuildSchedules().iterator();
		while (it.hasNext()) {
			BuildScheduleFacade buildScheduleFacade = (BuildScheduleFacade) it.next();
			if (buildScheduleFacade.getId() == buildScheduleId)
				return buildScheduleFacade;
		}
		return null;
	}

	public BuildFacade getBuild(long buildId) {
		Iterator it = getBuilds().iterator();
		while (it.hasNext()) {
			com.luntsys.luntbuild.facades.lb111.BuildFacade buildFacade = (BuildFacade) it.next();
			if (buildFacade.getId() == buildId)
				return buildFacade;
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
}
