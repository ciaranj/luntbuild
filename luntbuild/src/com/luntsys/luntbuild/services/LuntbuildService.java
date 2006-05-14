/*
 * Copyright luntsys (c) 2004-2005,
 * Date: 2004-10-19
 * Time: 17:39:18
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
package com.luntsys.luntbuild.services;

import com.luntsys.luntbuild.remoting.*;
import com.luntsys.luntbuild.remoting.facade.BuildFacade;
import com.luntsys.luntbuild.remoting.facade.ProjectFacade;
import com.luntsys.luntbuild.remoting.facade.ScheduleFacade;
import com.luntsys.luntbuild.db.*;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.web.ManualBuildEditor;
import com.luntsys.luntbuild.remoting.SearchCriteria;
import org.quartz.SimpleTrigger;

import java.util.*;

/**
 * Implementation of interface {@link com.luntsys.luntbuild.remoting.ILuntbuild}
 * @author robin shine
 */
public class LuntbuildService implements ILuntbuild {
	public ProjectFacade getProjectByName(String projectName) {
		Iterator it = Luntbuild.getDao().loadProjects().iterator();
		while (it.hasNext()) {
			Project project = (Project) it.next();
			if (project.getName().equals(projectName)) {
				return project.getFacade();
			}
		}
		return null;
	}

	public ScheduleFacade getScheduleByName(ProjectFacade projectFacade, String scheduleName) {
		Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
		Iterator it = project.getSchedules().iterator();
		while (it.hasNext()) {
			Schedule schedule = (Schedule) it.next();
			if (schedule.getName().equals(scheduleName))
				return schedule.getFacade();
		}
		return null;
	}

	public void triggerBuild(ScheduleFacade scheduleFacade) {
		triggerBuild(scheduleFacade, null, -1, -1, -1, -1);
	}

	public void triggerBuild(String projectName, String scheduleName, String buildAsVersion,
							 int buildType, int postbuildStrategy, int labelStrategy, int notifyStrategy) {
		ProjectFacade projectFacade = getProjectByName(projectName);
		if (projectFacade == null)
			throw new LuntbuildException("Failed to find project with name \"" + projectName + "\"");
		ScheduleFacade scheduleFacade = getScheduleByName(projectFacade, scheduleName);
		if (scheduleFacade == null)
			throw new LuntbuildException("Failed to find schedule with name \"" + scheduleName +
					"\" inside project \"" + projectName + "\"");
		triggerBuild(scheduleFacade, buildAsVersion, buildType, postbuildStrategy, labelStrategy, notifyStrategy);
	}

	public void triggerBuild(String projectName, String scheduleName) {
		triggerBuild(projectName, scheduleName, null, -1, -1, -1, -1);
	}

	public void triggerBuild(ScheduleFacade scheduleFacade, String buildAsVersion, int buildType,
							 int postbuildStrategy, int labelStrategy, int notifyStrategy) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());

		SimpleTrigger trigger = new SimpleTrigger();
		trigger.setGroup(ManualBuildEditor.MANUALBUILD_GROUP);
		if (buildAsVersion == null)
			buildAsVersion = "";
		else
			Luntbuild.validateBuildVersion(buildAsVersion);

		if (buildType == -1) {
			buildType = schedule.getBuildType();
		} else
			Schedule.validateBuildType(buildType);

		if (postbuildStrategy == -1)
			postbuildStrategy = schedule.getPostbuildStrategy();
		else
			Schedule.validatePostbuildStrategy(postbuildStrategy);

		if (labelStrategy == -1)
			labelStrategy = schedule.getLabelStrategy();
		else
			Schedule.validateLabelStrategy(labelStrategy);

		if (notifyStrategy == -1)
			notifyStrategy = schedule.getNotifyStrategy();
		else
			Schedule.validateNotifyStrategy(notifyStrategy);

		trigger.setName(schedule.getId() + Luntbuild.TRIGGER_NAME_SEPERATOR +
				buildType + Luntbuild.TRIGGER_NAME_SEPERATOR + buildAsVersion +
				Luntbuild.TRIGGER_NAME_SEPERATOR + labelStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + notifyStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + postbuildStrategy +
				Luntbuild.TRIGGER_NAME_SEPERATOR + System.currentTimeMillis());
		trigger.setRepeatCount(0);
		trigger.setRepeatInterval(0);
		trigger.setStartTime(new Date(System.currentTimeMillis()));
		Luntbuild.getSchedService().scheduleBuild(schedule.getProject(), trigger);
	}

	public void saveProject(ProjectFacade projectFacade) {
		Project project = Luntbuild.getDao().loadProject(projectFacade.getId());
		project.setFacade(projectFacade);
		Luntbuild.getDao().saveProject(project);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public void saveSchedule(ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		schedule.setFacade(scheduleFacade);
		Luntbuild.getDao().saveSchedule(schedule);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public String getSystemProperty(String propertyName) {
		Map properties = Luntbuild.getDao().loadProperties();
		return (String) properties.get(propertyName);
	}

	public void setSystemProperty(String propertyName, String propertyValue) {
		Map properties = Luntbuild.getDao().loadProperties();
		if (propertyValue != null)
			properties.put(propertyName, propertyValue.trim());
		Luntbuild.getDao().saveProperties(properties);
		Luntbuild.getSchedService().rescheduleBuilds();
	}

	public List searchBuilds(SearchCriteria condition, int start,  int count) {
		List buildFacades = new ArrayList();
		Map properties = Luntbuild.getDao().loadProperties();
		Iterator it = Luntbuild.getDao().searchBuilds(condition, start, count).listIterator();
		while (it.hasNext()) {
			Build build = (Build) it.next();
			buildFacades.add(build.getFacade(properties));
		}
		return buildFacades;
	}

	public BuildFacade getLastBuild(ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		Build build = Luntbuild.getDao().loadLastBuild(schedule);
		if (build != null) {
			Map properties = Luntbuild.getDao().loadProperties();
			return build.getFacade(properties);
		} else
			return null;
	}

	public BuildFacade getLastSuccessBuild(ScheduleFacade scheduleFacade) {
		Schedule schedule = Luntbuild.getDao().loadSchedule(scheduleFacade.getId());
		Build build = Luntbuild.getDao().loadLastSuccessBuild(schedule);
		if (build != null) {
			Map properties = Luntbuild.getDao().loadProperties();
			return build.getFacade(properties);
		} else
			return null;
	}
}
